package br.edu.ifpb.cstsi.pss.scireview.service;

import br.edu.ifpb.cstsi.pss.scireview.model.Artigo;
import br.edu.ifpb.cstsi.pss.scireview.model.Evento;
import br.edu.ifpb.cstsi.pss.scireview.model.Revisao;
import br.edu.ifpb.cstsi.pss.scireview.model.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SistemaAvaliacao {
    private List<Artigo> artigos = new ArrayList<>();
    private List<Revisao> revisoes = new ArrayList<>();

    public void adicionarArtigo(Artigo artigo) {
        artigos.add(artigo);
    }

    public List<Artigo> listarTodosArtigos() {
        return new ArrayList<>(artigos);
    }

    public List<Artigo> getArtigosPorEvento(Evento evento) {
        return artigos.stream()
                .filter(a -> a.getEvento().equals(evento))
                .collect(Collectors.toList());
    }

    public List<Revisao> getRevisoesPorArtigo(Artigo artigo) {
        return revisoes.stream()
                .filter(r -> r.getArtigo().equals(artigo))
                .collect(Collectors.toList());
    }

    public List<Usuario> getRevisoresPorArtigo(Artigo artigo) {
        return getRevisoesPorArtigo(artigo).stream()
                .map(Revisao::getRevisor)
                .collect(Collectors.toList());
    }

    public void adicionarRevisao(Revisao revisao) {
        revisoes.add(revisao);
    }
}