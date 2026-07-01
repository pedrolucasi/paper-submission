package br.edu.ifpb.cstsi.pss.scireview.command;

import java.time.LocalDateTime;

public interface Command {
    void executar();
    void desfazer();
    String getDescricao();
    LocalDateTime getDataExecucao();
    String getExecutor();
    boolean isReversivel();
}