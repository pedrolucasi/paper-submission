package br.edu.ifpb.cstsi.pss.scireview.service;

import br.edu.ifpb.cstsi.pss.scireview.exception.AcessoNaoAutorizadoException;
import br.edu.ifpb.cstsi.pss.scireview.exception.DadosInvalidosException;
import br.edu.ifpb.cstsi.pss.scireview.exception.PrazoSubmissaoEncerradoException;
import br.edu.ifpb.cstsi.pss.scireview.model.Artigo;
import br.edu.ifpb.cstsi.pss.scireview.model.Evento;
import br.edu.ifpb.cstsi.pss.scireview.model.Papel;
import br.edu.ifpb.cstsi.pss.scireview.model.Usuario;

import java.time.Clock;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SubmissaoArtigo {

    private final GerenciadorEvento gerenciadorEvento;
    private final Map<String, Artigo> artigosPorId = new LinkedHashMap<>();
    private final Clock clock;

    public SubmissaoArtigo(GerenciadorEvento gerenciadorEvento) {
        this(gerenciadorEvento, Clock.systemDefaultZone());
    }

    public SubmissaoArtigo(GerenciadorEvento gerenciadorEvento, Clock clock) {
        this.gerenciadorEvento = gerenciadorEvento;
        this.clock = clock;
        gerenciadorEvento.aoLimparEstado(artigosPorId::clear);
    }

    public Artigo submeter(Usuario autor, String nomeArtigo, String resumo, List<String> coautores) {
        validarAutor(autor);
        Evento eventoAtivo = obterEventoAtivo();
        validarPrazo(eventoAtivo);

        String id = gerarIdUnico();
        Artigo artigo = new Artigo(id, nomeArtigo, resumo, coautores, autor);
        artigosPorId.put(id, artigo);
        return artigo;
    }

    public List<Artigo> listarArtigosDoAutor(Usuario autor) {
        validarAutor(autor);
        return artigosPorId.values().stream()
                .filter(artigo -> artigo.getAutor().equals(autor))
                .toList();
    }

    private void validarAutor(Usuario autor) {
        if (autor == null || !autor.possuiPapel(Papel.AUTOR)) {
            throw new AcessoNaoAutorizadoException(
                    "Apenas usuários com o papel de autor podem submeter ou consultar artigos.");
        }
    }

    private Evento obterEventoAtivo() {
        return gerenciadorEvento.getEventoAtual()
                .orElseThrow(() -> new DadosInvalidosException(
                        "Não há evento ativo para receber submissões."));
    }

    private void validarPrazo(Evento evento) {
        LocalDate hoje = LocalDate.now(clock);
        if (!evento.estaDentroDoPrazo(hoje)) {
            throw new PrazoSubmissaoEncerradoException();
        }
    }

    private String gerarIdUnico() {
        String id;
        do {
            id = UUID.randomUUID().toString();
        } while (artigosPorId.containsKey(id));
        return id;
    }
}
