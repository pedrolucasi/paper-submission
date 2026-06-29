package br.edu.ifpb.cstsi.pss.scireview.exception;

public class EmailDuplicadoException extends RuntimeException {

    public EmailDuplicadoException(String email) {
        super("E-mail já cadastrado: " + email);
    }
}
