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
import br.edu.ifpb.cstsi.pss.scireview.loader.AssociacaoRevisorArea;
import br.edu.ifpb.cstsi.pss.scireview.loader.CsvDataLoader;
import br.edu.ifpb.cstsi.pss.scireview.loader.CsvPersistidor;
import br.edu.ifpb.cstsi.pss.scireview.loader.DadosArtigo;
import br.edu.ifpb.cstsi.pss.scireview.loader.DadosCarregados;
import br.edu.ifpb.cstsi.pss.scireview.loader.DadosEvento;
import br.edu.ifpb.cstsi.pss.scireview.loader.DadosUsuario;
import br.edu.ifpb.cstsi.pss.scireview.model.Artigo;
import br.edu.ifpb.cstsi.pss.scireview.model.ArtigoParaRevisao;
import br.edu.ifpb.cstsi.pss.scireview.model.Papel;
import br.edu.ifpb.cstsi.pss.scireview.model.Revisao;
import br.edu.ifpb.cstsi.pss.scireview.model.Usuario;
import br.edu.ifpb.cstsi.pss.scireview.model.Veredito;
import br.edu.ifpb.cstsi.pss.scireview.model.categoria.CategoriaArtigo;
import br.edu.ifpb.cstsi.pss.scireview.model.categoria.Demo;
import br.edu.ifpb.cstsi.pss.scireview.model.categoria.FullPaper;
import br.edu.ifpb.cstsi.pss.scireview.model.categoria.ShortPaper;
import br.edu.ifpb.cstsi.pss.scireview.model.estado.StatusArtigo;
import br.edu.ifpb.cstsi.pss.scireview.presentation.DashboardApresentador;
import br.edu.ifpb.cstsi.pss.scireview.presentation.SaidaConsole;
import br.edu.ifpb.cstsi.pss.scireview.service.CadastroAreaTematica;
import br.edu.ifpb.cstsi.pss.scireview.service.CadastroUsuario;
import br.edu.ifpb.cstsi.pss.scireview.service.ComiteTecnico;
import br.edu.ifpb.cstsi.pss.scireview.service.DistribuicaoRevisores;
import br.edu.ifpb.cstsi.pss.scireview.service.GerenciadorEvento;
import br.edu.ifpb.cstsi.pss.scireview.service.RevisaoArtigo;
import br.edu.ifpb.cstsi.pss.scireview.service.ServicoEmail;
import br.edu.ifpb.cstsi.pss.scireview.service.SistemaAvaliacao;
import br.edu.ifpb.cstsi.pss.scireview.service.SubmissaoArtigo;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Modo interativo: menu de terminal que permite ao usuário exercitar todos os
 * requisitos funcionais manualmente, partindo dos dados iniciais dos CSVs e
 * persistindo os novos dados de volta nos mesmos arquivos.
 */
public class ModoInterativo {

    private static final Path DIRETORIO_DADOS = Path.of("src", "main", "resources", "dados");

    private final Scanner scanner;
    private final SaidaConsole saida;

    private final CommandHistory historico;
    private final GerenciadorEvento gerenciadorEvento;
    private final CadastroUsuario cadastroUsuario;
    private final CadastroAreaTematica cadastroAreaTematica;
    private final SistemaAvaliacao sistemaAvaliacao;
    private final SubmissaoArtigo submissaoArtigo;
    private final ComiteTecnico comiteTecnico;
    private final ServicoEmail servicoEmail;
    private final Dashboard dashboard;
    private final DashboardApresentador dashboardApresentador;
    private final DistribuicaoRevisores distribuicao;
    private final RevisaoArtigo revisaoArtigo;
    private final CsvPersistidor persistidor;

