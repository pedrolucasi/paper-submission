package br.edu.ifpb.cstsi.pss.scireview.service;

import br.edu.ifpb.cstsi.pss.scireview.model.Artigo;
import br.edu.ifpb.cstsi.pss.scireview.model.Evento;
import br.edu.ifpb.cstsi.pss.scireview.model.Usuario;

public class EmailAceite extends GeradorEmail {

    @Override
    protected String gerarCabecalho(Artigo artigo, Usuario autor) {
        return getSaudacao(autor) + "\n\n" +
               "Parabéns! Sua submissão de nº " + artigo.getId() +
               ", intitulada \"" + artigo.getTitulo() + "\", para o " +
               artigo.getEvento().getNome() + " - " + artigo.getCategoria() + ", foi ACEITA.\n\n" +
               "As avaliações estão disponíveis abaixo.";
    }

    @Override
    protected String gerarCorpo(Artigo artigo, Evento evento) {
        return "Seu artigo foi aceito para publicação no evento.";
    }
}