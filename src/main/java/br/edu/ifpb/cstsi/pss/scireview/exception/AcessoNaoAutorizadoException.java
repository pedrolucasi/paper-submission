package br.edu.ifpb.cstsi.pss.scireview.exception;

public class AcessoNaoAutorizadoException extends RuntimeException {

    public AcessoNaoAutorizadoException(String mensagem) {
        super(mensagem);
    }
}
