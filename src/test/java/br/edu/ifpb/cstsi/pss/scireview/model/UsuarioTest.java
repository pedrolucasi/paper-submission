package br.edu.ifpb.cstsi.pss.scireview.model;

import br.edu.ifpb.cstsi.pss.scireview.exception.DadosInvalidosException;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UsuarioTest {

    @Test
    void deveCriarUsuarioComDadosValidos() {
        Usuario usuario = new Usuario(
                "maria@ifpb.edu.br",
                "senha123",
                "IFPB",
                Set.of(Papel.AUTOR, Papel.REVISOR)
        );

        assertEquals("maria@ifpb.edu.br", usuario.getEmail());
        assertEquals("senha123", usuario.getSenha());
        assertEquals("IFPB", usuario.getInstituicao());
        assertTrue(usuario.possuiPapel(Papel.AUTOR));
        assertTrue(usuario.possuiPapel(Papel.REVISOR));
    }

    @Test
    void deveNormalizarEmailParaMinusculas() {
        Usuario usuario = new Usuario(
                "  Maria@IFPB.edu.br  ",
                "senha123",
                "IFPB",
                Set.of(Papel.COORDENADOR)
        );

        assertEquals("maria@ifpb.edu.br", usuario.getEmail());
    }

    @Test
    void deveRejeitarEmailInvalido() {
        assertThrows(DadosInvalidosException.class, () -> new Usuario(
                "email-invalido",
                "senha123",
                "IFPB",
                Set.of(Papel.AUTOR)
        ));
    }

    @Test
    void deveRejeitarUsuarioSemPapeis() {
        assertThrows(DadosInvalidosException.class, () -> new Usuario(
                "maria@ifpb.edu.br",
                "senha123",
                "IFPB",
                Set.of()
        ));
    }
}
