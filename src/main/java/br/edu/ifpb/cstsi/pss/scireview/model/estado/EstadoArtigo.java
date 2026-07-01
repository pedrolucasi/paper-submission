package br.edu.ifpb.cstsi.pss.scireview.model.estado;

import br.edu.ifpb.cstsi.pss.scireview.model.Artigo;

public interface EstadoArtigo {

    StatusArtigo getStatus();

    void enviarParaRevisao(Artigo artigo);

    void aceitar(Artigo artigo);

    void rejeitar(Artigo artigo);
}
