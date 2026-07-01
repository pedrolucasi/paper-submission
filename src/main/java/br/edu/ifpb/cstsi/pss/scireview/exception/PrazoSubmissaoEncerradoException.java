package br.edu.ifpb.cstsi.pss.scireview.exception;

public class PrazoSubmissaoEncerradoException extends RuntimeException {

    public PrazoSubmissaoEncerradoException() {
        super("Submissões estão fora do prazo do evento ativo.");
    }
}
