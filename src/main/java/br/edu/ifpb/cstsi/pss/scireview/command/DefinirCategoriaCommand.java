package br.edu.ifpb.cstsi.pss.scireview.command;

import br.edu.ifpb.cstsi.pss.scireview.model.Usuario;
import br.edu.ifpb.cstsi.pss.scireview.model.categoria.CategoriaArtigo;
import br.edu.ifpb.cstsi.pss.scireview.service.GerenciadorEvento;

import java.time.LocalDateTime;

public class DefinirCategoriaCommand implements Command {
    private final GerenciadorEvento gerenciadorEvento;
    private final Usuario coordenador;
    private final CategoriaArtigo categoria;
    private final LocalDateTime dataExecucao;

    public DefinirCategoriaCommand(GerenciadorEvento gerenciadorEvento, Usuario coordenador, CategoriaArtigo categoria) {
        this.gerenciadorEvento = gerenciadorEvento;
        this.coordenador = coordenador;
        this.categoria = categoria;
        this.dataExecucao = LocalDateTime.now();
    }

    @Override
    public void executar() {
        gerenciadorEvento.definirCategoria(coordenador, categoria);
        System.out.println("[OK] Categoria definida: " + categoria.getNome());
        CommandHistory.getInstance().adicionar(this);
    }

    @Override
    public void desfazer() {
        System.out.println("[DESFAZER] Definicao da categoria: " + categoria.getNome());
    }

    @Override
    public String getDescricao() {
        return "Definir Categoria: " + categoria.getNome();
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