package br.edu.ifpb.cstsi.pss.scireview.loader;

public record DadosEvento(
        String nome,
        String cidade,
        String periodo,
        int diasInicioAntesHoje,
        int diasFimDepoisHoje
) {
}
