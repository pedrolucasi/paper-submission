package br.edu.ifpb.cstsi.pss.scireview.service;

import br.edu.ifpb.cstsi.pss.scireview.model.Evento;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GerenciadorEvento {

    private final List<Runnable> acoesAoLimparEstado = new ArrayList<>();
    private Evento eventoAtual;

    public Evento startNovoEvento(
            String nome,
            String cidade,
            String periodo,
            LocalDate inicioSubmissao,
            LocalDate fimSubmissao
    ) {
        limparEstadoAnterior();
        eventoAtual = new Evento(nome, cidade, periodo, inicioSubmissao, fimSubmissao);
        return eventoAtual;
    }

    public void aoLimparEstado(Runnable acao) {
        acoesAoLimparEstado.add(acao);
    }

    public Optional<Evento> getEventoAtual() {
        return Optional.ofNullable(eventoAtual);
    }

    public boolean possuiEventoAtivo() {
        return eventoAtual != null;
    }

    private void limparEstadoAnterior() {
        eventoAtual = null;
        acoesAoLimparEstado.forEach(Runnable::run);
    }
}
