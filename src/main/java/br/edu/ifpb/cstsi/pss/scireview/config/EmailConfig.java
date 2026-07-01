package br.edu.ifpb.cstsi.pss.scireview.config;

public class EmailConfig {
    private static final String HOST = "smtp.gmail.com";
    private static final int PORT = 587;
    private static final String USERNAME = "pedro@gmail.com";
    private static final String PASSWORD = "123456";

    public static String getHost() { return HOST; }
    public static int getPort() { return PORT; }
    public static String getUsername() { return USERNAME; }
    public static String getPassword() { return PASSWORD; }
}