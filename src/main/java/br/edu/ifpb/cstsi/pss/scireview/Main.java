package br.edu.ifpb.cstsi.pss.scireview;

import br.edu.ifpb.cstsi.pss.scireview.model.Artigo;
import br.edu.ifpb.cstsi.pss.scireview.model.Avaliacao;
import br.edu.ifpb.cstsi.pss.scireview.model.Evento;
import br.edu.ifpb.cstsi.pss.scireview.model.Papel;
import br.edu.ifpb.cstsi.pss.scireview.model.Revisao;
import br.edu.ifpb.cstsi.pss.scireview.model.StatusArtigo;
import br.edu.ifpb.cstsi.pss.scireview.model.Usuario;
import br.edu.ifpb.cstsi.pss.scireview.model.Veredito;
import br.edu.ifpb.cstsi.pss.scireview.service.CadastroAreaTematica;
import br.edu.ifpb.cstsi.pss.scireview.service.CadastroUsuario;
import br.edu.ifpb.cstsi.pss.scireview.service.GerenciadorEvento;
import br.edu.ifpb.cstsi.pss.scireview.service.ServicoEmail;
import br.edu.ifpb.cstsi.pss.scireview.service.SistemaAvaliacao;

import java.util.Set;

public class Main {

    public static void main(String[] args) {
        System.out.println("SciReview - Sistema de Submissão de Artigos");
        System.out.println("============================================\n");

        GerenciadorEvento gerenciadorEvento = new GerenciadorEvento();
        CadastroUsuario cadastroUsuario = new CadastroUsuario();
        SistemaAvaliacao sistemaAvaliacao = new SistemaAvaliacao();

        ServicoEmail servicoEmail = new ServicoEmail(gerenciadorEvento, cadastroUsuario, sistemaAvaliacao);
        gerenciadorEvento.adicionarObserver(servicoEmail);

        Evento evento = gerenciadorEvento.startNovoEvento(
                "Simpósio Brasileiro de Sistemas de Informação - 2026",
                "Vitória - ES",
                "25 de maio a 28 de maio de 2026"
        );

        cadastroUsuario.cadastrar(
                "coordenador@evento.com",
                "senha123",
                "IFPB",
                Set.of(Papel.COORDENADOR)
        );

        Usuario autor1 = cadastroUsuario.cadastrar(
                "autor1@email.com",
                "senha123",
                "UFPB",
                Set.of(Papel.AUTOR)
        );

        Usuario revisor1 = cadastroUsuario.cadastrar(
                "revisor1@email.com",
                "senha123",
                "UFCG",
                Set.of(Papel.REVISOR)
        );

        Usuario revisor2 = cadastroUsuario.cadastrar(
                "revisor2@email.com",
                "senha123",
                "UFRN",
                Set.of(Papel.REVISOR)
        );

        CadastroAreaTematica cadastroArea = new CadastroAreaTematica();
        cadastroArea.cadastrar(cadastroUsuario.buscarPorEmail("coordenador@evento.com").get(), "Inteligência Artificial");
        cadastroArea.cadastrar(cadastroUsuario.buscarPorEmail("coordenador@evento.com").get(), "Machine Learning");

        cadastroArea.associarRevisor(revisor1, "Inteligência Artificial");
        cadastroArea.associarRevisor(revisor2, "Machine Learning");

        Artigo artigo1 = new Artigo(
                "IA Aplicada à Saúde",
                "Resumo sobre IA na saúde...",
                autor1.getEmail(),
                "Full Paper",
                evento
        );
        sistemaAvaliacao.adicionarArtigo(artigo1);

        Artigo artigo2 = new Artigo(
                "Machine Learning em Dados",
                "Resumo sobre ML...",
                autor1.getEmail(),
                "Short Paper",
                evento
        );
        sistemaAvaliacao.adicionarArtigo(artigo2);

        Revisao rev1 = new Revisao(artigo1, revisor1);
        Revisao rev2 = new Revisao(artigo2, revisor2);
        sistemaAvaliacao.adicionarRevisao(rev1);
        sistemaAvaliacao.adicionarRevisao(rev2);

        artigo1.setStatus(StatusArtigo.REVISAO);
        artigo2.setStatus(StatusArtigo.REVISAO);

        Avaliacao aval1 = new Avaliacao(
                "Ótimo trabalho, contribuição significativa",
                "Alguns pontos de melhoria na metodologia",
                Veredito.ACEITO
        );
        rev1.setAvaliacao(aval1);

        Avaliacao aval2 = new Avaliacao(
                "Boa proposta, mas precisa de mais dados",
                "Amostra pequena, falta validação",
                Veredito.FRACAMENTE_RECUSADO
        );
        rev2.setAvaliacao(aval2);

        artigo1.setStatus(StatusArtigo.ACEITO);
        artigo2.setStatus(StatusArtigo.REJEITADO);

        System.out.println("\n=== FINALIZANDO CICLO DE AVALIAÇÕES ===");
        gerenciadorEvento.finalizarCicloRevisoes();

        System.out.println("\n=== PROGRAMA FINALIZADO ===");
    }
}