package br.edu.ifpb.cstsi.pss.scireview.service;

import br.edu.ifpb.cstsi.pss.scireview.model.Artigo;
import br.edu.ifpb.cstsi.pss.scireview.model.Evento;
import br.edu.ifpb.cstsi.pss.scireview.model.Usuario;

public class EmailRejeicao extends GeradorEmail {

    @Override
    protected String gerarCabecalho(Artigo artigo, Usuario autor) {
        return getSaudacao(autor) + "\n\n" +
               "Lamentamos informar que seu artigo de nº " + artigo.getId() +
               " intitulado \"" + artigo.getTitulo() + "\" não pôde ser aceito para o " +
               artigo.getEvento().getNome() + " - " + artigo.getCategoria() + ".\n\n" +
               "Ao final do email, seguem os pareceres dos revisores, que esperamos que possam auxiliá-lo em futuras submissões.\n\n" +
               "Agradecemos sua submissão.";
    }

    @Override
    protected String gerarCorpo(Artigo artigo, Evento evento) {
        return "Infelizmente seu artigo não foi aceito para publicação.";
    }
}