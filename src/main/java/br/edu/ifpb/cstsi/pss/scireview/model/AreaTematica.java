package br.edu.ifpb.cstsi.pss.scireview.model;

import br.edu.ifpb.cstsi.pss.scireview.exception.DadosInvalidosException;

import java.util.Objects;

public class AreaTematica {

    private final String nome;

    public AreaTematica(String nome) {
        this.nome = normalizarNome(nome);
    }

    public String getNome() {
        return nome;
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (objeto == null || getClass() != objeto.getClass()) {
            return false;
        }
        AreaTematica area = (AreaTematica) objeto;
        return Objects.equals(nome, area.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome);
    }

    @Override
    public String toString() {
        return nome;
    }

    private static String normalizarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new DadosInvalidosException("Nome da área temática é obrigatório.");
        }
        return nome.trim().toLowerCase();
    }
}
