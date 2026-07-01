package br.edu.ifpb.cstsi.pss.scireview.command;

import br.edu.ifpb.cstsi.pss.scireview.model.Usuario;
import br.edu.ifpb.cstsi.pss.scireview.service.GerenciadorEvento;

import java.time.LocalDateTime;

public class FinalizarCicloCommand implements Command {
    private final GerenciadorEvento gerenciadorEvento;
    private final Usuario coordenador;
    private final LocalDateTime dataExecucao;

    public FinalizarCicloCommand(GerenciadorEvento gerenciadorEvento, Usuario coordenador) {
        this.gerenciadorEvento = gerenciadorEvento;
        this.coordenador = coordenador;
        this.dataExecucao = LocalDateTime.now();
    }

    @Override
    public void executar() {
        gerenciadorEvento.finalizarCicloRevisoes();
        System.out.println("[OK] Ciclo de revisoes finalizado!");
        CommandHistory.getInstance().adicionar(this);
    }

    @Override
    public void desfazer() {
        System.out.println("[DESFAZER] Finalizacao do ciclo de revisoes");
    }

    @Override
    public String getDescricao() {
        return "Finalizar Ciclo de Revisoes";
    }

    @Override
    public LocalDateTime getDataExecucao() {
        return dataExecucao;
    }

    @Override
    public String getExecutor() {
        return coordenador != null ? coordenador.getEmail() : "Sistema";
    }

    @Override
    public boolean isReversivel() {
        return false;
    }
}