    public ModoInterativo(Scanner scanner, SaidaConsole saida) {
        this.scanner = scanner;
        this.saida = saida;

        this.historico = CommandHistory.getInstance();
        this.gerenciadorEvento = new GerenciadorEvento();
        this.cadastroUsuario = new CadastroUsuario();
        this.cadastroAreaTematica = new CadastroAreaTematica();
        this.sistemaAvaliacao = new SistemaAvaliacao();
        this.submissaoArtigo = new SubmissaoArtigo(gerenciadorEvento, cadastroAreaTematica, cadastroUsuario);
        this.comiteTecnico = new ComiteTecnico();
        this.servicoEmail = new ServicoEmail(gerenciadorEvento, cadastroUsuario, sistemaAvaliacao, saida);
        gerenciadorEvento.adicionarObserver(servicoEmail);
        this.dashboard = new Dashboard(sistemaAvaliacao, cadastroUsuario);
        this.dashboardApresentador = new DashboardApresentador(saida);
        this.distribuicao = new DistribuicaoRevisores(sistemaAvaliacao, cadastroAreaTematica, servicoEmail);
        this.revisaoArtigo = new RevisaoArtigo(sistemaAvaliacao, cadastroAreaTematica, gerenciadorEvento);
        this.persistidor = new CsvPersistidor(DIRETORIO_DADOS);

        gerenciadorEvento.aoLimparEstado(cadastroAreaTematica::limpar);
        gerenciadorEvento.aoLimparEstado(comiteTecnico::limpar);
        gerenciadorEvento.aoLimparEstado(sistemaAvaliacao::limpar);
        gerenciadorEvento.aoLimparEstado(cadastroUsuario::limparAreasDeInteresseDosRevisores);
        gerenciadorEvento.aoLimparEstado(historico::limpar);
    }

    public void executar() {
        carregarDadosIniciais();
        historico.limpar();
        loopMenu();
    }

    private void carregarDadosIniciais() {
        DadosCarregados dados = new CsvDataLoader(DIRETORIO_DADOS).carregar();

        Usuario coordenador = null;
        for (DadosUsuario u : dados.usuarios()) {
            Usuario usuario = cadastroUsuario.cadastrar(u.email(), u.senha(), u.instituicao(), u.papeis());
            if (usuario.possuiPapel(Papel.COORDENADOR)) {
                coordenador = usuario;
            }
        }
        if (coordenador == null) {
            throw new IllegalStateException("Nenhum coordenador encontrado em usuarios.csv.");
        }

        LocalDate hoje = LocalDate.now();
        DadosEvento ev = dados.evento();
        gerenciadorEvento.startNovoEvento(ev.nome(), ev.cidade(), ev.periodo(),
                hoje.minusDays(ev.diasInicioAntesHoje()), hoje.plusDays(ev.diasFimDepoisHoje()));
        gerenciadorEvento.definirCategoria(coordenador, new FullPaper());

        for (String area : dados.areas()) {
            cadastroAreaTematica.cadastrar(coordenador, area);
        }
        for (AssociacaoRevisorArea assoc : dados.associacoesRevisores()) {
            cadastroUsuario.buscarPorEmail(assoc.emailRevisor())
                    .ifPresent(revisor -> cadastroAreaTematica.associarRevisor(revisor, assoc.area()));
        }
        for (Usuario u : cadastroUsuario.listarTodos()) {
            if (u.possuiPapel(Papel.REVISOR)) {
                comiteTecnico.registrarRevisor(coordenador, u);
            }
        }
        for (DadosArtigo a : dados.artigos()) {
            cadastroUsuario.buscarPorEmail(a.emailAutor()).ifPresent(autor -> {
                Artigo artigo = submissaoArtigo.submeter(
                        autor, a.titulo(), a.resumo(), a.coautores(), a.areas(), a.paginas());
                sistemaAvaliacao.adicionarArtigo(artigo);
            });
        }

        saida.linha("[CSV] Dados iniciais carregados de " + DIRETORIO_DADOS + "/");
        saida.linha("      " + cadastroUsuario.listarTodos().size() + " usuarios | "
                + cadastroAreaTematica.listar().size() + " areas | "
                + comiteTecnico.quantidadeRevisores() + " revisores no comite | "
                + sistemaAvaliacao.listarTodosArtigos().size() + " artigos submetidos.");
    }

    private void loopMenu() {
        boolean sair = false;
        while (!sair) {
            exibirCabecalho();
            String opcao = lerLinha("Escolha uma opcao:");
            saida.linha();
            try {
                switch (opcao) {
                    case "1" -> cadastrarUsuario();
                    case "2" -> cadastrarArea();
                    case "3" -> associarAreaRevisor();
                    case "4" -> definirCategoria();
                    case "5" -> registrarRevisor();
                    case "6" -> submeterArtigo();
                    case "7" -> listarArtigosDoAutor();
                    case "8" -> distribuirArtigos();
                    case "9" -> emitirParecer();
                    case "10" -> dashboardApresentador.exibir(dashboard.consultarDados());
                    case "11" -> decidirENotificar();
                    case "12" -> historicoEUndo();
                    case "13" -> iniciarNovoEvento();
                    case "0" -> sair = true;
                    default -> saida.linha("Opcao invalida.");
                }
            } catch (RuntimeException e) {
                saida.linha("[ERRO] " + e.getMessage());
            }
            saida.linha();
        }
        saida.linha("[FIM] Modo interativo encerrado.");
    }

