package br.edu.ifpb.cstsi.pss.scireview.model;

public class Revisao {
    private Artigo artigo;
    private Usuario revisor;
    private Avaliacao avaliacao;

    public Revisao(Artigo artigo, Usuario revisor) {
        this.artigo = artigo;
        this.revisor = revisor;
    }

    public Artigo getArtigo() { return artigo; }
    public Usuario getRevisor() { return revisor; }
    public Avaliacao getAvaliacao() { return avaliacao; }
    public void setAvaliacao(Avaliacao avaliacao) { this.avaliacao = avaliacao; }

    public boolean isConcluida() {
        return avaliacao != null;
    }
}