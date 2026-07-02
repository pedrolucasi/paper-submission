package br.edu.ifpb.cstsi.pss.scireview.model.estado;

import br.edu.ifpb.cstsi.pss.scireview.model.Artigo;

public class Revisado extends EstadoArtigoBase {

    public Revisado() {
        super(StatusArtigo.REVISADO);
    }

    @Override
    public void aceitar(Artigo artigo) {
        artigo.alterarEstado(new Aceito());
    }

    @Override
    public void rejeitar(Artigo artigo) {
        artigo.alterarEstado(new Rejeitado());
    }
}
