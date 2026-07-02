package br.edu.ifpb.cstsi.pss.scireview.service;

import br.edu.ifpb.cstsi.pss.scireview.exception.AcessoNaoAutorizadoException;
import br.edu.ifpb.cstsi.pss.scireview.exception.DadosInvalidosException;
import br.edu.ifpb.cstsi.pss.scireview.exception.ParecerJaEmitidoException;
import br.edu.ifpb.cstsi.pss.scireview.model.Artigo;
import br.edu.ifpb.cstsi.pss.scireview.model.Avaliacao;
import br.edu.ifpb.cstsi.pss.scireview.model.Papel;
import br.edu.ifpb.cstsi.pss.scireview.model.Revisao;
import br.edu.ifpb.cstsi.pss.scireview.model.Usuario;
import br.edu.ifpb.cstsi.pss.scireview.model.Veredito;
import br.edu.ifpb.cstsi.pss.scireview.model.estado.StatusArtigo;

import java.util.List;

public class RevisaoArtigo {

    private final SistemaAvaliacao sistemaAvaliacao;

    public RevisaoArtigo(SistemaAvaliacao sistemaAvaliacao) {
        this.sistemaAvaliacao = sistemaAvaliacao;
    }

    public RevisaoArtigo(SistemaAvaliacao sistemaAvaliacao, GerenciadorEvento gerenciadorEvento) {
        this.sistemaAvaliacao = sistemaAvaliacao;
        gerenciadorEvento.aoLimparEstado(sistemaAvaliacao::limpar);
    }

    public List<Artigo> listarArtigosPendentes(Usuario revisor) {
        validarRevisor(revisor);
        return sistemaAvaliacao.listarRevisoesPendentesDoRevisor(revisor).stream()
                .map(Revisao::getArtigo)
                .toList();
    }

    public Revisao emitirParecer(
            Usuario revisor,
            String artigoId,
            String contribuicoes,
            String pontosCritica,
            Veredito veredito
    ) {
        validarRevisor(revisor);

        Artigo artigo = sistemaAvaliacao.buscarArtigoPorId(artigoId)
                .orElseThrow(() -> new DadosInvalidosException("Artigo não encontrado: " + artigoId));

        Revisao revisao = sistemaAvaliacao.buscarRevisao(artigo, revisor)
                .orElseThrow(() -> new AcessoNaoAutorizadoException(
                        "Artigo não está atribuído a este revisor."));

        if (revisao.isConcluida()) {
            throw new ParecerJaEmitidoException();
        }

        if (artigo.getStatus() != StatusArtigo.EM_REVISAO) {
            throw new DadosInvalidosException("Artigo não está em revisão.");
        }

        Avaliacao parecer = new Avaliacao(contribuicoes, pontosCritica, veredito);
        revisao.setAvaliacao(parecer);

        boolean todasConcluidas = sistemaAvaliacao.getRevisoesPorArtigo(artigo).stream()
                .allMatch(Revisao::isConcluida);
        if (todasConcluidas) {
            artigo.concluirRevisao();
        }
        return revisao;
    }

    private void validarRevisor(Usuario revisor) {
        if (revisor == null || !revisor.possuiPapel(Papel.REVISOR)) {
            throw new AcessoNaoAutorizadoException(
                    "Apenas usuários com o papel de revisor podem consultar pendências ou emitir parecer.");
        }
    }
}