    private void exibirCabecalho() {
        saida.linha("==================== SciReview — Modo Interativo ====================");
        gerenciadorEvento.getEventoAtual().ifPresentOrElse(ev -> {
            String categoria = ev.getCategoria().map(CategoriaArtigo::getNome).orElse("(nao definida)");
            saida.linha("Evento: " + ev.getNome() + " | Categoria: " + categoria);
        }, () -> saida.linha("Evento: (nenhum ativo)"));
        saida.linha("--------------------------------------------------------------------");
        saida.linha(" 1) Cadastrar usuario (RF02)");
        saida.linha(" 2) Cadastrar area tematica (RF03) [coordenador]");
        saida.linha(" 3) Revisor: declarar area de interesse (RF03)");
        saida.linha(" 4) Definir categoria do evento (RF04) [coordenador]");
        saida.linha(" 5) Registrar revisor no comite (RF04) [coordenador]");
        saida.linha(" 6) Submeter artigo (RF05) [autor]");
        saida.linha(" 7) Listar meus artigos e status (RF05) [autor]");
        saida.linha(" 8) Distribuir artigos aos revisores (RF06) [coordenador]");
        saida.linha(" 9) Revisor: emitir parecer (RF07)");
        saida.linha("10) Dashboard (RF08)");
        saida.linha("11) Decisao final + notificar autores (RF09) [coordenador]");
        saida.linha("12) Historico de acoes / desfazer (RF10)");
        saida.linha("13) Iniciar novo evento (RF01) [coordenador]");
        saida.linha(" 0) Sair");
    }

    // RF02
    private void cadastrarUsuario() {
        String email = lerLinha("E-mail:");
        String senha = lerLinha("Senha (min. 6 caracteres):");
        String instituicao = lerLinha("Instituicao:");
        Set<Papel> papeis = lerPapeis("Papeis (AUTOR, REVISOR, COORDENADOR — separados por virgula):");

        cadastroUsuario.cadastrar(email, senha, instituicao, papeis);
        persistidor.anexarUsuario(email, senha, instituicao, papeis);
        saida.linha("[OK] Usuario cadastrado e persistido em usuarios.csv: " + email);
    }

    // RF03
    private void cadastrarArea() {
        Usuario coordenador = lerUsuario("E-mail do coordenador:");
        if (coordenador == null) {
            return;
        }
        String nome = lerLinha("Nome da area tematica:");
        new CadastrarAreaCommand(cadastroAreaTematica, coordenador, nome).executar();
        persistidor.anexarArea(nome);
        saida.linha("[OK] Area persistida em areas.csv: " + nome);
    }

    // RF03
    private void associarAreaRevisor() {
        Usuario revisor = lerUsuario("E-mail do revisor:");
        if (revisor == null) {
            return;
        }
        String area = lerLinha("Area tematica (deve estar cadastrada):");
        cadastroAreaTematica.associarRevisor(revisor, area);
        persistidor.anexarAssociacaoRevisorArea(revisor.getEmail(), area);
        saida.linha("[OK] Associacao persistida em revisores_areas.csv: " + revisor.getEmail() + " -> " + area);
    }

    // RF04
    private void definirCategoria() {
        if (gerenciadorEvento.getEventoAtual().map(ev -> ev.getCategoria().isPresent()).orElse(false)) {
            saida.linha("A categoria ja foi definida para este evento (é definida uma unica vez).");
            saida.linha("Use a opcao 13 para iniciar um novo evento e redefini-la.");
            return;
        }
        Usuario coordenador = lerUsuario("E-mail do coordenador:");
        if (coordenador == null) {
            return;
        }
        String escolha = lerLinha("Categoria — 1) Full Paper  2) Short Paper  3) Demo:");
        CategoriaArtigo categoria = switch (escolha) {
            case "2" -> new ShortPaper();
            case "3" -> new Demo();
            default -> new FullPaper();
        };
        new DefinirCategoriaCommand(gerenciadorEvento, coordenador, categoria).executar();
    }

