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
import br.edu.ifpb.cstsi.pss.scireview.loader.DadosArtigo;
import br.edu.ifpb.cstsi.pss.scireview.loader.DadosCarregados;
import br.edu.ifpb.cstsi.pss.scireview.loader.DadosEvento;
import br.edu.ifpb.cstsi.pss.scireview.loader.DadosUsuario;
import br.edu.ifpb.cstsi.pss.scireview.model.Artigo;
import br.edu.ifpb.cstsi.pss.scireview.model.HistoricoRevisao;
import br.edu.ifpb.cstsi.pss.scireview.model.Papel;
import br.edu.ifpb.cstsi.pss.scireview.model.Revisao;
import br.edu.ifpb.cstsi.pss.scireview.model.Usuario;
import br.edu.ifpb.cstsi.pss.scireview.model.Veredito;
import br.edu.ifpb.cstsi.pss.scireview.model.categoria.FullPaper;
import br.edu.ifpb.cstsi.pss.scireview.presentation.DashboardApresentador;
import br.edu.ifpb.cstsi.pss.scireview.presentation.SaidaAplicacao;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Modo exemplo: executa uma demonstração automática, de ponta a ponta, de todos
 * os requisitos funcionais a partir dos dados iniciais dos arquivos CSV.
 */
public class ModoExemplo {

