package br.edu.ifpb.cstsi.pss.scireview.service;

import br.edu.ifpb.cstsi.pss.scireview.exception.EmailDuplicadoException;
import br.edu.ifpb.cstsi.pss.scireview.model.Papel;
import br.edu.ifpb.cstsi.pss.scireview.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CadastroUsuarioTest {

    private CadastroUsuario cadastroUsuario;

    @BeforeEach
    void setUp() {
        cadastroUsuario = new CadastroUsuario();
    }

    @Test
    void deveCadastrarUsuarioComSucesso() {
        Usuario usuario = cadastroUsuario.cadastrar(
                "joao@ifpb.edu.br",
                "senha123",
                "IFPB",
                Set.of(Papel.AUTOR)
        );

        assertEquals("joao@ifpb.edu.br", usuario.getEmail());
        assertTrue(cadastroUsuario.emailJaCadastrado("joao@ifpb.edu.br"));
    }

    @Test
    void deveImpedirCadastroComEmailDuplicado() {
        cadastroUsuario.cadastrar(
                "joao@ifpb.edu.br",
                "senha123",
                "IFPB",
                Set.of(Papel.AUTOR)
        );

        assertThrows(EmailDuplicadoException.class, () -> cadastroUsuario.cadastrar(
                "JOAO@ifpb.edu.br",
                "outrasenha",
                "UFPB",
                Set.of(Papel.REVISOR)
        ));
    }

    @Test
    void deveBuscarUsuarioPorEmail() {
        cadastroUsuario.cadastrar(
                "ana@ifpb.edu.br",
                "senha123",
                "IFPB",
                Set.of(Papel.COORDENADOR)
        );

        Usuario encontrado = cadastroUsuario.buscarPorEmail("ana@ifpb.edu.br").orElseThrow();
        assertEquals(Papel.COORDENADOR, encontrado.getPapeis().iterator().next());
    }
}
