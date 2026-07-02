package br.edu.ifpb.cstsi.pss.scireview.model;

public class Notificacao {
    private final String destinatario;
    private final String titulo;
    private final String conteudo;

    public Notificacao(String destinatario, String titulo, String conteudo) {
        this.destinatario = destinatario;
        this.titulo = titulo;
        this.conteudo = conteudo;
    }

    public String getDestinatario() { return destinatario; }
    public String getTitulo() { return titulo; }
    public String getConteudo() { return conteudo; }
}