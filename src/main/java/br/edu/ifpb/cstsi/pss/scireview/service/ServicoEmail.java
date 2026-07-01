package br.edu.ifpb.cstsi.pss.scireview.service;

import java.util.List;

import br.edu.ifpb.cstsi.pss.scireview.model.Artigo;
import br.edu.ifpb.cstsi.pss.scireview.model.Evento;
import br.edu.ifpb.cstsi.pss.scireview.model.Notificacao;
import br.edu.ifpb.cstsi.pss.scireview.model.Revisao;
import br.edu.ifpb.cstsi.pss.scireview.model.Usuario;
import br.edu.ifpb.cstsi.pss.scireview.model.estado.StatusArtigo;
import br.edu.ifpb.cstsi.pss.scireview.observer.Observer;

public class ServicoEmail implements Observer {

    private GerenciadorEvento gerenciadorEvento;
    private CadastroUsuario cadastroUsuario;
    private SistemaAvaliacao sistemaAvaliacao;

    public ServicoEmail(GerenciadorEvento gerenciadorEvento,
                        CadastroUsuario cadastroUsuario,
                        SistemaAvaliacao sistemaAvaliacao) {
        this.gerenciadorEvento = gerenciadorEvento;
        this.cadastroUsuario = cadastroUsuario;
        this.sistemaAvaliacao = sistemaAvaliacao;
    }

    @Override
    public void atualizar(String evento, Object dados) {
        if ("CICLO_REVISOES_FINALIZADO".equals(evento)) {
            Evento eventoAtual = (Evento) dados;
            System.out.println("\n>>> NOTIFICANDO AUTORES SOBRE O RESULTADO DAS AVALIAÇÕES <<<\n");
            notificarTodosAutores(eventoAtual);
        }
    }

    public void notificarTodosAutores(Evento evento) {
        List<Artigo> artigos = sistemaAvaliacao.getArtigosPorEvento(evento);

        for (Artigo artigo : artigos) {
            if (artigo.getStatus() == StatusArtigo.ACEITO || artigo.getStatus() == StatusArtigo.REJEITADO) {
                Usuario autor = cadastroUsuario.buscarPorEmail(artigo.getEmailAutor()).orElse(null);
                if (autor != null) {
                    List<Revisao> revisoes = sistemaAvaliacao.getRevisoesPorArtigo(artigo);
                    String coordenador = obterNomeCoordenador();

                    GeradorEmail gerador = criarGeradorApropriado(artigo);
                    Notificacao notificacao = gerador.gerarEmail(artigo, evento, revisoes, autor, coordenador);

                    enviarEmailReal(notificacao);
                }
            }
        }
    }

    private GeradorEmail criarGeradorApropriado(Artigo artigo) {
        if (artigo.getStatus() == StatusArtigo.ACEITO) {
            return new EmailAceite();
        } else {
            return new EmailRejeicao();
        }
    }

    private String obterNomeCoordenador() {
        return cadastroUsuario.buscarPorEmail("coordenador@evento.com")
                .map(Usuario::getEmail)
                .orElse("Coordenador do Evento");
    }

    private void enviarEmailReal(Notificacao notificacao) {
        System.out.println("\n=== ENVIANDO E-MAIL REAL ===");
        System.out.println("Para: " + notificacao.getDestinatario());
        System.out.println("Assunto: " + notificacao.getTitulo());
        System.out.println("Conteúdo:\n" + notificacao.getConteudo());
        System.out.println("================================\n");
    }
}