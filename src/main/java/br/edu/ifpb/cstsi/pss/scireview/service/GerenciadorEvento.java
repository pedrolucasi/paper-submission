package br.edu.ifpb.cstsi.pss.scireview.service;

import br.edu.ifpb.cstsi.pss.scireview.model.Evento;

import java.util.Optional;

public class GerenciadorEvento {

    private Evento eventoAtual;

    public Evento startNovoEvento(String nome, String cidade, String periodo) {
        limparEstadoAnterior();
        eventoAtual = new Evento(nome, cidade, periodo);
        return eventoAtual;
    }

    public Optional<Evento> getEventoAtual() {
        return Optional.ofNullable(eventoAtual);
    }

    public boolean possuiEventoAtivo() {
        return eventoAtual != null;
    }

    private void limparEstadoAnterior() {
        eventoAtual = null;
    }
}
