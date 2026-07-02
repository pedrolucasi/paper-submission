package br.edu.ifpb.cstsi.pss.scireview.presentation;

import br.edu.ifpb.cstsi.pss.scireview.model.AreaTematica;
import br.edu.ifpb.cstsi.pss.scireview.model.Artigo;
import br.edu.ifpb.cstsi.pss.scireview.model.Usuario;
import br.edu.ifpb.cstsi.pss.scireview.service.DistribuicaoRevisores;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DistribuicaoApresentador {

    private final SaidaConsole saida;

    public DistribuicaoApresentador(SaidaConsole saida) {
        this.saida = saida;
    }

    public void exibir(Map<Artigo, List<Usuario>> distribuicao, DistribuicaoRevisores distribuicaoRevisores) {
        saida.linha();
        saida.linha("+--------------------------------------------------------+");
        saida.linha("|           DISTRIBUICAO DE ARTIGOS POR REVISOR           |");
        saida.linha("+--------------------------------------------------------+");

        for (Map.Entry<Artigo, List<Usuario>> entry : distribuicao.entrySet()) {
            Artigo artigo = entry.getKey();
            List<Usuario> revisoresAtribuidos = entry.getValue();
            Set<AreaTematica> areasDoArtigo = artigo.getAreasTematicas();

            saida.linha();
            saida.linha("Artigo: " + artigo.getTitulo() + " (ID: " + artigo.getId() + ")");
            saida.linha("   Autor: " + artigo.getAutor().getEmail());
            saida.linha("   Coautores: " + artigo.getCoautores());

            if (revisoresAtribuidos.isEmpty()) {
                saida.linha("   Nenhum revisor disponivel para este artigo.");
            } else {
                saida.linha("   Revisores atribuidos:");
                for (Usuario revisor : revisoresAtribuidos) {
                    int afinidade = calcularAfinidade(revisor, areasDoArtigo);
                    int carga = distribuicaoRevisores.obterCargaPorRevisor(List.of(revisor)).get(revisor);
                    saida.linha("      - " + revisor.getEmail()
                            + " (afinidade: " + afinidade + ", carga total: " + carga + ")");
                }
            }
        }
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
}
