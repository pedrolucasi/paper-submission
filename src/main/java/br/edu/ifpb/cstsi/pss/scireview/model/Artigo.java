package br.edu.ifpb.cstsi.pss.scireview.model;

public class Artigo {
    private int id;
    private String titulo;
    private String resumo;
    private String emailAutor;
    private StatusArtigo status;
    private String categoria;
    private Evento evento;
    private static int contadorId = 0;

    public Artigo(String titulo, String resumo, String emailAutor, String categoria, Evento evento) {
        this.id = ++contadorId;
        this.titulo = titulo;
        this.resumo = resumo;
        this.emailAutor = emailAutor;
        this.status = StatusArtigo.SUBMETIDO;
        this.categoria = categoria;
        this.evento = evento;
    }

    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getResumo() { return resumo; }
    public String getEmailAutor() { return emailAutor; }
    public StatusArtigo getStatus() { return status; }
    public void setStatus(StatusArtigo status) { this.status = status; }
    public String getCategoria() { return categoria; }
    public Evento getEvento() { return evento; }

    @Override
    public String toString() {
        return "Artigo #" + id + " - " + titulo + " (" + status + ")";
    }
}