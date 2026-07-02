package br.edu.ifpb.cstsi.pss.scireview.service;

import br.edu.ifpb.cstsi.pss.scireview.exception.AcessoNaoAutorizadoException;
import br.edu.ifpb.cstsi.pss.scireview.exception.DadosInvalidosException;
import br.edu.ifpb.cstsi.pss.scireview.model.Papel;
import br.edu.ifpb.cstsi.pss.scireview.model.Usuario;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;


public class ComiteTecnico {

    private final Set<Usuario> revisores = new LinkedHashSet<>();

    public Usuario registrarRevisor(Usuario coordenador, Usuario revisor) {
        if (coordenador == null || !coordenador.possuiPapel(Papel.COORDENADOR)) {
            throw new AcessoNaoAutorizadoException(
                    "Apenas o coordenador pode registrar revisores no comitê técnico.");
        }
        if (revisor == null || !revisor.possuiPapel(Papel.REVISOR)) {
            throw new DadosInvalidosException(
                    "Somente usuários com o papel de revisor podem compor o comitê técnico.");
        }
        revisores.add(revisor);
        return revisor;
    }

    public boolean pertenceAoComite(Usuario usuario) {
        return revisores.contains(usuario);
    }

    public Set<Usuario> listarRevisores() {
        return Collections.unmodifiableSet(revisores);
    }

    public int quantidadeRevisores() {
        return revisores.size();
    }

    public void limpar() {
        revisores.clear();
    }
}
