package br.edu.ifpb.cstsi.pss.scireview;

import br.edu.ifpb.cstsi.pss.scireview.presentation.SaidaAplicacao;
import br.edu.ifpb.cstsi.pss.scireview.presentation.SaidaConsole;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        SaidaConsole saida = new SaidaConsole();
        SaidaAplicacao.configurar(saida);

        saida.linha("SciReview - Sistema de Submissao e Avaliacao de Artigos");
        saida.linha("=======================================================");
        saida.linha();
        saida.linha("Escolha o modo de execucao:");
        saida.linha("  1) Modo exemplo    (demonstracao automatica de todos os RFs)");
        saida.linha("  2) Modo interativo (uso manual via terminal)");
        saida.linha();

        try (Scanner scanner = new Scanner(System.in)) {
            saida.linha("Opcao (1/2):");
            String opcao = scanner.hasNextLine() ? scanner.nextLine().trim() : "1";
            saida.linha();

            if ("2".equals(opcao)) {
                new ModoInterativo(scanner, saida).executar();
            } else {
                ModoExemplo.executar();
            }
        }
    }
}
