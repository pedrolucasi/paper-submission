package br.edu.ifpb.cstsi.pss.scireview.service;

import br.edu.ifpb.cstsi.pss.scireview.config.EmailConfig;
import br.edu.ifpb.cstsi.pss.scireview.model.Artigo;
import br.edu.ifpb.cstsi.pss.scireview.model.Evento;
import br.edu.ifpb.cstsi.pss.scireview.model.Notificacao;
import br.edu.ifpb.cstsi.pss.scireview.model.Revisao;
import br.edu.ifpb.cstsi.pss.scireview.model.Usuario;
import br.edu.ifpb.cstsi.pss.scireview.model.estado.StatusArtigo;
import br.edu.ifpb.cstsi.pss.scireview.observer.Observer;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;

public class ServicoEmail implements Observer {

    private final GerenciadorEvento gerenciadorEvento;
    private final CadastroUsuario cadastroUsuario;
    private final SistemaAvaliacao sistemaAvaliacao;
    private boolean enviarEmailReal;

    public ServicoEmail(GerenciadorEvento gerenciadorEvento,
                        CadastroUsuario cadastroUsuario,
                        SistemaAvaliacao sistemaAvaliacao) {
        this.gerenciadorEvento = gerenciadorEvento;
        this.cadastroUsuario = cadastroUsuario;
        this.sistemaAvaliacao = sistemaAvaliacao;
        this.enviarEmailReal = false;
    }

    public void ativarEmailReal() {
        this.enviarEmailReal = true;
    }

    public void desativarEmailReal() {
        this.enviarEmailReal = false;
    }

    @Override
    public void atualizar(String evento, Object dados) {
        if ("CICLO_REVISOES_FINALIZADO".equals(evento)) {
            Evento eventoAtual = (Evento) dados;
            System.out.println("\n[EMAIL] Notificando autores sobre o resultado das avaliacoes");
            notificarTodosAutores(eventoAtual);
        }
    }

    public void notificarTodosAutores(Evento evento) {
        List<Artigo> artigos = sistemaAvaliacao.getArtigosPorEvento(evento);

        for (Artigo artigo : artigos) {
            StatusArtigo status = artigo.getStatus();
            if (status == StatusArtigo.ACEITO || status == StatusArtigo.REJEITADO) {
                Usuario autor = cadastroUsuario.buscarPorEmail(artigo.getEmailAutor()).orElse(null);
                if (autor != null) {
                    List<Revisao> revisoes = sistemaAvaliacao.getRevisoesPorArtigo(artigo);
                    String coordenador = obterNomeCoordenador();

                    GeradorEmail gerador = criarGeradorApropriado(artigo);
                    Notificacao notificacao = gerador.gerarEmail(artigo, evento, revisoes, autor, coordenador);

                    if (enviarEmailReal) {
                        enviarEmailReal(notificacao);
                    } else {
                        simularEnvioEmail(notificacao);
                    }
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

    private void simularEnvioEmail(Notificacao notificacao) {
        System.out.println("\n[EMAIL] ================================================");
        System.out.println("[EMAIL] SIMULACAO DE ENVIO");
        System.out.println("[EMAIL] Para: " + notificacao.getDestinatario());
        System.out.println("[EMAIL] Assunto: " + notificacao.getTitulo());
        System.out.println("[EMAIL] Conteudo:");
        System.out.println(notificacao.getConteudo());
        System.out.println("[EMAIL] ================================================\n");
    }

    private void enviarEmailReal(Notificacao notificacao) {
        try {
            String host = EmailConfig.getHost();
            int port = EmailConfig.getPort();
            String username = EmailConfig.getUsername();
            String password = EmailConfig.getPassword();

            Properties props = new Properties();
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
            props.put("mail.smtp.ssl.trust", host);

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(notificacao.getDestinatario()));
            message.setSubject(notificacao.getTitulo());
            message.setText(notificacao.getConteudo());

            Transport.send(message);

            System.out.println("\n[EMAIL] ================================================");
            System.out.println("[EMAIL] EMAIL REAL ENVIADO COM SUCESSO!");
            System.out.println("[EMAIL] Para: " + notificacao.getDestinatario());
            System.out.println("[EMAIL] Assunto: " + notificacao.getTitulo());
            System.out.println("[EMAIL] ================================================\n");

        } catch (MessagingException e) {
            System.err.println("[EMAIL] ERRO AO ENVIAR EMAIL: " + e.getMessage());
            System.err.println("[EMAIL] Verifique suas credenciais no EmailConfig.java");
            simularEnvioEmail(notificacao);
        }
    }
}