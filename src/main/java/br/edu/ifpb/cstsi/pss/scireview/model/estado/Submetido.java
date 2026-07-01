package br.edu.ifpb.cstsi.pss.scireview.model.estado;

import br.edu.ifpb.cstsi.pss.scireview.model.Artigo;

public class Submetido extends EstadoArtigoBase {

    public Submetido() {
        super(StatusArtigo.SUBMETIDO);
    }

    @Override
    public void enviarParaRevisao(Artigo artigo) {
        artigo.alterarEstado(new EmRevisao());
    }
}
