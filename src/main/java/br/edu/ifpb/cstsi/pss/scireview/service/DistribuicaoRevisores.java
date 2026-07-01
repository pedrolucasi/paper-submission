package br.edu.ifpb.cstsi.pss.scireview.service;

import br.edu.ifpb.cstsi.pss.scireview.model.Artigo;
import br.edu.ifpb.cstsi.pss.scireview.model.AreaTematica;
import br.edu.ifpb.cstsi.pss.scireview.model.Revisao;
import br.edu.ifpb.cstsi.pss.scireview.model.Usuario;
import br.edu.ifpb.cstsi.pss.scireview.model.estado.StatusArtigo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DistribuicaoRevisores {

    private final SistemaAvaliacao sistemaAvaliacao;
    private final CadastroAreaTematica cadastroAreaTematica;

    public DistribuicaoRevisores(SistemaAvaliacao sistemaAvaliacao, CadastroAreaTematica cadastroAreaTematica) {
        this.sistemaAvaliacao = sistemaAvaliacao;
        this.cadastroAreaTematica = cadastroAreaTematica;
    }

    public Map<Artigo, List<Usuario>> distribuirArtigos(List<Artigo> artigos, List<Usuario> revisores) {
        if (artigos.isEmpty() || revisores.isEmpty()) {
            return new HashMap<>();
        }

        Map<Artigo, List<Usuario>> distribuicao = new LinkedHashMap<>();
        List<Usuario> revisoresDisponiveis = new ArrayList<>(revisores);

        for (Artigo artigo : artigos) {
            List<Usuario> revisoresParaArtigo = selecionarRevisores(artigo, revisoresDisponiveis);
            distribuicao.put(artigo, revisoresParaArtigo);
        }

        return distribuicao;
    }

    private List<Usuario> selecionarRevisores(Artigo artigo, List<Usuario> revisoresDisponiveis) {
        Set<String> autoresDoArtigo = obterAutoresDoArtigo(artigo);

        List<Usuario> revisoresElegiveis = revisoresDisponiveis.stream()
                .filter(revisor -> !autoresDoArtigo.contains(revisor.getEmail()))
                .collect(Collectors.toList());

        if (revisoresElegiveis.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Usuario, Integer> afinidadePorRevisor = new LinkedHashMap<>();
        Set<AreaTematica> areasDoArtigo = obterAreasDoArtigo(artigo);

        for (Usuario revisor : revisoresElegiveis) {
            int afinidade = calcularAfinidade(revisor, areasDoArtigo);
            afinidadePorRevisor.put(revisor, afinidade);
        }

        List<Usuario> revisoresOrdenados = revisoresElegiveis.stream()
                .sorted((r1, r2) -> Integer.compare(
                        afinidadePorRevisor.get(r2),
                        afinidadePorRevisor.get(r1)
                ))
                .collect(Collectors.toList());

        int quantidadeRevisores = calcularQuantidadeRevisores(artigo);
        return revisoresOrdenados.stream()
                .limit(quantidadeRevisores)
                .collect(Collectors.toList());
    }

    private int calcularQuantidadeRevisores(Artigo artigo) {
        return 2;
    }

    private Set<String> obterAutoresDoArtigo(Artigo artigo) {
        Set<String> autores = new HashSet<>();
        autores.add(artigo.getAutor().getEmail());
        autores.addAll(artigo.getCoautores());
        return autores;
    }

    private Set<AreaTematica> obterAreasDoArtigo(Artigo artigo) {
        Set<AreaTematica> areas = new HashSet<>();
        String titulo = artigo.getTitulo().toLowerCase();
        String resumo = artigo.getResumo().toLowerCase();

        for (AreaTematica area : cadastroAreaTematica.listar()) {
            String nomeArea = area.getNome().toLowerCase();
            if (titulo.contains(nomeArea) || resumo.contains(nomeArea)) {
                areas.add(area);
            }
        }

        if (areas.isEmpty()) {
            List<AreaTematica> todasAreas = new ArrayList<>(cadastroAreaTematica.listar());
            if (!todasAreas.isEmpty()) {
                areas.add(todasAreas.get(0));
            }
        }

        return areas;
    }

    private int calcularAfinidade(Usuario revisor, Set<AreaTematica> areasDoArtigo) {
        Set<AreaTematica> areasRevisor = revisor.getAreasDeInteresse();
        if (areasRevisor.isEmpty()) {
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

    public void distribuirAutomaticamente(List<Artigo> artigos, List<Usuario> revisores) {
        if (artigos.isEmpty() || revisores.isEmpty()) {
            System.out.println("Não há artigos ou revisores para distribuir.");
            return;
        }

        List<Usuario> revisoresCopia = new ArrayList<>(revisores);
        Collections.shuffle(revisoresCopia);

        int totalArtigos = artigos.size();
        int totalRevisores = revisoresCopia.size();
        int artigosPorRevisor = totalArtigos / totalRevisores;
        int sobra = totalArtigos % totalRevisores;

        Map<Usuario, List<Artigo>> cargaPorRevisor = new LinkedHashMap<>();
        for (Usuario revisor : revisoresCopia) {
            cargaPorRevisor.put(revisor, new ArrayList<>());
        }

        List<Artigo> artigosNaoDistribuidos = new ArrayList<>(artigos);
        int indiceRevisor = 0;

        for (Artigo artigo : artigos) {
            Usuario revisor = revisoresCopia.get(indiceRevisor % totalRevisores);
            cargaPorRevisor.get(revisor).add(artigo);
            indiceRevisor++;
        }

        for (Map.Entry<Usuario, List<Artigo>> entry : cargaPorRevisor.entrySet()) {
            Usuario revisor = entry.getKey();
            List<Artigo> artigosRevisor = entry.getValue();

            System.out.println("\n🔍 Revisor: " + revisor.getEmail());
            System.out.println("   Artigos atribuídos: " + artigosRevisor.size());

            for (Artigo artigo : artigosRevisor) {
                Set<AreaTematica> areasDoArtigo = obterAreasDoArtigo(artigo);
                Set<AreaTematica> areasRevisor = revisor.getAreasDeInteresse();
                int afinidade = calcularAfinidade(revisor, areasDoArtigo);

                String afinidadeTexto;
                if (afinidade >= 2) {
                    afinidadeTexto = "🔴 ALTA";
                } else if (afinidade == 1) {
                    afinidadeTexto = "🟡 MÉDIA";
                } else {
                    afinidadeTexto = "🟢 BAIXA";
                }

                if (artigo.getStatus() == StatusArtigo.SUBMETIDO) {
                    artigo.enviarParaRevisao();
                }

                Revisao revisao = new Revisao(artigo, revisor);
                sistemaAvaliacao.adicionarRevisao(revisao);

                System.out.println("   📄 " + artigo.getTitulo() + " (ID: " + artigo.getId() + ")");
                System.out.println("      Afinidade: " + afinidadeTexto + " (" + afinidade + " tema(s) compatível(is))");
                System.out.println("      Áreas do artigo: " + areasDoArtigo);
                System.out.println("      Áreas do revisor: " + areasRevisor);
            }
        }

        System.out.println("\n✅ Distribuição concluída!");
        System.out.println("📊 Resumo:");
        System.out.println("   Total de artigos: " + totalArtigos);
        System.out.println("   Total de revisores: " + totalRevisores);
        System.out.println("   Média de artigos por revisor: " + artigosPorRevisor + " (sobra: " + sobra + ")");
    }

    public void exibirDistribuicao(Map<Artigo, List<Usuario>> distribuicao) {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║           📋 DISTRIBUIÇÃO DE ARTIGOS POR REVISOR           ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");

        for (Map.Entry<Artigo, List<Usuario>> entry : distribuicao.entrySet()) {
            Artigo artigo = entry.getKey();
            List<Usuario> revisores = entry.getValue();

            System.out.println("\n📄 Artigo: " + artigo.getTitulo() + " (ID: " + artigo.getId() + ")");
            System.out.println("   Autor: " + artigo.getAutor().getEmail());
            System.out.println("   Coautores: " + artigo.getCoautores());

            if (revisores.isEmpty()) {
                System.out.println("   ⚠️ Nenhum revisor disponível para este artigo.");
            } else {
                System.out.println("   👥 Revisores atribuídos:");
                for (Usuario revisor : revisores) {
                    Set<AreaTematica> areasDoArtigo = obterAreasDoArtigo(artigo);
                    int afinidade = calcularAfinidade(revisor, areasDoArtigo);
                    System.out.println("      🔹 " + revisor.getEmail() + " (Afinidade: " + afinidade + ")");
                }
            }
        }
    }
}