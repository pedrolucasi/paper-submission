package br.edu.ifpb.cstsi.pss.scireview.dashboard;

import br.edu.ifpb.cstsi.pss.scireview.model.Artigo;
import br.edu.ifpb.cstsi.pss.scireview.model.Papel;
import br.edu.ifpb.cstsi.pss.scireview.model.Revisao;
import br.edu.ifpb.cstsi.pss.scireview.model.Usuario;
import br.edu.ifpb.cstsi.pss.scireview.model.estado.StatusArtigo;
import br.edu.ifpb.cstsi.pss.scireview.service.CadastroUsuario;
import br.edu.ifpb.cstsi.pss.scireview.service.SistemaAvaliacao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Dashboard {

    private SistemaAvaliacao sistemaAvaliacao;
    private CadastroUsuario cadastroUsuario;

    public Dashboard(SistemaAvaliacao sistemaAvaliacao, CadastroUsuario cadastroUsuario) {
        this.sistemaAvaliacao = sistemaAvaliacao;
        this.cadastroUsuario = cadastroUsuario;
    }

    public DadosDashboard consultarDados() {
        List<Artigo> todosArtigos = sistemaAvaliacao.listarTodosArtigos();
        List<Usuario> todosUsuarios = cadastroUsuario.listarTodos();
        List<Usuario> revisores = todosUsuarios.stream()
                .filter(u -> u.possuiPapel(Papel.REVISOR))
                .collect(Collectors.toList());

        int totalArtigos = todosArtigos.size();
        int totalRevisores = revisores.size();
        int artigosAvaliados = contarArtigosAvaliados(todosArtigos);
        int artigosPendentes = totalArtigos - artigosAvaliados;

        Map<Usuario, List<Artigo>> pendenciasPorRevisor = mapearPendenciasPorRevisor(todosArtigos);

        return new DadosDashboard(
                totalArtigos,
                totalRevisores,
                artigosAvaliados,
                artigosPendentes,
                pendenciasPorRevisor
        );
    }

    private int contarArtigosAvaliados(List<Artigo> artigos) {
        int count = 0;
        for (Artigo artigo : artigos) {
            StatusArtigo status = artigo.getStatus();
            if (status == StatusArtigo.REVISADO
                    || status == StatusArtigo.ACEITO
                    || status == StatusArtigo.REJEITADO) {
                count++;
            }
        }
        return count;
    }

    private Map<Usuario, List<Artigo>> mapearPendenciasPorRevisor(List<Artigo> artigos) {
        Map<Usuario, List<Artigo>> pendencias = new HashMap<>();

        for (Revisao revisao : obterRevisoesPendentes()) {
            Usuario revisor = revisao.getRevisor();
            Artigo artigo = revisao.getArtigo();
            pendencias.computeIfAbsent(revisor, k -> new ArrayList<>()).add(artigo);
        }

        return pendencias;
    }

    private List<Revisao> obterRevisoesPendentes() {
        return sistemaAvaliacao.listarTodosArtigos().stream()
                .flatMap(artigo -> sistemaAvaliacao.getRevisoesPorArtigo(artigo).stream())
                .filter(revisao -> !revisao.isConcluida())
                .toList();
    }

    public static class DadosDashboard {
        private final int totalArtigos;
        private final int totalRevisores;
        private final int artigosAvaliados;
        private final int artigosPendentes;
        private final Map<Usuario, List<Artigo>> pendenciasPorRevisor;

        public DadosDashboard(int totalArtigos, int totalRevisores,
                             int artigosAvaliados, int artigosPendentes,
                             Map<Usuario, List<Artigo>> pendenciasPorRevisor) {
            this.totalArtigos = totalArtigos;
            this.totalRevisores = totalRevisores;
            this.artigosAvaliados = artigosAvaliados;
            this.artigosPendentes = artigosPendentes;
            this.pendenciasPorRevisor = pendenciasPorRevisor;
        }

        public int getTotalArtigos() { return totalArtigos; }
        public int getTotalRevisores() { return totalRevisores; }
        public int getArtigosAvaliados() { return artigosAvaliados; }
        public int getArtigosPendentes() { return artigosPendentes; }
        public Map<Usuario, List<Artigo>> getPendenciasPorRevisor() { return pendenciasPorRevisor; }
    }
}