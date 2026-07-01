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
            if (status == StatusArtigo.ACEITO || status == StatusArtigo.REJEITADO) {
                count++;
            }
        }
        return count;
    }

    private Map<Usuario, List<Artigo>> mapearPendenciasPorRevisor(List<Artigo> artigos) {
        Map<Usuario, List<Artigo>> pendencias = new HashMap<>();

        for (Artigo artigo : artigos) {
            if (artigo.getStatus() == StatusArtigo.EM_REVISAO) {
                List<Usuario> revisoresDoArtigo = sistemaAvaliacao.getRevisoresPorArtigo(artigo);
                for (Usuario revisor : revisoresDoArtigo) {
                    pendencias.computeIfAbsent(revisor, k -> new ArrayList<>()).add(artigo);
                }
            }
        }

        return pendencias;
    }

    public void exibirDashboard() {
        DadosDashboard dados = consultarDados();

        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║                    📊 DASHBOARD                        ║");
        System.out.println("║            RESULTADOS DO EVENTO                       ║");
        System.out.println("╠════════════════════════════════════════════════════════╣");
        System.out.printf("║ 📌 Total de Artigos Submetidos: %-28d ║%n", dados.getTotalArtigos());
        System.out.printf("║ 👥 Total de Revisores: %-34d ║%n", dados.getTotalRevisores());
        System.out.printf("║ ✅ Artigos Avaliados: %-36d ║%n", dados.getArtigosAvaliados());
        System.out.printf("║ ⏳ Artigos Pendentes: %-36d ║%n", dados.getArtigosPendentes());
        System.out.println("╠════════════════════════════════════════════════════════╣");

        if (!dados.getPendenciasPorRevisor().isEmpty()) {
            System.out.println("║ 📋 PENDÊNCIAS POR REVISOR:                          ║");
            for (Map.Entry<Usuario, List<Artigo>> entry : dados.getPendenciasPorRevisor().entrySet()) {
                Usuario revisor = entry.getKey();
                List<Artigo> artigosPendentes = entry.getValue();
                System.out.printf("║   🔹 %-35s ║%n", 
                        revisor.getEmail().substring(0, Math.min(35, revisor.getEmail().length())));
                System.out.printf("║      %d artigo(s) pendente(s)%n", artigosPendentes.size());
                for (Artigo artigo : artigosPendentes) {
                    String titulo = artigo.getTitulo();
                    String info = "• ID: " + artigo.getId() + " | " + titulo;
                    System.out.printf("║      %-41s ║%n", 
                            info.substring(0, Math.min(41, info.length())));
                }
            }
        } else {
            System.out.println("║ 🎉 Nenhuma pendência!                               ║");
            System.out.println("║ Todos os artigos foram avaliados!                   ║");
        }
        System.out.println("╚════════════════════════════════════════════════════════╝\n");
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