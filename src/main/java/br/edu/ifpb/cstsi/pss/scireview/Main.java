package br.edu.ifpb.cstsi.pss.scireview;

import br.edu.ifpb.cstsi.pss.scireview.command.CadastrarAreaCommand;
import br.edu.ifpb.cstsi.pss.scireview.command.CommandHistory;
import br.edu.ifpb.cstsi.pss.scireview.command.DefinirCategoriaCommand;
import br.edu.ifpb.cstsi.pss.scireview.command.DistribuirArtigosCommand;
import br.edu.ifpb.cstsi.pss.scireview.command.FinalizarCicloCommand;
import br.edu.ifpb.cstsi.pss.scireview.command.IniciarEventoCommand;
import br.edu.ifpb.cstsi.pss.scireview.command.RegistrarRevisorCommand;
import br.edu.ifpb.cstsi.pss.scireview.command.SubmeterArtigoCommand;
import br.edu.ifpb.cstsi.pss.scireview.dashboard.Dashboard;
import br.edu.ifpb.cstsi.pss.scireview.model.Artigo;
import br.edu.ifpb.cstsi.pss.scireview.model.AreaTematica;
import br.edu.ifpb.cstsi.pss.scireview.model.Avaliacao;
import br.edu.ifpb.cstsi.pss.scireview.model.Papel;
import br.edu.ifpb.cstsi.pss.scireview.model.Revisao;
import br.edu.ifpb.cstsi.pss.scireview.model.Usuario;
import br.edu.ifpb.cstsi.pss.scireview.model.Veredito;
import br.edu.ifpb.cstsi.pss.scireview.model.categoria.FullPaper;
import br.edu.ifpb.cstsi.pss.scireview.service.CadastroAreaTematica;
import br.edu.ifpb.cstsi.pss.scireview.service.CadastroUsuario;
import br.edu.ifpb.cstsi.pss.scireview.service.ComiteTecnico;
import br.edu.ifpb.cstsi.pss.scireview.service.DistribuicaoRevisores;
import br.edu.ifpb.cstsi.pss.scireview.service.GerenciadorEvento;
import br.edu.ifpb.cstsi.pss.scireview.service.ServicoEmail;
import br.edu.ifpb.cstsi.pss.scireview.service.SistemaAvaliacao;
import br.edu.ifpb.cstsi.pss.scireview.service.SubmissaoArtigo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        System.out.println("SciReview - Sistema de Submissao de Artigos");
        System.out.println("============================================\n");

        CommandHistory historico = CommandHistory.getInstance();

        GerenciadorEvento gerenciadorEvento = new GerenciadorEvento();
        CadastroUsuario cadastroUsuario = new CadastroUsuario();
        SistemaAvaliacao sistemaAvaliacao = new SistemaAvaliacao();
        SubmissaoArtigo submissaoArtigo = new SubmissaoArtigo(gerenciadorEvento);
        ComiteTecnico comiteTecnico = new ComiteTecnico();
        CadastroAreaTematica cadastroAreaTematica = new CadastroAreaTematica();

        ServicoEmail servicoEmail = new ServicoEmail(gerenciadorEvento, cadastroUsuario, sistemaAvaliacao);
        gerenciadorEvento.adicionarObserver(servicoEmail);

        Dashboard dashboard = new Dashboard(sistemaAvaliacao, cadastroUsuario);
        DistribuicaoRevisores distribuicao = new DistribuicaoRevisores(sistemaAvaliacao, cadastroAreaTematica);

        // RF02 - Cadastro de usuarios
        Usuario coordenador = cadastroUsuario.cadastrar(
                "coordenador@evento.com", "senha123", "IFPB", Set.of(Papel.COORDENADOR));
        Usuario autor1 = cadastroUsuario.cadastrar(
                "autor1@email.com", "senha123", "UFPB", Set.of(Papel.AUTOR));
        Usuario autor2 = cadastroUsuario.cadastrar(
                "autor2@email.com", "senha123", "UFCG", Set.of(Papel.AUTOR));

        List<Usuario> revisores = new ArrayList<>();
        revisores.add(cadastroUsuario.cadastrar(
                "revisor1@email.com", "senha123", "USP", Set.of(Papel.REVISOR)));
        revisores.add(cadastroUsuario.cadastrar(
                "revisor2@email.com", "senha123", "UNICAMP", Set.of(Papel.REVISOR)));
        revisores.add(cadastroUsuario.cadastrar(
                "revisor3@email.com", "senha123", "UFRJ", Set.of(Papel.REVISOR)));

        // RF01 - Command: Iniciar Evento
        LocalDate hoje = LocalDate.now();
        IniciarEventoCommand iniciarEvento = new IniciarEventoCommand(
                gerenciadorEvento, coordenador,
                "Simposio Brasileiro de Sistemas de Informacao - 2026",
                "Vitoria - ES",
                "25 de maio a 28 de maio de 2026",
                hoje.minusDays(30),
                hoje.plusDays(30)
        );
        iniciarEvento.executar();

        // RF04 - Command: Definir Categoria
        DefinirCategoriaCommand definirCategoria = new DefinirCategoriaCommand(
                gerenciadorEvento, coordenador, new FullPaper());
        definirCategoria.executar();

        // RF03 - Command: Cadastrar Areas
        CadastrarAreaCommand areaIA = new CadastrarAreaCommand(
                cadastroAreaTematica, coordenador, "Inteligencia Artificial");
        areaIA.executar();

        CadastrarAreaCommand areaML = new CadastrarAreaCommand(
                cadastroAreaTematica, coordenador, "Machine Learning");
        areaML.executar();

        CadastrarAreaCommand areaVisao = new CadastrarAreaCommand(
                cadastroAreaTematica, coordenador, "Visao Computacional");
        areaVisao.executar();

        CadastrarAreaCommand areaDados = new CadastrarAreaCommand(
                cadastroAreaTematica, coordenador, "Ciencia de Dados");
        areaDados.executar();

        CadastrarAreaCommand areaSaude = new CadastrarAreaCommand(
                cadastroAreaTematica, coordenador, "Informatica na Saude");
        areaSaude.executar();

        // Associar areas aos revisores
        cadastroAreaTematica.associarRevisor(revisores.get(0), "Inteligencia Artificial");
        cadastroAreaTematica.associarRevisor(revisores.get(0), "Machine Learning");
        cadastroAreaTematica.associarRevisor(revisores.get(0), "Ciencia de Dados");
        cadastroAreaTematica.associarRevisor(revisores.get(1), "Visao Computacional");
        cadastroAreaTematica.associarRevisor(revisores.get(1), "Inteligencia Artificial");
        cadastroAreaTematica.associarRevisor(revisores.get(2), "Ciencia de Dados");
        cadastroAreaTematica.associarRevisor(revisores.get(2), "Informatica na Saude");

        // RF04 - Command: Registrar Revisores
        for (Usuario revisor : revisores) {
            RegistrarRevisorCommand registrarRevisor = new RegistrarRevisorCommand(
                    comiteTecnico, coordenador, revisor);
            registrarRevisor.executar();
        }

        // RF05 - Command: Submeter Artigos
        List<Artigo> artigos = new ArrayList<>();

        SubmeterArtigoCommand submeter1 = new SubmeterArtigoCommand(
                submissaoArtigo, autor1,
                "IA Aplicada a Saude com Machine Learning",
                "Este artigo explora aplicacoes de Inteligencia Artificial e Machine Learning na area da saude...",
                List.of("coautor1@email.com")
        );
        submeter1.executar();
        artigos.add(submissaoArtigo.listarArtigosDoAutor(autor1).get(0));

        SubmeterArtigoCommand submeter2 = new SubmeterArtigoCommand(
                submissaoArtigo, autor1,
                "Visao Computacional para Diagnostico por Imagem",
                "Utilizacao de tecnicas de Visao Computacional para auxiliar no diagnostico medico...",
                List.of("coautor2@email.com")
        );
        submeter2.executar();
        artigos.add(submissaoArtigo.listarArtigosDoAutor(autor1).get(1));

        SubmeterArtigoCommand submeter3 = new SubmeterArtigoCommand(
                submissaoArtigo, autor2,
                "Analise de Dados em Sistemas de Saude",
                "Aplicacao de Ciencia de Dados para analise de prontuarios eletronicos...",
                List.of("coautor3@email.com")
        );
        submeter3.executar();
        artigos.add(submissaoArtigo.listarArtigosDoAutor(autor2).get(0));

        SubmeterArtigoCommand submeter4 = new SubmeterArtigoCommand(
                submissaoArtigo, autor2,
                "Deep Learning para Processamento de Linguagem Natural",
                "Uso de Deep Learning para processamento de linguagem natural em textos medicos...",
                List.of("coautor4@email.com")
        );
        submeter4.executar();
        artigos.add(submissaoArtigo.listarArtigosDoAutor(autor2).get(1));

        // Colocar artigos em revisao
        for (Artigo artigo : artigos) {
            artigo.enviarParaRevisao();
            sistemaAvaliacao.adicionarArtigo(artigo);
        }

        // RF06 - Command: Distribuir Artigos
        DistribuirArtigosCommand distribuir = new DistribuirArtigosCommand(
                distribuicao, artigos, revisores, coordenador);
        distribuir.executar();

        // RF07 - Realizar avaliacoes
        for (Revisao revisao : sistemaAvaliacao.getTodasRevisoes()) {
            if (revisao.getArtigo().getTitulo().contains("Saude") ||
                revisao.getArtigo().getTitulo().contains("Diagnostico")) {
                revisao.setAvaliacao(new Avaliacao(
                        "Excelente contribuicao para a area",
                        "Poderia melhorar a metodologia",
                        Veredito.ACEITO
                ));
                revisao.getArtigo().aceitar();
            } else {
                revisao.setAvaliacao(new Avaliacao(
                        "Boa proposta, mas com limitacoes",
                        "Amostra pequena, faltam dados",
                        Veredito.FRACAMENTE_RECUSADO
                ));
                revisao.getArtigo().rejeitar();
            }
        }

        // RF09 - Command: Finalizar Ciclo
        FinalizarCicloCommand finalizar = new FinalizarCicloCommand(gerenciadorEvento, coordenador);
        finalizar.executar();

        // RF10 - Exibir Historico de Auditoria
        System.out.println("\n+--------------------------------------------------------+");
        System.out.println("|  TASK 4.2 - PADRAO COMMAND (RF10)                     |");
        System.out.println("|  Log e Auditoria Historica de Acoes do Coordenador    |");
        System.out.println("+--------------------------------------------------------+");

        historico.exibirHistorico();

        // Demonstrando Undo
        System.out.println("\n[DEMONSTRACAO] Desfazendo ultimo comando...");
        historico.desfazerUltimo();

        System.out.println("\n[DEMONSTRACAO] Historico apos Undo:");
        historico.exibirHistorico();

        System.out.println("\n[DASHBOARD] Exibindo dados finais:");
        dashboard.exibirDashboard();

        System.out.println("\n[FIM] Programa finalizado.");
    }
}