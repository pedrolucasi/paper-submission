package br.edu.ifpb.cstsi.pss.scireview.loader;

import java.util.List;

public record DadosCarregados(
        List<DadosUsuario> usuarios,
        DadosEvento evento,
        List<String> areas,
        List<AssociacaoRevisorArea> associacoesRevisores,
        List<DadosArtigo> artigos
) {
}