    // RF04
    private void registrarRevisor() {
        Usuario coordenador = lerUsuario("E-mail do coordenador:");
        if (coordenador == null) {
            return;
        }
        Usuario revisor = lerUsuario("E-mail do revisor a registrar:");
        if (revisor == null) {
            return;
        }
        new RegistrarRevisorCommand(comiteTecnico, coordenador, revisor).executar();
    }

    // RF05
    private void submeterArtigo() {
        Usuario autor = lerUsuario("E-mail do autor:");
        if (autor == null) {
            return;
        }
        String titulo = lerLinha("Titulo do artigo:");
        String resumo = lerLinha("Resumo:");
        List<String> coautores = lerLista("Coautores (e-mails, separados por virgula):");
        List<String> areas = lerLista("Areas tematicas (separadas por virgula):");
        int paginas = lerInteiro("Numero de paginas:");

        SubmeterArtigoCommand comando = new SubmeterArtigoCommand(
                submissaoArtigo, autor, titulo, resumo, coautores, areas, paginas);
        comando.executar();
        Artigo artigo = comando.getArtigoSubmetido();
        sistemaAvaliacao.adicionarArtigo(artigo);
        persistidor.anexarArtigo(autor.getEmail(), titulo, resumo, coautores, areas, paginas, false);
        saida.linha("[OK] Artigo submetido (ID: " + artigo.getId() + ", status: " + artigo.getStatus()
                + ") e persistido em artigos.csv.");
    }

    // RF05
    private void listarArtigosDoAutor() {
        Usuario autor = lerUsuario("E-mail do autor:");
        if (autor == null) {
            return;
        }
        List<Artigo> artigos = submissaoArtigo.listarArtigosDoAutor(autor);
        if (artigos.isEmpty()) {
            saida.linha("Nenhum artigo submetido por " + autor.getEmail() + ".");
            return;
        }
        saida.linha("Artigos de " + autor.getEmail() + ":");
        for (Artigo artigo : artigos) {
            saida.linha("   - " + artigo.getTitulo() + " | status: " + artigo.getStatus()
                    + " | ID: " + artigo.getId());
        }
    }

    // RF06
    private void distribuirArtigos() {
        Usuario coordenador = lerUsuario("E-mail do coordenador:");
        if (coordenador == null) {
            return;
        }
        List<Usuario> revisores = new ArrayList<>(comiteTecnico.listarRevisores());
        if (revisores.isEmpty()) {
            saida.linha("Nenhum revisor no comite. Registre revisores antes de distribuir.");
            return;
        }
        List<Artigo> pendentes = sistemaAvaliacao.listarTodosArtigos().stream()
                .filter(artigo -> sistemaAvaliacao.getRevisoesPorArtigo(artigo).isEmpty())
                .collect(Collectors.toList());
        if (pendentes.isEmpty()) {
            saida.linha("Nao ha artigos aguardando distribuicao.");
            return;
        }
        for (Artigo artigo : pendentes) {
            if (artigo.getStatus() == StatusArtigo.SUBMETIDO) {
                artigo.enviarParaRevisao();
            }
        }
        new DistribuirArtigosCommand(distribuicao, pendentes, revisores, coordenador).executar();

        saida.linha("Carga por revisor:");
        distribuicao.obterCargaPorRevisor(revisores).forEach((revisor, carga) ->
                saida.linha("   - " + revisor.getEmail() + ": " + carga + " artigo(s)"));
    }

    // RF07
    private void emitirParecer() {
        Usuario revisor = lerUsuario("E-mail do revisor:");
        if (revisor == null) {
            return;
        }
        List<ArtigoParaRevisao> pendentes = revisaoArtigo.listarArtigosPendentes(revisor);
        if (pendentes.isEmpty()) {
            saida.linha("Nenhum artigo pendente de revisao para " + revisor.getEmail() + ".");
            return;
        }
        saida.linha("Artigos pendentes (visao cega — sem autores):");
        for (ArtigoParaRevisao artigo : pendentes) {
            saida.linha("   - ID: " + artigo.id() + " | " + artigo.titulo() + " | areas: " + artigo.areasTematicas());
        }
        String artigoId = lerLinha("ID do artigo a avaliar:");
        String contribuicoes = lerLinha("Contribuicoes / pontos positivos:");
        String criticas = lerLinha("Pontos de critica:");
        Veredito veredito = lerVeredito();

        revisaoArtigo.emitirParecer(revisor, artigoId, contribuicoes, criticas, veredito);
        saida.linha("[OK] Parecer registrado. Veredito: " + veredito + ".");
    }

