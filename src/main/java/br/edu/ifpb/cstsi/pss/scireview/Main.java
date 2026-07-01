package br.edu.ifpb.cstsi.pss.scireview;

import br.edu.ifpb.cstsi.pss.scireview.model.Artigo;
import br.edu.ifpb.cstsi.pss.scireview.model.Avaliacao;
import br.edu.ifpb.cstsi.pss.scireview.model.Papel;
import br.edu.ifpb.cstsi.pss.scireview.model.Revisao;
import br.edu.ifpb.cstsi.pss.scireview.model.Usuario;
import br.edu.ifpb.cstsi.pss.scireview.model.Veredito;
import br.edu.ifpb.cstsi.pss.scireview.model.categoria.FullPaper;
import br.edu.ifpb.cstsi.pss.scireview.service.CadastroAreaTematica;
import br.edu.ifpb.cstsi.pss.scireview.service.CadastroUsuario;
import br.edu.ifpb.cstsi.pss.scireview.service.ComiteTecnico;
import br.edu.ifpb.cstsi.pss.scireview.service.GerenciadorEvento;
import br.edu.ifpb.cstsi.pss.scireview.service.ServicoEmail;
import br.edu.ifpb.cstsi.pss.scireview.service.SistemaAvaliacao;
import br.edu.ifpb.cstsi.pss.scireview.service.SubmissaoArtigo;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        GerenciadorEvento gerenciadorEvento = new GerenciadorEvento();
        CadastroUsuario cadastroUsuario = new CadastroUsuario();
        SistemaAvaliacao sistemaAvaliacao = new SistemaAvaliacao();
        SubmissaoArtigo submissaoArtigo = new SubmissaoArtigo(gerenciadorEvento);
        ComiteTecnico comiteTecnico = new ComiteTecnico();
        CadastroAreaTematica cadastroArea = new CadastroAreaTematica();

        ServicoEmail servicoEmail = new ServicoEmail(gerenciadorEvento, cadastroUsuario, sistemaAvaliacao);
        gerenciadorEvento.adicionarObserver(servicoEmail);

        // RF01 - inicia o evento com uma janela de submissão que engloba a data atual
        LocalDate hoje = LocalDate.now();
        gerenciadorEvento.startNovoEvento(
                "Simpósio Brasileiro de Sistemas de Informação - 2026",
                "Vitória - ES",
                "25 de maio a 28 de maio de 2026",
                hoje.minusDays(30),
                hoje.plusDays(30)
        );

        // RF02 - cadastro de usuários
        Usuario coordenador = cadastroUsuario.cadastrar(
                "coordenador@evento.com", "senha123", "IFPB", Set.of(Papel.COORDENADOR));
        Usuario autor1 = cadastroUsuario.cadastrar(
                "autor1@email.com", "senha123", "UFPB", Set.of(Papel.AUTOR));
        Usuario revisor1 = cadastroUsuario.cadastrar(
                "revisor1@email.com", "senha123", "UFCG", Set.of(Papel.REVISOR));
        Usuario revisor2 = cadastroUsuario.cadastrar(
                "revisor2@email.com", "senha123", "UFRN", Set.of(Papel.REVISOR));

        // RF04 - o coordenador define a categoria (Strategy) e monta o comitê técnico
        gerenciadorEvento.definirCategoria(coordenador, new FullPaper());
        comiteTecnico.registrarRevisor(coordenador, revisor1);
        comiteTecnico.registrarRevisor(coordenador, revisor2);

        // RF03 - áreas temáticas e afinidade dos revisores
        cadastroArea.cadastrar(coordenador, "Inteligência Artificial");
        cadastroArea.cadastrar(coordenador, "Machine Learning");
        cadastroArea.associarRevisor(revisor1, "Inteligência Artificial");
        cadastroArea.associarRevisor(revisor2, "Machine Learning");

        // RF05 - submissão de artigos (o serviço gera o ID e associa o evento ativo)
        Artigo artigo1 = submissaoArtigo.submeter(
                autor1, "IA Aplicada à Saúde", "Resumo sobre IA na saúde...", List.of("Maria Silva"));
        Artigo artigo2 = submissaoArtigo.submeter(
                autor1, "Machine Learning em Dados", "Resumo sobre ML...", List.of("João Souza"));
        sistemaAvaliacao.adicionarArtigo(artigo1);
        sistemaAvaliacao.adicionarArtigo(artigo2);

        // RF06 - distribuição dos artigos aos revisores
        Revisao rev1 = new Revisao(artigo1, revisor1);
        Revisao rev2 = new Revisao(artigo2, revisor2);
        sistemaAvaliacao.adicionarRevisao(rev1);
        sistemaAvaliacao.adicionarRevisao(rev2);

        // RF07 - transições de estado (State): submetido -> em revisão
        artigo1.enviarParaRevisao();
        artigo2.enviarParaRevisao();

        rev1.setAvaliacao(new Avaliacao(
                "Ótimo trabalho, contribuição significativa",
                "Alguns pontos de melhoria na metodologia",
                Veredito.ACEITO));
        rev2.setAvaliacao(new Avaliacao(
                "Boa proposta, mas precisa de mais dados",
                "Amostra pequena, falta validação",
                Veredito.FRACAMENTE_RECUSADO));

        // em revisão -> aceito / rejeitado
        artigo1.aceitar();
        artigo2.rejeitar();

        // RF09 - o Observer dispara a notificação em massa aos autores
        gerenciadorEvento.finalizarCicloRevisoes();
    }
}
