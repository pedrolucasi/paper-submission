package br.edu.ifpb.cstsi.pss.scireview.service;

import br.edu.ifpb.cstsi.pss.scireview.model.Artigo;
import br.edu.ifpb.cstsi.pss.scireview.model.Evento;
import br.edu.ifpb.cstsi.pss.scireview.model.Usuario;

public class EmailRejeicao extends GeradorEmail {

    @Override
    protected String gerarCabecalho(Artigo artigo, Usuario autor) {
        return getSaudacao(autor) + "\n\n" +
               "Lamentamos informar que seu artigo de no " + artigo.getId() +
               " intitulado \"" + artigo.getTitulo() + "\" nao pode ser aceito para o " +
               artigo.getEvento().getNome() + " - " + artigo.getCategoria() + ".\n\n" +
               "Ao final do email, seguem os pareceres dos revisores, que esperamos que possam auxilia-lo em futuras submissoes.\n\n" +
               "Agradecemos sua submissao.";
    }

    @Override
    protected String gerarCorpo(Artigo artigo, Evento evento) {
        return "Infelizmente seu artigo nao foi aceito para publicacao no evento " + evento.getNome() + ".";
    }
}