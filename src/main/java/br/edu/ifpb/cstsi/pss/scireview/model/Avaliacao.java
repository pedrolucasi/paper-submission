package br.edu.ifpb.cstsi.pss.scireview.model;

import br.edu.ifpb.cstsi.pss.scireview.exception.DadosInvalidosException;

import java.util.Objects;

public class Avaliacao {

    private final String contribuicoes;
    private final String pontosCritica;
    private final Veredito veredito;

    public Avaliacao(String contribuicoes, String pontosCritica, Veredito veredito) {
        this.contribuicoes = validarTextoObrigatorio(contribuicoes, "Contribuições são obrigatórias.");
        this.pontosCritica = validarTextoObrigatorio(pontosCritica, "Pontos de crítica são obrigatórios.");
        this.veredito = Objects.requireNonNull(veredito, "Veredito é obrigatório.");
    }

    public String getContribuicoes() {
        return contribuicoes;
    }

    public String getPontosCritica() {
        return pontosCritica;
    }

    public String getCriticas() {
        return pontosCritica;
    }

    public Veredito getVeredito() {
        return veredito;
    }

    private static String validarTextoObrigatorio(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new DadosInvalidosException(mensagem);
        }
        return valor.trim();
    }
}
