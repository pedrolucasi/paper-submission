package br.edu.ifpb.cstsi.pss.scireview.presentation;

import br.edu.ifpb.cstsi.pss.scireview.dashboard.Dashboard;
import br.edu.ifpb.cstsi.pss.scireview.model.Artigo;
import br.edu.ifpb.cstsi.pss.scireview.model.Usuario;

import java.util.List;
import java.util.Map;

public class DashboardApresentador {

    private final SaidaConsole saida;

    public DashboardApresentador(SaidaConsole saida) {
        this.saida = saida;
    }

    public void exibir(Dashboard.DadosDashboard dados) {
        saida.linha();
        saida.linha("+--------------------------------------------------------+");
        saida.linha("|                    DASHBOARD                           |");
        saida.linha("|            RESULTADOS DO EVENTO                        |");
        saida.linha("+--------------------------------------------------------+");
        saida.formatado("| Total de Artigos Submetidos: %-28d |%n", dados.getTotalArtigos());
        saida.formatado("| Total de Revisores: %-34d |%n", dados.getTotalRevisores());
        saida.formatado("| Artigos Avaliados: %-36d |%n", dados.getArtigosAvaliados());
        saida.formatado("| Artigos Pendentes: %-36d |%n", dados.getArtigosPendentes());
        saida.linha("+--------------------------------------------------------+");

        if (!dados.getPendenciasPorRevisor().isEmpty()) {
            saida.linha("| PENDENCIAS POR REVISOR:                                |");
            for (Map.Entry<Usuario, List<Artigo>> entry : dados.getPendenciasPorRevisor().entrySet()) {
                Usuario revisor = entry.getKey();
                List<Artigo> artigosPendentes = entry.getValue();
                String email = revisor.getEmail().substring(0, Math.min(35, revisor.getEmail().length()));
                saida.formatado("|   - %-35s |%n", email);
                saida.formatado("|      %d artigo(s) pendente(s)%n", artigosPendentes.size());
                for (Artigo artigo : artigosPendentes) {
                    String info = "ID: " + artigo.getId() + " | " + artigo.getTitulo();
                    saida.formatado("|      %-41s |%n", info.substring(0, Math.min(41, info.length())));
                }
            }
        } else {
            saida.linha("| Nenhuma pendencia!                                       |");
            saida.linha("| Todos os artigos foram avaliados!                       |");
        }
        saida.linha("+--------------------------------------------------------+");
        saida.linha();
    }
}
