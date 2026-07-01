package br.edu.ifpb.cstsi.pss.scireview.command;

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
            System.out.println("[AVISO] Nenhum comando para desfazer.");
            return;
        }
        Command ultimo = historico.remove(historico.size() - 1);
        if (ultimo.isReversivel()) {
            ultimo.desfazer();
            desfeitos.add(ultimo);
            System.out.println("[DESFAZER] " + ultimo.getDescricao());
        } else {
            System.out.println("[ERRO] Comando nao e reversivel: " + ultimo.getDescricao());
            historico.add(ultimo);
        }
    }

    public void refazerUltimoDesfeito() {
        if (desfeitos.isEmpty()) {
            System.out.println("[AVISO] Nenhum comando para refazer.");
            return;
        }
        Command ultimo = desfeitos.remove(desfeitos.size() - 1);
        ultimo.executar();
        historico.add(ultimo);
        System.out.println("[REFAZER] " + ultimo.getDescricao());
    }

    public void exibirHistorico() {
        System.out.println("\n+--------------------------------------------------------+");
        System.out.println("|              HISTORICO DE AUDITORIA                    |");
        System.out.println("+--------------------------------------------------------+");

        if (historico.isEmpty()) {
            System.out.println("|   Nenhuma acao registrada.                            |");
        } else {
            int i = 1;
            for (Command cmd : historico) {
                String data = cmd.getDataExecucao().toString();
                String executor = cmd.getExecutor();
                String descricao = cmd.getDescricao();
                String reversivel = cmd.isReversivel() ? "[X]" : "[ ]";

                System.out.printf("| %2d. %s %s%n", i, reversivel, descricao);
                System.out.printf("|     Usuario: %s | Hora: %s%n", executor, data);
                i++;
            }
        }
        System.out.println("+--------------------------------------------------------+");
        System.out.println("   Total de comandos: " + historico.size());
    }

    public List<Command> getHistorico() {
        return Collections.unmodifiableList(historico);
    }

    public void limparHistorico() {
        historico.clear();
        desfeitos.clear();
        System.out.println("[SISTEMA] Historico limpo.");
    }
}