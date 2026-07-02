package br.edu.ifpb.cstsi.pss.scireview.service;

import br.edu.ifpb.cstsi.pss.scireview.model.AreaTematica;
import br.edu.ifpb.cstsi.pss.scireview.model.Artigo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResolverAreasArtigo {

    private final CadastroAreaTematica cadastroAreaTematica;

    public ResolverAreasArtigo(CadastroAreaTematica cadastroAreaTematica) {
        this.cadastroAreaTematica = cadastroAreaTematica;
    }

    public Set<AreaTematica> resolver(Artigo artigo) {
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

    public List<String> nomesDasAreas(Artigo artigo) {
        return resolver(artigo).stream()
                .map(AreaTematica::getNome)
                .toList();
    }
}
