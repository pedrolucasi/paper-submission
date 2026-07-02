package br.edu.ifpb.cstsi.pss.scireview.command;

import br.edu.ifpb.cstsi.pss.scireview.model.Artigo;
import br.edu.ifpb.cstsi.pss.scireview.model.Usuario;
import br.edu.ifpb.cstsi.pss.scireview.service.DistribuicaoRevisores;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class DistribuirArtigosCommand implements Command {
    private final DistribuicaoRevisores distribuicao;
    private final List<Artigo> artigos;
    private final List<Usuario> revisores;
    private final LocalDateTime dataExecucao;
    private final Usuario coordenador;
    private Map<Artigo, List<Usuario>> distribuicaoRealizada;

    public DistribuirArtigosCommand(DistribuicaoRevisores distribuicao, List<Artigo> artigos,
                                    List<Usuario> revisores, Usuario coordenador) {
        this.distribuicao = distribuicao;
        this.artigos = artigos;
        this.revisores = revisores;
        this.coordenador = coordenador;
        this.dataExecucao = LocalDateTime.now();
    }

    @Override
    public void executar() {
        distribuicaoRealizada = distribuicao.distribuirArtigos(artigos, revisores);
        System.out.println("[OK] Artigos distribuidos: " + artigos.size() + " artigos para " + revisores.size() + " revisores");
        CommandHistory.getInstance().adicionar(this);
    }

    public Map<Artigo, List<Usuario>> getDistribuicaoRealizada() {
        return distribuicaoRealizada;
    }

    @Override
    public void desfazer() {
        System.out.println("[DESFAZER] Distribuicao de artigos");
    }

    @Override
    public String getDescricao() {
        return "Distribuir Artigos: " + artigos.size() + " artigos para " + revisores.size() + " revisores";
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