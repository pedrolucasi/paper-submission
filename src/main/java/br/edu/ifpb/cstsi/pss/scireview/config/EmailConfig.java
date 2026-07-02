package br.edu.ifpb.cstsi.pss.scireview.config;

public class EmailConfig {
    private static final String HOST = "smtp.gmail.com";
    private static final int PORT = 587;

    // Credenciais padrao (serao sobrescritas por variaveis de ambiente 
    private static final String USERNAME = "seuemail@gmail.com";
    private static final String PASSWORD = "suasenha";

    public static String getHost() { return HOST; }
    public static int getPort() { return PORT; }

    public static String getUsername() {
        String env = System.getenv("EMAIL_USERNAME");
        return (env != null && !env.isEmpty()) ? env : USERNAME;
    }

    public static String getPassword() {
        String env = System.getenv("EMAIL_PASSWORD");
        return (env != null && !env.isEmpty()) ? env : PASSWORD;
    }
}