package br.edu.ifpb.cstsi.pss.scireview.command;

import br.edu.ifpb.cstsi.pss.scireview.model.AreaTematica;
import br.edu.ifpb.cstsi.pss.scireview.model.Usuario;
import br.edu.ifpb.cstsi.pss.scireview.presentation.SaidaAplicacao;
import br.edu.ifpb.cstsi.pss.scireview.service.CadastroAreaTematica;

import java.time.LocalDateTime;

public class CadastrarAreaCommand implements Command {
    private final CadastroAreaTematica cadastroArea;
    private final Usuario coordenador;
    private final String nomeArea;
    private final LocalDateTime dataExecucao;
    private AreaTematica areaCriada;

    public CadastrarAreaCommand(CadastroAreaTematica cadastroArea, Usuario coordenador, String nomeArea) {
        this.cadastroArea = cadastroArea;
        this.coordenador = coordenador;
        this.nomeArea = nomeArea;
        this.dataExecucao = LocalDateTime.now();
    }

    @Override
    public void executar() {
        areaCriada = cadastroArea.cadastrar(coordenador, nomeArea);
        SaidaAplicacao.get().linha("[OK] Area cadastrada: " + nomeArea);
        CommandHistory.getInstance().adicionar(this);
    }

    @Override
    public void desfazer() {
        if (areaCriada != null) {
            SaidaAplicacao.get().linha("[DESFAZER] Cadastro da area: " + nomeArea);
        }
    }

    @Override
    public String getDescricao() {
        return "Cadastrar Area Tematica: " + nomeArea;
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