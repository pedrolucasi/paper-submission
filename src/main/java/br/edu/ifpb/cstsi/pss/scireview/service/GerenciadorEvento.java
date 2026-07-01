package br.edu.ifpb.cstsi.pss.scireview.service;

import br.edu.ifpb.cstsi.pss.scireview.model.Evento;
import br.edu.ifpb.cstsi.pss.scireview.observer.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GerenciadorEvento {
    private Evento eventoAtual;
    private List<Observer> observadores = new ArrayList<>();

    public Evento startNovoEvento(String nome, String cidade, String periodo) {
        limparEstadoAnterior();
        eventoAtual = new Evento(nome, cidade, periodo);
        notificarObservadores("EVENTO_INICIADO", eventoAtual);
        return eventoAtual;
    }

    public void finalizarCicloRevisoes() {
        if (eventoAtual != null) {
            notificarObservadores("CICLO_REVISOES_FINALIZADO", eventoAtual);
        }
    }

    public void adicionarObserver(Observer observer) {
        observadores.add(observer);
    }

    public void removerObserver(Observer observer) {
        observadores.remove(observer);
    }

    private void notificarObservadores(String evento, Object dados) {
        for (Observer observer : observadores) {
            observer.atualizar(evento, dados);
        }
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