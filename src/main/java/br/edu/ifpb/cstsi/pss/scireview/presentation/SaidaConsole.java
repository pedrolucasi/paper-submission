package br.edu.ifpb.cstsi.pss.scireview.presentation;

public class SaidaConsole {

    public void linha() {
        System.out.println();
    }

    public void linha(String mensagem) {
        System.out.println(mensagem);
    }

    public void formatado(String formato, Object... argumentos) {
        System.out.printf(formato, argumentos);
    }

    public void erro(String mensagem) {
        System.err.println(mensagem);
    }

    public void simularEmail(String destinatario, String titulo, String conteudo) {
        linha();
        linha("[EMAIL] ================================================");
        linha("[EMAIL] SIMULACAO DE ENVIO (NENHUM EMAIL FOI ENVIADO)");
        linha("[EMAIL] Para: " + destinatario);
        linha("[EMAIL] Assunto: " + titulo);
        linha("[EMAIL] Conteudo:");
        linha(conteudo);
        linha("[EMAIL] ================================================");
        linha();
    }

    public void emailEnviado(String destinatario, String titulo) {
        linha();
        linha("[EMAIL] ================================================");
        linha("[EMAIL] EMAIL REAL ENVIADO COM SUCESSO!");
        linha("[EMAIL] Para: " + destinatario);
        linha("[EMAIL] Assunto: " + titulo);
        linha("[EMAIL] ================================================");
        linha();
    }

    public void emailErroCredenciais() {
        linha();
        linha("[EMAIL] ERRO: Credenciais nao configuradas.");
        linha("[EMAIL] Configure as variaveis de ambiente EMAIL_USERNAME e EMAIL_PASSWORD");
        linha("[EMAIL] Ou edite o arquivo EmailConfig.java com suas credenciais.");
        linha("[EMAIL] NENHUM EMAIL FOI ENVIADO.");
        linha();
    }

    public void emailErroEnvio(String mensagem) {
        erro("");
        erro("[EMAIL] ================================================");
        erro("[EMAIL] ERRO AO ENVIAR EMAIL: " + mensagem);
        erro("[EMAIL] NENHUM EMAIL FOI ENVIADO.");
        erro("[EMAIL] Verifique suas credenciais no EmailConfig.java");
        erro("[EMAIL] ou configure as variaveis de ambiente.");
        erro("[EMAIL] ================================================");
        erro("");
    }
}
