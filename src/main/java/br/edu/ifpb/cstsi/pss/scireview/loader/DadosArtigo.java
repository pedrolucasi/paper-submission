package br.edu.ifpb.cstsi.pss.scireview.loader;

import java.util.List;

public record DadosArtigo(
        String emailAutor,
        String titulo,
        String resumo,
        List<String> coautores,
        List<String> areas,
        int paginas,
        boolean recomendado
) {
}
