package br.edu.ifpb.cstsi.pss.scireview.model;

import java.util.List;

public record ArtigoParaRevisao(
        String id,
        String titulo,
        String resumo,
        List<String> areasTematicas
) {
}
