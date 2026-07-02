package br.edu.ifpb.cstsi.pss.scireview.service;

import br.edu.ifpb.cstsi.pss.scireview.model.AreaTematica;
import br.edu.ifpb.cstsi.pss.scireview.model.Artigo;

import java.util.List;
import java.util.Set;

public class ResolverAreasArtigo {

    public Set<AreaTematica> resolver(Artigo artigo) {
        return artigo.getAreasTematicas();
    }

    public List<String> nomesDasAreas(Artigo artigo) {
        return artigo.getAreasTematicas().stream()
                .map(AreaTematica::getNome)
                .toList();
    }
}
