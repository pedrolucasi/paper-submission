package br.edu.ifpb.cstsi.pss.scireview.loader;

import java.util.List;

public record DadosArtigo(
        String emailAutor,
        String titulo,
        String resumo,
        List<String> coautores,
        int paginas,
        boolean recomendado
) {
}
