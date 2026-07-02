package br.edu.ifpb.cstsi.pss.scireview.service;

import br.edu.ifpb.cstsi.pss.scireview.exception.EmailDuplicadoException;
import br.edu.ifpb.cstsi.pss.scireview.model.Papel;
import br.edu.ifpb.cstsi.pss.scireview.model.Usuario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class CadastroUsuario {

    private final Map<String, Usuario> usuariosPorEmail = new HashMap<>();

    public Usuario cadastrar(String email, String senha, String instituicao, Set<Papel> papeis) {
        return cadastrar(new Usuario(email, senha, instituicao, papeis));
    }

    public Usuario cadastrar(Usuario usuario) {
        String email = usuario.getEmail();
        if (usuariosPorEmail.containsKey(email)) {
            throw new EmailDuplicadoException(email);
        }
        usuariosPorEmail.put(email, usuario);
        return usuario;
    }

    public boolean emailJaCadastrado(String email) {
        return usuariosPorEmail.containsKey(normalizarChaveEmail(email));
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return Optional.ofNullable(usuariosPorEmail.get(normalizarChaveEmail(email)));
    }

    public List<Usuario> listarTodos() {
        return new ArrayList<>(usuariosPorEmail.values());
    }

    public void limparAreasDeInteresseDosRevisores() {
        usuariosPorEmail.values().stream()
                .filter(usuario -> usuario.possuiPapel(Papel.REVISOR))
                .forEach(Usuario::limparAreasDeInteresse);
    }

    private String normalizarChaveEmail(String email) {
        return email.trim().toLowerCase();
    }

    public List<Usuario> listarRevisoresComAres(){
        return usuariosPorEmail.values().stream()
                .filter(u -> u.possuiPapel(Papel.REVISOR) && !u.getAreasDeInteresse().isEmpty())
                .collect(java.util.stream.Collectors.toList());
    }
}