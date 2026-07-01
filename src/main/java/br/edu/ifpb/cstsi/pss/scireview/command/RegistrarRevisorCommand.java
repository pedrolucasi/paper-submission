package br.edu.ifpb.cstsi.pss.scireview.command;

import br.edu.ifpb.cstsi.pss.scireview.model.Usuario;
import br.edu.ifpb.cstsi.pss.scireview.service.ComiteTecnico;

import java.time.LocalDateTime;

public class RegistrarRevisorCommand implements Command {
    private final ComiteTecnico comiteTecnico;
    private final Usuario coordenador;
    private final Usuario revisor;
    private final LocalDateTime dataExecucao;

    public RegistrarRevisorCommand(ComiteTecnico comiteTecnico, Usuario coordenador, Usuario revisor) {
        this.comiteTecnico = comiteTecnico;
        this.coordenador = coordenador;
        this.revisor = revisor;
        this.dataExecucao = LocalDateTime.now();
    }

    @Override
    public void executar() {
        comiteTecnico.registrarRevisor(coordenador, revisor);
        System.out.println("[OK] Revisor registrado: " + revisor.getEmail());
        CommandHistory.getInstance().adicionar(this);
    }

    @Override
    public void desfazer() {
        System.out.println("[DESFAZER] Registro do revisor: " + revisor.getEmail());
    }

    @Override
    public String getDescricao() {
        return "Registrar Revisor: " + revisor.getEmail();
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
        return true;
    }
}