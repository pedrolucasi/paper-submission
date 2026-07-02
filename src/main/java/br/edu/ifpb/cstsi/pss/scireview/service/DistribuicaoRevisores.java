package br.edu.ifpb.cstsi.pss.scireview.service;

import br.edu.ifpb.cstsi.pss.scireview.model.AreaTematica;
import br.edu.ifpb.cstsi.pss.scireview.model.Artigo;
import br.edu.ifpb.cstsi.pss.scireview.model.Revisao;
import br.edu.ifpb.cstsi.pss.scireview.model.Usuario;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DistribuicaoRevisores {

    private static final int REVISORES_POR_ARTIGO = 2;

    private final SistemaAvaliacao sistemaAvaliacao;
    private final ResolverAreasArtigo resolverAreasArtigo;
    private final ServicoEmail servicoEmail;

    public DistribuicaoRevisores(SistemaAvaliacao sistemaAvaliacao, CadastroAreaTematica cadastroAreaTematica) {
        this(sistemaAvaliacao, new ResolverAreasArtigo(), null);
    }

    public DistribuicaoRevisores(SistemaAvaliacao sistemaAvaliacao,
                                 CadastroAreaTematica cadastroAreaTematica,
                                 ServicoEmail servicoEmail) {
        this(sistemaAvaliacao, new ResolverAreasArtigo(), servicoEmail);
    }

    DistribuicaoRevisores(SistemaAvaliacao sistemaAvaliacao,
                          ResolverAreasArtigo resolverAreasArtigo,
                          ServicoEmail servicoEmail) {
        this.sistemaAvaliacao = sistemaAvaliacao;
        this.resolverAreasArtigo = resolverAreasArtigo;
        this.servicoEmail = servicoEmail;
    }

    public Map<Artigo, List<Usuario>> distribuirArtigos(List<Artigo> artigos, List<Usuario> revisores) {
        if (artigos.isEmpty() || revisores.isEmpty()) {
            return new HashMap<>();
        }

        Map<Usuario, Integer> cargaPorRevisor = new HashMap<>();
        for (Usuario revisor : revisores) {
            cargaPorRevisor.put(revisor, 0);
        }

        Map<Artigo, List<Usuario>> distribuicao = new LinkedHashMap<>();

        for (Artigo artigo : artigos) {
            List<Usuario> revisoresSelecionados = selecionarRevisores(artigo, revisores, cargaPorRevisor);
            distribuicao.put(artigo, revisoresSelecionados);

            LocalDate prazoRevisao = calcularPrazoRevisao(artigo);
            for (Usuario revisor : revisoresSelecionados) {
                sistemaAvaliacao.adicionarRevisao(new Revisao(artigo, revisor));
                cargaPorRevisor.merge(revisor, 1, Integer::sum);
                notificarRevisor(revisor, artigo, prazoRevisao);
            }
        }

        return distribuicao;
    }

    private List<Usuario> selecionarRevisores(Artigo artigo,
                                              List<Usuario> revisores,
                                              Map<Usuario, Integer> cargaPorRevisor) {
        Set<String> autoresDoArtigo = obterAutoresDoArtigo(artigo);
        Set<AreaTematica> areasDoArtigo = resolverAreasArtigo.resolver(artigo);
        List<Usuario> selecionados = new ArrayList<>();

        Comparator<Usuario> porAfinidadeECarga = Comparator
                .comparingInt((Usuario revisor) -> calcularAfinidade(revisor, areasDoArtigo))
                .reversed()
                .thenComparingInt(revisor -> cargaPorRevisor.getOrDefault(revisor, 0))
                .thenComparing(Usuario::getEmail);

        for (int i = 0; i < REVISORES_POR_ARTIGO; i++) {
            Usuario escolhido = revisores.stream()
                    .filter(revisor -> !autoresDoArtigo.contains(revisor.getEmail()))
                    .filter(revisor -> !selecionados.contains(revisor))
                    .sorted(porAfinidadeECarga)
                    .findFirst()
                    .orElse(null);

            if (escolhido == null) {
                break;
            }
            selecionados.add(escolhido);
        }

        return selecionados;
    }

    private void notificarRevisor(Usuario revisor, Artigo artigo, LocalDate prazoRevisao) {
        if (servicoEmail != null) {
            servicoEmail.notificarRevisorAtribuicao(revisor, artigo, prazoRevisao);
        }
    }

    private LocalDate calcularPrazoRevisao(Artigo artigo) {
        return artigo.getEvento().getFimSubmissao().plusDays(30);
    }

    private Set<String> obterAutoresDoArtigo(Artigo artigo) {
        Set<String> autores = new HashSet<>();
        autores.add(artigo.getAutor().getEmail());
        autores.addAll(artigo.getCoautores());
        return autores;
    }

    private int calcularAfinidade(Usuario revisor, Set<AreaTematica> areasDoArtigo) {
        Set<AreaTematica> areasRevisor = revisor.getAreasDeInteresse();
        if (areasRevisor.isEmpty() || areasDoArtigo.isEmpty()) {
            return 0;
        }

        int afinidade = 0;
        for (AreaTematica areaArtigo : areasDoArtigo) {
            if (areasRevisor.contains(areaArtigo)) {
                afinidade++;
            }
        }
        return afinidade;
    }

    public void exibirDistribuicao(Map<Artigo, List<Usuario>> distribuicao) {
        System.out.println("\n+--------------------------------------------------------+");
        System.out.println("|           DISTRIBUICAO DE ARTIGOS POR REVISOR           |");
        System.out.println("+--------------------------------------------------------+");

        for (Map.Entry<Artigo, List<Usuario>> entry : distribuicao.entrySet()) {
            Artigo artigo = entry.getKey();
            List<Usuario> revisoresAtribuidos = entry.getValue();
            Set<AreaTematica> areasDoArtigo = resolverAreasArtigo.resolver(artigo);

            System.out.println("\nArtigo: " + artigo.getTitulo() + " (ID: " + artigo.getId() + ")");
            System.out.println("   Autor: " + artigo.getAutor().getEmail());
            System.out.println("   Coautores: " + artigo.getCoautores());

            if (revisoresAtribuidos.isEmpty()) {
                System.out.println("   Nenhum revisor disponivel para este artigo.");
            } else {
                System.out.println("   Revisores atribuidos:");
                for (Usuario revisor : revisoresAtribuidos) {
                    int afinidade = calcularAfinidade(revisor, areasDoArtigo);
                    int carga = (int) sistemaAvaliacao.getTodasRevisoes().stream()
                            .filter(r -> r.getRevisor().equals(revisor))
                            .count();
                    System.out.println("      - " + revisor.getEmail()
                            + " (afinidade: " + afinidade + ", carga total: " + carga + ")");
                }
            }
        }
    }
}
