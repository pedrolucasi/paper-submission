package br.edu.ifpb.cstsi.pss.scireview.service;

import br.edu.ifpb.cstsi.pss.scireview.exception.AcessoNaoAutorizadoException;
import br.edu.ifpb.cstsi.pss.scireview.exception.DadosInvalidosException;
import br.edu.ifpb.cstsi.pss.scireview.exception.PrazoSubmissaoEncerradoException;
import br.edu.ifpb.cstsi.pss.scireview.model.AreaTematica;
import br.edu.ifpb.cstsi.pss.scireview.model.Artigo;
import br.edu.ifpb.cstsi.pss.scireview.model.Evento;
import br.edu.ifpb.cstsi.pss.scireview.model.Papel;
import br.edu.ifpb.cstsi.pss.scireview.model.Usuario;
import br.edu.ifpb.cstsi.pss.scireview.model.categoria.CategoriaArtigo;

import java.time.Clock;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class SubmissaoArtigo {

    private final GerenciadorEvento gerenciadorEvento;
    private final CadastroAreaTematica cadastroAreaTematica;
    private final CadastroUsuario cadastroUsuario;
    private final Map<String, Artigo> artigosPorId = new LinkedHashMap<>();
    private final Clock clock;

    public SubmissaoArtigo(GerenciadorEvento gerenciadorEvento) {
        this(gerenciadorEvento, null, null, Clock.systemDefaultZone());
    }

    public SubmissaoArtigo(GerenciadorEvento gerenciadorEvento, Clock clock) {
        this(gerenciadorEvento, null, null, clock);
    }

    public SubmissaoArtigo(GerenciadorEvento gerenciadorEvento,
                           CadastroAreaTematica cadastroAreaTematica,
                           CadastroUsuario cadastroUsuario) {
        this(gerenciadorEvento, cadastroAreaTematica, cadastroUsuario, Clock.systemDefaultZone());
    }

    public SubmissaoArtigo(GerenciadorEvento gerenciadorEvento,
                           CadastroAreaTematica cadastroAreaTematica,
                           CadastroUsuario cadastroUsuario,
                           Clock clock) {
        this.gerenciadorEvento = gerenciadorEvento;
        this.cadastroAreaTematica = cadastroAreaTematica;
        this.cadastroUsuario = cadastroUsuario;
        this.clock = clock;
        gerenciadorEvento.aoLimparEstado(artigosPorId::clear);
    }

    public Artigo submeter(Usuario autor, String nomeArtigo, String resumo,
                           List<String> coautores, List<String> nomesAreas, int quantidadePaginas) {
        validarAutor(autor);
        Evento eventoAtivo = obterEventoAtivo();
        validarPrazo(eventoAtivo);
        validarRegrasDaCategoria(eventoAtivo, resumo, quantidadePaginas);

        Set<AreaTematica> areas = resolverAreas(nomesAreas);
        List<String> coautoresValidados = validarCoautores(coautores, autor);

        String id = gerarIdUnico();
        Artigo artigo = new Artigo(id, nomeArtigo, resumo, coautoresValidados, areas, autor, eventoAtivo);
        artigosPorId.put(id, artigo);
        return artigo;
    }

    public List<Artigo> listarArtigosDoAutor(Usuario autor) {
        validarAutor(autor);
        return artigosPorId.values().stream()
                .filter(artigo -> artigo.getAutor().equals(autor))
                .toList();
    }

    private Set<AreaTematica> resolverAreas(List<String> nomesAreas) {
        if (cadastroAreaTematica == null) {
            throw new DadosInvalidosException("Cadastro de áreas temáticas não configurado.");
        }
        if (nomesAreas == null || nomesAreas.isEmpty()) {
            throw new DadosInvalidosException("Pelo menos uma área temática deve ser informada na submissão.");
        }

        Set<AreaTematica> areas = new LinkedHashSet<>();
        for (String nomeArea : nomesAreas) {
            AreaTematica area = cadastroAreaTematica.buscarPorNome(nomeArea)
                    .orElseThrow(() -> new DadosInvalidosException(
                            "Área temática não cadastrada: " + nomeArea));
            areas.add(area);
        }
        return Set.copyOf(areas);
    }

    private List<String> validarCoautores(List<String> coautores, Usuario autor) {
        if (cadastroUsuario == null) {
            throw new DadosInvalidosException("Cadastro de usuários não configurado.");
        }
        if (coautores == null || coautores.isEmpty()) {
            throw new DadosInvalidosException("Coautores são obrigatórios.");
        }

        Set<String> emailsUnicos = new HashSet<>();
        List<String> coautoresValidados = new java.util.ArrayList<>();
        String emailAutor = autor.getEmail().trim().toLowerCase();

        for (String coautor : coautores) {
            if (coautor == null || coautor.isBlank()) {
                throw new DadosInvalidosException("E-mail de coautor não pode ser vazio.");
            }
            String email = coautor.trim().toLowerCase();
            if (email.equals(emailAutor)) {
                throw new DadosInvalidosException("O autor não pode ser listado como coautor.");
            }
            if (!emailsUnicos.add(email)) {
                throw new DadosInvalidosException("Coautor duplicado na submissão: " + email);
            }
            if (!cadastroUsuario.emailJaCadastrado(email)) {
                throw new DadosInvalidosException("Coautor não cadastrado no sistema: " + email);
            }
            coautoresValidados.add(email);
        }
        return List.copyOf(coautoresValidados);
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

    private void validarRegrasDaCategoria(Evento evento, String resumo, int quantidadePaginas) {
        CategoriaArtigo categoria = evento.getCategoria().orElseThrow(() ->
                new DadosInvalidosException("A categoria do evento ainda não foi definida."));
        categoria.validarSubmissao(resumo, quantidadePaginas);
    }

    private String gerarIdUnico() {
        String id;
        do {
            id = UUID.randomUUID().toString();
        } while (artigosPorId.containsKey(id));
        return id;
    }
}
