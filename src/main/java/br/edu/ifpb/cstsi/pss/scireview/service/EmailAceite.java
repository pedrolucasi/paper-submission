package br.edu.ifpb.cstsi.pss.scireview.service;

import br.edu.ifpb.cstsi.pss.scireview.model.Artigo;
import br.edu.ifpb.cstsi.pss.scireview.model.Evento;
import br.edu.ifpb.cstsi.pss.scireview.model.Usuario;

public class EmailAceite extends GeradorEmail {

    @Override
    protected String gerarCabecalho(Artigo artigo, Usuario autor) {
        return getSaudacao(autor) + "\n\n" +
               "Parabens! Sua submissao de no " + artigo.getId() +
               ", intitulada \"" + artigo.getTitulo() + "\", para o " +
               artigo.getEvento().getNome() + " - " + artigo.getCategoria() + ", foi ACEITA.\n\n" +
               "As avaliacoes estao disponiveis abaixo.";
    }

    @Override
    protected String gerarCorpo(Artigo artigo, Evento evento) {
        return "Seu artigo foi aceito para publicacao no evento " + evento.getNome() + ".";
    }
}