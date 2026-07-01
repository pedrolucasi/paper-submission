package br.edu.ifpb.cstsi.pss.scireview.service;

import br.edu.ifpb.cstsi.pss.scireview.exception.AcessoNaoAutorizadoException;
import br.edu.ifpb.cstsi.pss.scireview.exception.DadosInvalidosException;
import br.edu.ifpb.cstsi.pss.scireview.model.Evento;
import br.edu.ifpb.cstsi.pss.scireview.model.Papel;
import br.edu.ifpb.cstsi.pss.scireview.model.Usuario;
import br.edu.ifpb.cstsi.pss.scireview.model.categoria.CategoriaArtigo;

import java.util.Optional;

public class GerenciadorEvento {

    private Evento eventoAtual;

    public Evento startNovoEvento(String nome, String cidade, String periodo) {
        limparEstadoAnterior();
        eventoAtual = new Evento(nome, cidade, periodo);
        return eventoAtual;
    }

    public void definirCategoria(Usuario coordenador, CategoriaArtigo categoria) {
        if (coordenador == null || !coordenador.possuiPapel(Papel.COORDENADOR)) {
            throw new AcessoNaoAutorizadoException(
                    "Apenas o coordenador pode definir a categoria do evento.");
        }
        Evento evento = getEventoAtual().orElseThrow(() ->
                new DadosInvalidosException("Não há evento ativo para definir a categoria."));
        evento.definirCategoria(categoria);
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
