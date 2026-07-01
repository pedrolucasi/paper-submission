package br.edu.ifpb.cstsi.pss.scireview;

import br.edu.ifpb.cstsi.pss.scireview.dashboard.Dashboard;
import br.edu.ifpb.cstsi.pss.scireview.model.Artigo;
import br.edu.ifpb.cstsi.pss.scireview.model.AreaTematica;
import br.edu.ifpb.cstsi.pss.scireview.model.Avaliacao;
import br.edu.ifpb.cstsi.pss.scireview.model.Evento;
import br.edu.ifpb.cstsi.pss.scireview.model.Papel;
import br.edu.ifpb.cstsi.pss.scireview.model.Revisao;
import br.edu.ifpb.cstsi.pss.scireview.model.Usuario;
import br.edu.ifpb.cstsi.pss.scireview.model.Veredito;
import br.edu.ifpb.cstsi.pss.scireview.service.CadastroAreaTematica;
import br.edu.ifpb.cstsi.pss.scireview.service.CadastroUsuario;
import br.edu.ifpb.cstsi.pss.scireview.service.DistribuicaoRevisores;
import br.edu.ifpb.cstsi.pss.scireview.service.GerenciadorEvento;
import br.edu.ifpb.cstsi.pss.scireview.service.ServicoEmail;
import br.edu.ifpb.cstsi.pss.scireview.service.SistemaAvaliacao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Main {

    public static void main(String[] args) {
        System.out.println("SciReview - Sistema de Submissão de Artigos");
        System.out.println("============================================\n");

        GerenciadorEvento gerenciadorEvento = new GerenciadorEvento();
        CadastroUsuario cadastroUsuario = new CadastroUsuario();
        SistemaAvaliacao sistemaAvaliacao = new SistemaAvaliacao();
        CadastroAreaTematica cadastroAreaTematica = new CadastroAreaTematica();

        ServicoEmail servicoEmail = new ServicoEmail(gerenciadorEvento, cadastroUsuario, sistemaAvaliacao);
        gerenciadorEvento.adicionarObserver(servicoEmail);

        Dashboard dashboard = new Dashboard(sistemaAvaliacao, cadastroUsuario);
        DistribuicaoRevisores distribuicao = new DistribuicaoRevisores(sistemaAvaliacao, cadastroAreaTematica);

        LocalDate hoje = LocalDate.now();
        gerenciadorEvento.startNovoEvento(
                "Simpósio Brasileiro de Sistemas de Informação - 2026",
                "Vitória - ES",
                "25 de maio a 28 de maio de 2026",
                hoje.minusDays(10),
                hoje.plusDays(10)
        );

        Usuario coordenador = cadastroUsuario.cadastrar(
                "coordenador@evento.com",
                "senha123",
                "IFPB",
                Set.of(Papel.COORDENADOR)
        );

        System.out.println("📝 CADASTRANDO USUÁRIOS...");

        Usuario autor1 = cadastroUsuario.cadastrar(
                "autor1@email.com",
                "senha123",
                "UFPB",
                Set.of(Papel.AUTOR)
        );

        Usuario autor2 = cadastroUsuario.cadastrar(
                "autor2@email.com",
                "senha123",
                "UFCG",
                Set.of(Papel.AUTOR)
        );

        List<Usuario> revisores = new ArrayList<>();
        revisores.add(cadastroUsuario.cadastrar(
                "revisor1@email.com",
                "senha123",
                "USP",
                Set.of(Papel.REVISOR)
        ));
        revisores.add(cadastroUsuario.cadastrar(
                "revisor2@email.com",
                "senha123",
                "UNICAMP",
                Set.of(Papel.REVISOR)
        ));
        revisores.add(cadastroUsuario.cadastrar(
                "revisor3@email.com",
                "senha123",
                "UFRJ",
                Set.of(Papel.REVISOR)
        ));

        System.out.println("\n🏷️ CADASTRANDO ÁREAS TEMÁTICAS...");

        AreaTematica ia = cadastroAreaTematica.cadastrar(coordenador, "Inteligência Artificial");
        AreaTematica ml = cadastroAreaTematica.cadastrar(coordenador, "Machine Learning");
        AreaTematica visao = cadastroAreaTematica.cadastrar(coordenador, "Visão Computacional");
        AreaTematica dados = cadastroAreaTematica.cadastrar(coordenador, "Ciência de Dados");
        AreaTematica saude = cadastroAreaTematica.cadastrar(coordenador, "Informática na Saúde");

        System.out.println("\n🔗 ASSOCIANDO ÁREAS AOS REVISORES...");

        cadastroAreaTematica.associarRevisor(revisores.get(0), "Inteligência Artificial");
        cadastroAreaTematica.associarRevisor(revisores.get(0), "Machine Learning");
        cadastroAreaTematica.associarRevisor(revisores.get(0), "Ciência de Dados");

        cadastroAreaTematica.associarRevisor(revisores.get(1), "Visão Computacional");
        cadastroAreaTematica.associarRevisor(revisores.get(1), "Inteligência Artificial");

        cadastroAreaTematica.associarRevisor(revisores.get(2), "Ciência de Dados");
        cadastroAreaTematica.associarRevisor(revisores.get(2), "Informática na Saúde");

        for (Usuario revisor : revisores) {
            System.out.println("   👤 " + revisor.getEmail() + ": " + revisor.getAreasDeInteresse());
        }

        System.out.println("\n📄 SUBMETENDO ARTIGOS...");

        List<Artigo> artigos = new ArrayList<>();

        Artigo artigo1 = new Artigo(
                UUID.randomUUID().toString(),
                "IA Aplicada à Saúde com Machine Learning",
                "Este artigo explora aplicações de Inteligência Artificial e Machine Learning na área da saúde...",
                List.of("coautor1@email.com"),
                autor1
        );
        artigos.add(artigo1);
        sistemaAvaliacao.adicionarArtigo(artigo1);

        Artigo artigo2 = new Artigo(
                UUID.randomUUID().toString(),
                "Visão Computacional para Diagnóstico por Imagem",
                "Utilização de técnicas de Visão Computacional para auxiliar no diagnóstico médico...",
                List.of("coautor2@email.com"),
                autor1
        );
        artigos.add(artigo2);
        sistemaAvaliacao.adicionarArtigo(artigo2);

        Artigo artigo3 = new Artigo(
                UUID.randomUUID().toString(),
                "Análise de Dados em Sistemas de Saúde",
                "Aplicação de Ciência de Dados para análise de prontuários eletrônicos...",
                List.of("coautor3@email.com"),
                autor2
        );
        artigos.add(artigo3);
        sistemaAvaliacao.adicionarArtigo(artigo3);

        Artigo artigo4 = new Artigo(
                UUID.randomUUID().toString(),
                "Deep Learning para Processamento de Linguagem Natural",
                "Uso de Deep Learning para processamento de linguagem natural em textos médicos...",
                List.of("coautor4@email.com"),
                autor2
        );
        artigos.add(artigo4);
        sistemaAvaliacao.adicionarArtigo(artigo4);

        for (Artigo artigo : artigos) {
            System.out.println("   📄 " + artigo.getTitulo() + " (ID: " + artigo.getId() + ")");
            System.out.println("      Autor: " + artigo.getAutor().getEmail());
            System.out.println("      Coautores: " + artigo.getCoautores());
        }

        System.out.println("\n📤 DISTRIBUINDO ARTIGOS PARA REVISORES...");

        distribuicao.distribuirAutomaticamente(artigos, revisores);

        System.out.println("\n📊 DASHBOARD APÓS DISTRIBUIÇÃO:");
        dashboard.exibirDashboard();

        System.out.println("\n📧 FINALIZANDO CICLO DE AVALIAÇÕES...");
        gerenciadorEvento.finalizarCicloRevisoes();

        System.out.println("\n✅ PROGRAMA FINALIZADO!");
    }
}