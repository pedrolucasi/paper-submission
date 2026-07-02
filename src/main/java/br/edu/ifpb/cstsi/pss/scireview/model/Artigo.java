package br.edu.ifpb.cstsi.pss.scireview.model;

import br.edu.ifpb.cstsi.pss.scireview.exception.DadosInvalidosException;
import br.edu.ifpb.cstsi.pss.scireview.model.categoria.CategoriaArtigo;
import br.edu.ifpb.cstsi.pss.scireview.model.estado.EstadoArtigo;
import br.edu.ifpb.cstsi.pss.scireview.model.estado.StatusArtigo;
import br.edu.ifpb.cstsi.pss.scireview.model.estado.Submetido;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Artigo {

    private final String id;
    private final String nome;
    private final String resumo;
    private final List<String> coautores;
    private final Set<AreaTematica> areasTematicas;
    private final Usuario autor;
    private final Evento evento;
    private EstadoArtigo estado;

    public Artigo(String id, String nome, String resumo, List<String> coautores,
                  Set<AreaTematica> areasTematicas, Usuario autor, Evento evento) {
        this.id = validarId(id);
        this.nome = validarTextoObrigatorio(nome, "Nome do artigo é obrigatório.");
        this.resumo = validarTextoObrigatorio(resumo, "Resumo do artigo é obrigatório.");
        this.coautores = validarCoautores(coautores);
        this.areasTematicas = validarAreas(areasTematicas);
        this.autor = Objects.requireNonNull(autor, "Autor do artigo é obrigatório.");
        this.evento = Objects.requireNonNull(evento, "Evento do artigo é obrigatório.");
        this.estado = new Submetido();
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getResumo() {
        return resumo;
    }

    public List<String> getCoautores() {
        return Collections.unmodifiableList(coautores);
    }

    public Set<AreaTematica> getAreasTematicas() {
        return Collections.unmodifiableSet(areasTematicas);
    }

    public Usuario getAutor() {
        return autor;
    }

    public StatusArtigo getStatus() {
        return estado.getStatus();
    }

    public void alterarEstado(EstadoArtigo novoEstado) {
        this.estado = novoEstado;
    }

    public void enviarParaRevisao() {
        estado.enviarParaRevisao(this);
    }

    public void concluirRevisao() {
        estado.concluirRevisao(this);
    }

    public void aceitar() {
        estado.aceitar(this);
    }

    public void rejeitar() {
        estado.rejeitar(this);
    }

    public String getEmailAutor() {
        return autor.getEmail();
    }

    public Evento getEvento() {
        return evento;
    }

    public String getCategoria() {
        return evento.getCategoria()
                .map(CategoriaArtigo::getNome)
                .orElse("Categoria não definida");
    }

    public String getTitulo() {
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
        Artigo artigo = (Artigo) objeto;
        return Objects.equals(id, artigo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    private static String validarId(String id) {
        if (id == null || id.isBlank()) {
            throw new DadosInvalidosException("Identificador do artigo é obrigatório.");
        }
        return id.trim();
    }

    private static String validarTextoObrigatorio(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new DadosInvalidosException(mensagem);
        }
        return valor.trim();
    }

    private static List<String> validarCoautores(List<String> coautores) {
        if (coautores == null || coautores.isEmpty()) {
            throw new DadosInvalidosException("Coautores são obrigatórios.");
        }

        List<String> coautoresNormalizados = new ArrayList<>();
        for (String coautor : coautores) {
            if (coautor == null || coautor.isBlank()) {
                throw new DadosInvalidosException("Nome de coautor não pode ser vazio.");
            }
            coautoresNormalizados.add(coautor.trim());
        }
        return List.copyOf(coautoresNormalizados);
    }

    private static Set<AreaTematica> validarAreas(Set<AreaTematica> areas) {
        if (areas == null || areas.isEmpty()) {
            throw new DadosInvalidosException("Pelo menos uma área temática é obrigatória.");
        }
        return Set.copyOf(areas);
    }
}