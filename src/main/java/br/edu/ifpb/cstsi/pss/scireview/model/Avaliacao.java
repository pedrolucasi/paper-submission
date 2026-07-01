package br.edu.ifpb.cstsi.pss.scireview.model;

public class Avaliacao {
    private String contribuicoes;
    private String criticas;
    private Veredito veredito;

    public Avaliacao(String contribuicoes, String criticas, Veredito veredito) {
        this.contribuicoes = contribuicoes;
        this.criticas = criticas;
        this.veredito = veredito;
    }

    public String getContribuicoes() { return contribuicoes; }
    public String getCriticas() { return criticas; }
    public Veredito getVeredito() { return veredito; }
}