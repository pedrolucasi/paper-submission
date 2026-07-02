package br.edu.ifpb.cstsi.pss.scireview.loader;

import br.edu.ifpb.cstsi.pss.scireview.model.Papel;

import java.util.Set;

public record DadosUsuario(
        String email,
        String senha,
        String instituicao,
        Set<Papel> papeis
) {
}