    // RF09
    private void decidirENotificar() {
        Usuario coordenador = lerUsuario("E-mail do coordenador:");
        if (coordenador == null) {
            return;
        }
        List<Artigo> revisados = sistemaAvaliacao.listarTodosArtigos().stream()
                .filter(artigo -> artigo.getStatus() == StatusArtigo.REVISADO)
                .collect(Collectors.toList());
        if (revisados.isEmpty()) {
            saida.linha("Nenhum artigo em estado 'revisado' aguardando decisao.");
        }
        for (Artigo artigo : revisados) {
            List<Revisao> revisoes = sistemaAvaliacao.getRevisoesPorArtigo(artigo);
            long positivas = revisoes.stream()
                    .filter(Revisao::isConcluida)
                    .map(revisao -> revisao.getAvaliacao().getVeredito())
                    .filter(v -> v == Veredito.ACEITO || v == Veredito.FRACAMENTE_ACEITO)
                    .count();
            long concluidas = revisoes.stream().filter(Revisao::isConcluida).count();
            if (positivas * 2 >= concluidas) {
                artigo.aceitar();
                saida.linha("[DECISAO] '" + artigo.getTitulo() + "' -> ACEITO.");
            } else {
                artigo.rejeitar();
                saida.linha("[DECISAO] '" + artigo.getTitulo() + "' -> REJEITADO.");
            }
        }
        new FinalizarCicloCommand(gerenciadorEvento, coordenador).executar();
    }

    // RF10
    private void historicoEUndo() {
        historico.exibirHistorico();
        String resposta = lerLinha("Desfazer o ultimo comando? (s/N):");
        if (resposta.equalsIgnoreCase("s")) {
            historico.desfazerUltimo();
        }
    }

    // RF01
    private void iniciarNovoEvento() {
        Usuario coordenador = lerUsuario("E-mail do coordenador:");
        if (coordenador == null) {
            return;
        }
        String nome = lerLinha("Nome do evento:");
        String cidade = lerLinha("Cidade:");
        String periodo = lerLinha("Periodo (texto livre):");

        new IniciarEventoCommand(gerenciadorEvento, coordenador, nome, cidade, periodo,
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(30)).executar();
        persistidor.reescreverEvento(nome, cidade, periodo, 1, 30);

        saida.linha("[OK] Novo evento iniciado e persistido em evento.csv.");
        saida.linha("     O evento comeca vazio: defina a categoria (op. 4), cadastre areas (op. 2)");
        saida.linha("     e registre revisores (op. 5) antes de novas submissoes.");
    }

    // ----- utilitarios de entrada -----

    private String lerLinha(String prompt) {
        saida.linha(prompt);
        return scanner.nextLine().trim();
    }

    private int lerInteiro(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(lerLinha(prompt));
            } catch (NumberFormatException e) {
                saida.linha("Valor invalido. Digite um numero inteiro.");
            }
        }
    }

    private List<String> lerLista(String prompt) {
        String texto = lerLinha(prompt);
        if (texto.isBlank()) {
            return new ArrayList<>();
        }
        return Arrays.stream(texto.split(","))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .collect(Collectors.toList());
    }

    private Set<Papel> lerPapeis(String prompt) {
        Set<Papel> papeis = new HashSet<>();
        for (String valor : lerLista(prompt)) {
            try {
                papeis.add(Papel.valueOf(valor.toUpperCase()));
            } catch (IllegalArgumentException e) {
                saida.linha("Papel ignorado (invalido): " + valor);
            }
        }
        return papeis;
    }

    private Veredito lerVeredito() {
        String escolha = lerLinha(
                "Veredito — 1) recusado  2) fracamente recusado  3) fracamente aceito  4) aceito:");
        return switch (escolha) {
            case "1" -> Veredito.RECUSADO;
            case "2" -> Veredito.FRACAMENTE_RECUSADO;
            case "3" -> Veredito.FRACAMENTE_ACEITO;
            default -> Veredito.ACEITO;
        };
    }

    private Usuario lerUsuario(String prompt) {
        String email = lerLinha(prompt);
        Optional<Usuario> usuario = cadastroUsuario.buscarPorEmail(email);
        if (usuario.isEmpty()) {
            saida.linha("Usuario nao encontrado: " + email);
            return null;
        }
        return usuario.get();
    }
}