    public static void executar() {
        SaidaConsole saida = new SaidaConsole();
        SaidaAplicacao.configurar(saida);

        saida.linha("SciReview - Sistema de Submissao de Artigos");
        saida.linha("============================================");
        saida.linha();

        CommandHistory historico = CommandHistory.getInstance();

        GerenciadorEvento gerenciadorEvento = new GerenciadorEvento();
        CadastroUsuario cadastroUsuario = new CadastroUsuario();
        CadastroAreaTematica cadastroAreaTematica = new CadastroAreaTematica();
        SistemaAvaliacao sistemaAvaliacao = new SistemaAvaliacao();
        SubmissaoArtigo submissaoArtigo = new SubmissaoArtigo(
                gerenciadorEvento, cadastroAreaTematica, cadastroUsuario);
        ComiteTecnico comiteTecnico = new ComiteTecnico();

        ServicoEmail servicoEmail = new ServicoEmail(gerenciadorEvento, cadastroUsuario, sistemaAvaliacao, saida);
        gerenciadorEvento.adicionarObserver(servicoEmail);

        Dashboard dashboard = new Dashboard(sistemaAvaliacao, cadastroUsuario);
        DashboardApresentador dashboardApresentador = new DashboardApresentador(saida);
        DistribuicaoRevisores distribuicao = new DistribuicaoRevisores(
                sistemaAvaliacao, cadastroAreaTematica, servicoEmail);
        RevisaoArtigo revisaoArtigo = new RevisaoArtigo(
                sistemaAvaliacao, cadastroAreaTematica, gerenciadorEvento);

        // RF01 - Limpeza completa ao iniciar novo evento
        gerenciadorEvento.aoLimparEstado(cadastroAreaTematica::limpar);
        gerenciadorEvento.aoLimparEstado(comiteTecnico::limpar);
        gerenciadorEvento.aoLimparEstado(cadastroUsuario::limparAreasDeInteresseDosRevisores);
        gerenciadorEvento.aoLimparEstado(historico::limpar);

        // E1 - Carga de dados via CSV
        CsvDataLoader csvDataLoader = new CsvDataLoader();
        DadosCarregados dados = csvDataLoader.carregar();
        saida.linha("[CSV] Dados carregados de src/main/resources/dados/");
        saida.linha();

        Usuario coordenador = null;
        List<Usuario> revisores = new ArrayList<>();
        for (DadosUsuario dadosUsuario : dados.usuarios()) {
            Usuario usuario = cadastroUsuario.cadastrar(
                    dadosUsuario.email(),
                    dadosUsuario.senha(),
                    dadosUsuario.instituicao(),
                    dadosUsuario.papeis());
            if (usuario.possuiPapel(Papel.COORDENADOR)) {
                coordenador = usuario;
            }
            if (usuario.possuiPapel(Papel.REVISOR)) {
                revisores.add(usuario);
            }
        }

        if (coordenador == null) {
            throw new IllegalStateException("Nenhum coordenador encontrado no arquivo usuarios.csv.");
        }

        // RF01 - Command: Iniciar Evento
        LocalDate hoje = LocalDate.now();
        DadosEvento dadosEvento = dados.evento();
        IniciarEventoCommand iniciarEvento = new IniciarEventoCommand(
                gerenciadorEvento, coordenador,
                dadosEvento.nome(),
                dadosEvento.cidade(),
                dadosEvento.periodo(),
                hoje.minusDays(dadosEvento.diasInicioAntesHoje()),
                hoje.plusDays(dadosEvento.diasFimDepoisHoje())
        );
        iniciarEvento.executar();

        // RF04 - Command: Definir Categoria
        DefinirCategoriaCommand definirCategoria = new DefinirCategoriaCommand(
                gerenciadorEvento, coordenador, new FullPaper());
        definirCategoria.executar();

        // RF03 - Command: Cadastrar Areas
        for (String area : dados.areas()) {
            CadastrarAreaCommand cadastrarArea = new CadastrarAreaCommand(
                    cadastroAreaTematica, coordenador, area);
            cadastrarArea.executar();
        }

        // Associar areas aos revisores
        for (AssociacaoRevisorArea associacao : dados.associacoesRevisores()) {
            Usuario revisor = cadastroUsuario.buscarPorEmail(associacao.emailRevisor())
                    .orElseThrow(() -> new IllegalStateException(
                            "Revisor nao encontrado no CSV: " + associacao.emailRevisor()));
            cadastroAreaTematica.associarRevisor(revisor, associacao.area());
        }

        // RF04 - Command: Registrar Revisores
        for (Usuario revisor : revisores) {
            RegistrarRevisorCommand registrarRevisor = new RegistrarRevisorCommand(
                    comiteTecnico, coordenador, revisor);
            registrarRevisor.executar();
        }

        // RF05 - Command: Submeter Artigos
        List<Artigo> artigos = new ArrayList<>();
        Map<String, Boolean> recomendacaoPorTitulo = new HashMap<>();
        for (DadosArtigo dadosArtigo : dados.artigos()) {
            Usuario autor = cadastroUsuario.buscarPorEmail(dadosArtigo.emailAutor())
                    .orElseThrow(() -> new IllegalStateException(
                            "Autor nao encontrado no CSV: " + dadosArtigo.emailAutor()));

            SubmeterArtigoCommand submeter = new SubmeterArtigoCommand(
                    submissaoArtigo, autor,
                    dadosArtigo.titulo(),
                    dadosArtigo.resumo(),
                    dadosArtigo.coautores(),
                    dadosArtigo.areas(),
                    dadosArtigo.paginas()
            );
            submeter.executar();
            artigos.add(submeter.getArtigoSubmetido());
            recomendacaoPorTitulo.put(dadosArtigo.titulo(), dadosArtigo.recomendado());
        }

        // Colocar artigos em revisao
        for (Artigo artigo : artigos) {
            artigo.enviarParaRevisao();
            sistemaAvaliacao.adicionarArtigo(artigo);
        }

        // RF06 - Command: Distribuir Artigos
        DistribuirArtigosCommand distribuir = new DistribuirArtigosCommand(
                distribuicao, artigos, revisores, coordenador);
        distribuir.executar();

        saida.linha();
        saida.linha("[RF06] Carga por revisor apos distribuicao:");
        distribuicao.obterCargaPorRevisor(revisores).forEach((revisor, carga) ->
                saida.linha("   - " + revisor.getEmail() + ": " + carga + " artigo(s)"));

        // RF06 - Blind review: revisor ve apenas titulo, resumo e areas (sem autores)
        if (!revisores.isEmpty()) {
            saida.linha();
            saida.linha("[RF06] Artigos pendentes (visao cega) - " + revisores.get(0).getEmail());
            revisaoArtigo.listarArtigosPendentes(revisores.get(0)).forEach(artigoCego ->
                    saida.linha("   - " + artigoCego.titulo() + " | areas: " + artigoCego.areasTematicas()));
        }

        // RF07 - Conclusao de revisao: cada revisor emite seu parecer via o servico
        for (Revisao revisao : sistemaAvaliacao.getTodasRevisoes()) {
            boolean recomendado = recomendacaoPorTitulo.getOrDefault(
                    revisao.getArtigo().getTitulo(), false);
            revisaoArtigo.emitirParecer(
                    revisao.getRevisor(),
                    revisao.getArtigo().getId(),
                    recomendado ? "Excelente contribuicao para a area" : "Boa proposta, mas com limitacoes",
                    recomendado ? "Poderia melhorar a metodologia" : "Amostra pequena, faltam dados",
                    recomendado ? Veredito.ACEITO : Veredito.FRACAMENTE_RECUSADO);
        }

        // RF07 - Historico de avaliacoes concluidas por revisor (visao cega)
        for (Usuario revisor : revisores) {
            saida.linha();
            saida.linha("[RF07] Historico de revisoes - " + revisor.getEmail());
            List<HistoricoRevisao> historicoRevisor = revisaoArtigo.listarHistoricoRevisoes(revisor);
            if (historicoRevisor.isEmpty()) {
                saida.linha("   (nenhuma revisao concluida)");
            } else {
                historicoRevisor.forEach(item -> saida.linha(
                        "   - " + item.artigo().titulo()
                                + " | veredito: " + item.veredito()
                                + " | contribuicoes: " + item.contribuicoes()));
            }
        }

        // Decisao final do coordenador: aceita/rejeita cada artigo revisado conforme os pareceres
        for (Artigo artigo : artigos) {
            List<Revisao> revisoesDoArtigo = sistemaAvaliacao.getRevisoesPorArtigo(artigo);
            long positivas = revisoesDoArtigo.stream()
                    .filter(Revisao::isConcluida)
                    .map(revisao -> revisao.getAvaliacao().getVeredito())
                    .filter(v -> v == Veredito.ACEITO || v == Veredito.FRACAMENTE_ACEITO)
                    .count();
            long concluidas = revisoesDoArtigo.stream().filter(Revisao::isConcluida).count();
            if (positivas * 2 >= concluidas) {
                artigo.aceitar();
                saida.linha("[DECISAO] Artigo '" + artigo.getTitulo() + "' (ID: " + artigo.getId() + ") foi ACEITO.");
            } else {
                artigo.rejeitar();
                saida.linha("[DECISAO] Artigo '" + artigo.getTitulo() + "' (ID: " + artigo.getId() + ") foi REJEITADO.");
            }
        }

        // RF09 - Envio de email real (ATIVADO POR PADRAO)
        servicoEmail.ativarEmailReal();

        // RF09 - Command: Finalizar Ciclo (dispara os emails)
        FinalizarCicloCommand finalizar = new FinalizarCicloCommand(gerenciadorEvento, coordenador);
        finalizar.executar();

        // RF10 - Exibir Historico de Auditoria
        saida.linha();
        saida.linha("+--------------------------------------------------------+");
        saida.linha("|  TASK 4.2 - PADRAO COMMAND (RF10)                     |");
        saida.linha("|  Log e Auditoria Historica de Acoes do Coordenador    |");
        saida.linha("+--------------------------------------------------------+");

        historico.exibirHistorico();

        saida.linha();
        saida.linha("[DEMONSTRACAO] Desfazendo ultimo comando...");
        historico.desfazerUltimo();

        saida.linha();
        saida.linha("[DEMONSTRACAO] Historico apos Undo:");
        historico.exibirHistorico();

        saida.linha();
        saida.linha("[DASHBOARD] Exibindo dados finais:");
        dashboardApresentador.exibir(dashboard.consultarDados());

        saida.linha();
        saida.linha("[RF01] Iniciando novo evento (limpeza automatica do anterior)...");
        saida.linha("   Antes  -> artigos: " + sistemaAvaliacao.listarTodosArtigos().size()
                + " | areas: " + cadastroAreaTematica.listar().size()
                + " | comite: " + comiteTecnico.quantidadeRevisores()
                + " | comandos: " + historico.getHistorico().size());

        IniciarEventoCommand novoEvento = new IniciarEventoCommand(
                gerenciadorEvento, coordenador,
                "Workshop de Pesquisa Aplicada - 2027",
                "Joao Pessoa - PB",
                "10 a 12 de junho de 2027",
                hoje.minusDays(10),
                hoje.plusDays(50)
        );
        novoEvento.executar();

        saida.linha("   Depois -> artigos: " + sistemaAvaliacao.listarTodosArtigos().size()
                + " | areas: " + cadastroAreaTematica.listar().size()
                + " | comite: " + comiteTecnico.quantidadeRevisores()
                + " | comandos: " + historico.getHistorico().size() + " (apenas o novo evento)");

        saida.linha();
        saida.linha("[FIM] Programa finalizado.");
    }
}
