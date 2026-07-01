package br.edu.ifpb.cstsi.pss.scireview.service;

import br.edu.ifpb.cstsi.pss.scireview.model.Artigo;
import br.edu.ifpb.cstsi.pss.scireview.model.Evento;
import br.edu.ifpb.cstsi.pss.scireview.model.Revisao;
import br.edu.ifpb.cstsi.pss.scireview.model.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SistemaAvaliacao {

    private final List<Artigo> artigos = new ArrayList<>();
    private final List<Revisao> revisoes = new ArrayList<>();

    public void adicionarArtigo(Artigo artigo) {
        artigos.add(artigo);
    }

    public List<Artigo> listarTodosArtigos() {
        return new ArrayList<>(artigos);
    }

    public List<Artigo> getArtigosPorEvento(Evento evento) {
        return artigos.stream()
                .filter(artigo -> {
                    Evento eventoArtigo = artigo.getEvento();
                    return eventoArtigo != null && eventoArtigo.equals(evento);
                })
                .toList();
    }

    public Optional<Artigo> buscarArtigoPorId(String artigoId) {
        if (artigoId == null || artigoId.isBlank()) {
            return Optional.empty();
        }
        return artigos.stream()
                .filter(artigo -> artigo.getId().equals(artigoId.trim()))
                .findFirst();
    }

    public List<Revisao> getRevisoesPorArtigo(Artigo artigo) {
        return revisoes.stream()
                .filter(revisao -> revisao.getArtigo().equals(artigo))
                .toList();
    }

    public List<Usuario> getRevisoresPorArtigo(Artigo artigo) {
        return getRevisoesPorArtigo(artigo).stream()
                .map(Revisao::getRevisor)
                .toList();
    }

    public Optional<Revisao> buscarRevisao(Artigo artigo, Usuario revisor) {
        return revisoes.stream()
                .filter(revisao -> revisao.getArtigo().equals(artigo))
                .filter(revisao -> revisao.getRevisor().equals(revisor))
                .findFirst();
    }

    public List<Revisao> listarRevisoesPendentesDoRevisor(Usuario revisor) {
        return revisoes.stream()
                .filter(revisao -> revisao.getRevisor().equals(revisor))
                .filter(revisao -> !revisao.isConcluida())
                .toList();
    }

    public void adicionarRevisao(Revisao revisao) {
        revisoes.add(revisao);
    }

    public void limpar() {
        artigos.clear();
        revisoes.clear();
    }

    public List<Revisao> getTodasRevisoes() {
        return new ArrayList<>(revisoes);
    }
}
