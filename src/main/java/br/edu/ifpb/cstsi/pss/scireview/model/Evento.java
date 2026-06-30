package br.edu.ifpb.cstsi.pss.scireview.model;

import br.edu.ifpb.cstsi.pss.scireview.exception.DadosInvalidosException;

import java.util.Objects;

public class Evento {

    private final String nome;
    private final String cidade;
    private final String periodo;

    public Evento(String nome, String cidade, String periodo) {
        this.nome = validarTextoObrigatorio(nome, "Nome do evento é obrigatório.");
        this.cidade = validarTextoObrigatorio(cidade, "Cidade do evento é obrigatória.");
        this.periodo = validarTextoObrigatorio(periodo, "Período do evento é obrigatório.");
    }

    public String getNome() {
        return nome;
    }

    public String getCidade() {
        return cidade;
    }

    public String getPeriodo() {
        return periodo;
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (objeto == null || getClass() != objeto.getClass()) {
            return false;
        }
        Evento evento = (Evento) objeto;
        return Objects.equals(nome, evento.nome)
                && Objects.equals(cidade, evento.cidade)
                && Objects.equals(periodo, evento.periodo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, cidade, periodo);
    }

    @Override
    public String toString() {
        return periodo + " - " + nome + " (" + cidade + ")";
    }

    private static String validarTextoObrigatorio(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new DadosInvalidosException(mensagem);
        }
        return valor.trim();
    }
}
