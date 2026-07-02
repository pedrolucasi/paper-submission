package br.edu.ifpb.cstsi.pss.scireview.model;

public record HistoricoRevisao(
        ArtigoParaRevisao artigo,
        String contribuicoes,
        String pontosCritica,
        Veredito veredito
) {
}
