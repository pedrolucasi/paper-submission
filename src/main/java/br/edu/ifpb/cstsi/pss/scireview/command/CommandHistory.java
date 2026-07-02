package br.edu.ifpb.cstsi.pss.scireview.command;

import br.edu.ifpb.cstsi.pss.scireview.presentation.SaidaAplicacao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandHistory {
    private static CommandHistory instance;
    private final List<Command> historico = new ArrayList<>();
    private final List<Command> desfeitos = new ArrayList<>();

    private CommandHistory() {}

    public static CommandHistory getInstance() {
        if (instance == null) {
            instance = new CommandHistory();
        }
        return instance;
    }

    public void adicionar(Command command) {
        historico.add(command);
        desfeitos.clear();
    }

    public void desfazerUltimo() {
        if (historico.isEmpty()) {
            SaidaAplicacao.get().linha("[AVISO] Nenhum comando para desfazer.");
            return;
        }
        Command ultimo = historico.remove(historico.size() - 1);
        if (ultimo.isReversivel()) {
            ultimo.desfazer();
            desfeitos.add(ultimo);
            SaidaAplicacao.get().linha("[DESFAZER] " + ultimo.getDescricao());
        } else {
            SaidaAplicacao.get().linha("[ERRO] Comando nao e reversivel: " + ultimo.getDescricao());
            historico.add(ultimo);
        }
    }

    public void refazerUltimoDesfeito() {
        if (desfeitos.isEmpty()) {
            SaidaAplicacao.get().linha("[AVISO] Nenhum comando para refazer.");
            return;
        }
        Command ultimo = desfeitos.remove(desfeitos.size() - 1);
        ultimo.executar();
        historico.add(ultimo);
        SaidaAplicacao.get().linha("[REFAZER] " + ultimo.getDescricao());
    }

    public void exibirHistorico() {
        var saida = SaidaAplicacao.get();
        saida.linha();
        saida.linha("+--------------------------------------------------------+");
        saida.linha("|              HISTORICO DE AUDITORIA                    |");
        saida.linha("+--------------------------------------------------------+");

        if (historico.isEmpty()) {
            saida.linha("|   Nenhuma acao registrada.                            |");
        } else {
            int i = 1;
            for (Command cmd : historico) {
                String data = cmd.getDataExecucao().toString();
                String executor = cmd.getExecutor();
                String descricao = cmd.getDescricao();
                String reversivel = cmd.isReversivel() ? "[X]" : "[ ]";

                saida.formatado("| %2d. %s %s%n", i, reversivel, descricao);
                saida.formatado("|     Usuario: %s | Hora: %s%n", executor, data);
                i++;
            }
        }
        saida.linha("+--------------------------------------------------------+");
        saida.linha("   Total de comandos: " + historico.size());
    }

    public List<Command> getHistorico() {
        return Collections.unmodifiableList(historico);
    }

    public void limparHistorico() {
        limpar();
        SaidaAplicacao.get().linha("[SISTEMA] Historico limpo.");
    }

    public void limpar() {
        historico.clear();
        desfeitos.clear();
    }
}
