package br.edu.ifpb.cstsi.pss.scireview.presentation;

public final class SaidaAplicacao {

    private static SaidaConsole saida = new SaidaConsole();

    private SaidaAplicacao() {
    }

    public static void configurar(SaidaConsole console) {
        saida = console;
    }

    public static SaidaConsole get() {
        return saida;
    }
}
