package br.edu.ifpb.cstsi.pss.scireview.service;

import br.edu.ifpb.cstsi.pss.scireview.model.Artigo;
import br.edu.ifpb.cstsi.pss.scireview.model.Avaliacao;
import br.edu.ifpb.cstsi.pss.scireview.model.Evento;
import br.edu.ifpb.cstsi.pss.scireview.model.Notificacao;
import br.edu.ifpb.cstsi.pss.scireview.model.Revisao;
import br.edu.ifpb.cstsi.pss.scireview.model.Usuario;

import java.util.List;

public abstract class GeradorEmail {

    public final Notificacao gerarEmail(Artigo artigo, Evento evento, List<Revisao> revisoes, Usuario autor, String coordenador) {
        String cabecalho = gerarCabecalho(artigo, autor);
        String corpoMensagem = gerarCorpo(artigo, evento);
        String pareceres = gerarPareceres(revisoes);
        String assinatura = gerarAssinatura(evento, coordenador);

        String conteudoCompleto = cabecalho + "\n\n" + corpoMensagem + "\n\n" + pareceres + "\n\n" + assinatura;
        String titulo = "SciReview - Resultado da Submissao: " + artigo.getTitulo();

        return new Notificacao(autor.getEmail(), titulo, conteudoCompleto);
    }

    protected abstract String gerarCabecalho(Artigo artigo, Usuario autor);
    protected abstract String gerarCorpo(Artigo artigo, Evento evento);

    private String gerarPareceres(List<Revisao> revisoes) {
        StringBuilder sb = new StringBuilder();
        sb.append("--- PARECERES DOS REVISORES ---\n\n");

        for (int i = 0; i < revisoes.size(); i++) {
            Revisao revisao = revisoes.get(i);
            Avaliacao avaliacao = revisao.getAvaliacao();

            if (avaliacao != null) {
                sb.append("[Revisor ").append(i + 1).append("]\n");
                sb.append("Principal Contribuicao ou pontos positivos\n");
                sb.append("================================\n");
                sb.append(avaliacao.getContribuicoes()).append("\n\n");
                sb.append("Pontos negativos\n");
                sb.append("================================\n");
                sb.append(avaliacao.getPontosCritica()).append("\n\n");
                sb.append("Veredito: ").append(avaliacao.getVeredito()).append("\n\n");
            }
        }

        return sb.toString();
    }

    private String gerarAssinatura(Evento evento, String coordenador) {
        return "Atenciosamente,\n" +
               coordenador + "\n" +
               "Coordenadora do Comite de Programa do " + evento.getNome();
    }

    protected String getSaudacao(Usuario autor) {
        return "Prezado(a) " + autor.getInstituicao() + " " + autor.getEmail() + ":";
    }
}