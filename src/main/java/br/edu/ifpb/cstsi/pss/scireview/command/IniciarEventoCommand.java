package br.edu.ifpb.cstsi.pss.scireview.command;

import br.edu.ifpb.cstsi.pss.scireview.model.Evento;
import br.edu.ifpb.cstsi.pss.scireview.model.Usuario;
import br.edu.ifpb.cstsi.pss.scireview.service.GerenciadorEvento;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class IniciarEventoCommand implements Command {
    private final GerenciadorEvento gerenciadorEvento;
    private final Usuario coordenador;
    private final String nome;
    private final String cidade;
    private final String periodo;
    private final LocalDate inicioSubmissao;
    private final LocalDate fimSubmissao;
    private final LocalDateTime dataExecucao;
    private Evento eventoCriado;

    public IniciarEventoCommand(GerenciadorEvento gerenciadorEvento, Usuario coordenador,
                                String nome, String cidade, String periodo,
                                LocalDate inicioSubmissao, LocalDate fimSubmissao) {
        this.gerenciadorEvento = gerenciadorEvento;
        this.coordenador = coordenador;
        this.nome = nome;
        this.cidade = cidade;
        this.periodo = periodo;
        this.inicioSubmissao = inicioSubmissao;
        this.fimSubmissao = fimSubmissao;
        this.dataExecucao = LocalDateTime.now();
    }

    @Override
    public void executar() {
        eventoCriado = gerenciadorEvento.startNovoEvento(nome, cidade, periodo, inicioSubmissao, fimSubmissao);
        System.out.println("[OK] Evento iniciado: " + nome);
        CommandHistory.getInstance().adicionar(this);
    }

    @Override
    public void desfazer() {
        if (eventoCriado != null) {
            System.out.println("[DESFAZER] Criacao do evento: " + nome);
            gerenciadorEvento.limparEstadoAnterior();
        }
    }

    @Override
    public String getDescricao() {
        return "Iniciar Evento: " + nome + " (" + cidade + ")";
    }

    @Override
    public LocalDateTime getDataExecucao() {
        return dataExecucao;
    }

    @Override
    public String getExecutor() {
        return coordenador != null ? coordenador.getEmail() : "Sistema";
    }

    @Override
    public boolean isReversivel() {
        return true;
    }
}