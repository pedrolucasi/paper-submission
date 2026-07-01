package br.edu.ifpb.cstsi.pss.scireview.service;

import br.edu.ifpb.cstsi.pss.scireview.exception.AcessoNaoAutorizadoException;
import br.edu.ifpb.cstsi.pss.scireview.exception.DadosInvalidosException;
import br.edu.ifpb.cstsi.pss.scireview.model.Evento;
import br.edu.ifpb.cstsi.pss.scireview.model.Papel;
import br.edu.ifpb.cstsi.pss.scireview.model.Usuario;
import br.edu.ifpb.cstsi.pss.scireview.model.categoria.CategoriaArtigo;
import br.edu.ifpb.cstsi.pss.scireview.observer.Observer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GerenciadorEvento {

    private Evento eventoAtual;
    private List<Observer> observadores = new ArrayList<>();
    private final List<Runnable> acoesAoLimparEstado = new ArrayList<>();

    public Evento startNovoEvento(
            String nome,
            String cidade,
            String periodo,
            LocalDate inicioSubmissao,
            LocalDate fimSubmissao
    ) {
        limparEstadoAnterior();
        eventoAtual = new Evento(nome, cidade, periodo, inicioSubmissao, fimSubmissao);
        notificarObservadores("EVENTO_INICIADO", eventoAtual);
        return eventoAtual;
    }

    public void definirCategoria(Usuario coordenador, CategoriaArtigo categoria) {
        if (coordenador == null || !coordenador.possuiPapel(Papel.COORDENADOR)) {
            throw new AcessoNaoAutorizadoException(
                    "Apenas o coordenador pode definir a categoria do evento.");
        }
        Evento evento = getEventoAtual().orElseThrow(() ->
                new DadosInvalidosException("Nao ha evento ativo para definir a categoria."));
        evento.definirCategoria(categoria);
    }

    public void finalizarCicloRevisoes() {
        if (eventoAtual != null) {
            notificarObservadores("CICLO_REVISOES_FINALIZADO", eventoAtual);
        }
    }

    public void aoLimparEstado(Runnable acao) {
        acoesAoLimparEstado.add(acao);
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

    public void limparEstadoAnterior() {
        eventoAtual = null;
        acoesAoLimparEstado.forEach(Runnable::run);
    }

    private void limparEstadoInterno() {
        eventoAtual = null;
        acoesAoLimparEstado.forEach(Runnable::run);
    }
}