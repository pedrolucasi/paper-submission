package br.edu.ifpb.cstsi.pss.scireview.exception;

public class ParecerJaEmitidoException extends RuntimeException {

    public ParecerJaEmitidoException() {
        super("Parecer já foi emitido para este artigo.");
    }
}
