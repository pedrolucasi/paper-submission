package br.edu.ifpb.cstsi.pss.scireview.model.estado;

import br.edu.ifpb.cstsi.pss.scireview.model.Artigo;

public class EmRevisao extends EstadoArtigoBase {

    public EmRevisao() {
        super(StatusArtigo.EM_REVISAO);
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
