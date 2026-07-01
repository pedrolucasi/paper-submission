package br.edu.ifpb.cstsi.pss.scireview;

import br.edu.ifpb.cstsi.pss.scireview.dashboard.Dashboard;
import br.edu.ifpb.cstsi.pss.scireview.model.Artigo;
import br.edu.ifpb.cstsi.pss.scireview.model.Avaliacao;
import br.edu.ifpb.cstsi.pss.scireview.model.Evento;
import br.edu.ifpb.cstsi.pss.scireview.model.Papel;
import br.edu.ifpb.cstsi.pss.scireview.model.Revisao;
import br.edu.ifpb.cstsi.pss.scireview.model.Usuario;
import br.edu.ifpb.cstsi.pss.scireview.model.Veredito;
import br.edu.ifpb.cstsi.pss.scireview.model.estado.StatusArtigo;
import br.edu.ifpb.cstsi.pss.scireview.service.CadastroAreaTematica;
import br.edu.ifpb.cstsi.pss.scireview.service.CadastroUsuario;
import br.edu.ifpb.cstsi.pss.scireview.service.GerenciadorEvento;
import br.edu.ifpb.cstsi.pss.scireview.service.ServicoEmail;
import br.edu.ifpb.cstsi.pss.scireview.service.SistemaAvaliacao;

import java.time.LocalDate;
import java.util.List;
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

        Dashboard dashboard = new Dashboard(sistemaAvaliacao, cadastroUsuario);

        LocalDate hoje = LocalDate.now();
        Evento evento = gerenciadorEvento.startNovoEvento(
                "Simpósio Brasileiro de Sistemas de Informação - 2026",
                "Vitória - ES",
                "25 de maio a 28 de maio de 2026",
                hoje.minusDays(10),
                hoje.plusDays(10)
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
                UUID.randomUUID().toString(),
                "IA Aplicada à Saúde",
                "Resumo sobre IA na saúde...",
                List.of("coautor1@email.com"),
                autor1
        );
        sistemaAvaliacao.adicionarArtigo(artigo1);

        Artigo artigo2 = new Artigo(
                UUID.randomUUID().toString(),
                "Machine Learning em Dados",
                "Resumo sobre ML...",
                List.of("coautor2@email.com"),
                autor1
        );
        sistemaAvaliacao.adicionarArtigo(artigo2);

        Revisao rev1 = new Revisao(artigo1, revisor1);
        Revisao rev2 = new Revisao(artigo2, revisor2);
        sistemaAvaliacao.adicionarRevisao(rev1);
        sistemaAvaliacao.adicionarRevisao(rev2);

        artigo1.enviarParaRevisao();
        artigo2.enviarParaRevisao();

        Avaliacao aval1 = new Avaliacao(
                "Ótimo trabalho, contribuição significativa",
                "Alguns pontos de melhoria na metodologia",
                Veredito.ACEITO
        );
        rev1.setAvaliacao(aval1);

        artigo1.aceitar();

        System.out.println("\n=== EXIBINDO DASHBOARD ===");
        dashboard.exibirDashboard();

        System.out.println("\n=== FINALIZANDO CICLO DE AVALIAÇÕES ===");
        gerenciadorEvento.finalizarCicloRevisoes();

        System.out.println("\n=== PROGRAMA FINALIZADO ===");
    }
}