package br.edu.ifpb.cstsi.pss.scireview.service;

import br.edu.ifpb.cstsi.pss.scireview.config.EmailConfig;
import br.edu.ifpb.cstsi.pss.scireview.model.Artigo;
import br.edu.ifpb.cstsi.pss.scireview.model.Evento;
import br.edu.ifpb.cstsi.pss.scireview.model.Notificacao;
import br.edu.ifpb.cstsi.pss.scireview.model.Revisao;
import br.edu.ifpb.cstsi.pss.scireview.model.Usuario;
import br.edu.ifpb.cstsi.pss.scireview.model.estado.StatusArtigo;
import br.edu.ifpb.cstsi.pss.scireview.observer.Observer;
import br.edu.ifpb.cstsi.pss.scireview.presentation.SaidaConsole;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;

public class ServicoEmail implements Observer {

    private final GerenciadorEvento gerenciadorEvento;
    private final CadastroUsuario cadastroUsuario;
    private final SistemaAvaliacao sistemaAvaliacao;
    private final SaidaConsole saida;
    private boolean enviarEmailReal;

    public ServicoEmail(GerenciadorEvento gerenciadorEvento,
                        CadastroUsuario cadastroUsuario,
                        SistemaAvaliacao sistemaAvaliacao) {
        this(gerenciadorEvento, cadastroUsuario, sistemaAvaliacao, new SaidaConsole());
    }

    public ServicoEmail(GerenciadorEvento gerenciadorEvento,
                        CadastroUsuario cadastroUsuario,
                        SistemaAvaliacao sistemaAvaliacao,
                        SaidaConsole saida) {
        this.gerenciadorEvento = gerenciadorEvento;
        this.cadastroUsuario = cadastroUsuario;
        this.sistemaAvaliacao = sistemaAvaliacao;
        this.saida = saida;
        this.enviarEmailReal = false;
    }

    public void ativarEmailReal() {
        this.enviarEmailReal = true;
        saida.linha("[EMAIL] Modo REAL ativado.");
    }

    public void desativarEmailReal() {
        this.enviarEmailReal = false;
        saida.linha("[EMAIL] Modo SIMULACAO ativado.");
    }

    @Override
    public void atualizar(String evento, Object dados) {
        if ("CICLO_REVISOES_FINALIZADO".equals(evento)) {
            Evento eventoAtual = (Evento) dados;
            saida.linha();
            saida.linha("[EMAIL] Notificando autores sobre o resultado das avaliacoes");
            notificarTodosAutores(eventoAtual);
        }
    }

    public void notificarRevisorAtribuicao(Usuario revisor, Artigo artigo, LocalDate prazoRevisao) {
        String titulo = "SciReview - Artigo atribuido para revisao";
        String conteudo = """
                Prezado(a) revisor,

                Voce foi designado para revisar o artigo "%s" (ID: %s).
                Prazo maximo para conclusao da revisao: %s.

                Atenciosamente,
                SciReview
                """.formatted(artigo.getTitulo(), artigo.getId(), prazoRevisao);

        Notificacao notificacao = new Notificacao(revisor.getEmail(), titulo, conteudo);
        enviar(notificacao);
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
                    enviar(notificacao);
                }
            }
        }
    }

    private void enviar(Notificacao notificacao) {
        if (enviarEmailReal) {
            enviarEmailReal(notificacao);
        } else {
            saida.simularEmail(
                    notificacao.getDestinatario(),
                    notificacao.getTitulo(),
                    notificacao.getConteudo());
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
        try {
            String host = EmailConfig.getHost();
            int port = EmailConfig.getPort();

            String username = System.getenv("EMAIL_USERNAME");
            String password = System.getenv("EMAIL_PASSWORD");

            if (username == null || username.isEmpty()) {
                username = EmailConfig.getUsername();
            }
            if (password == null || password.isEmpty()) {
                password = EmailConfig.getPassword();
            }

            if (username == null || username.isEmpty() || "seuemail@gmail.com".equals(username)) {
                saida.emailErroCredenciais();
                return;
            }

            final String finalUsername = username;
            final String finalPassword = password;

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
                    return new PasswordAuthentication(finalUsername, finalPassword);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(finalUsername));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(notificacao.getDestinatario()));
            message.setSubject(notificacao.getTitulo());
            message.setText(notificacao.getConteudo());

            Transport.send(message);

            saida.emailEnviado(notificacao.getDestinatario(), notificacao.getTitulo());

        } catch (MessagingException e) {
            saida.emailErroEnvio(e.getMessage());
        }
    }
}
