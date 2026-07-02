package br.edu.ifpb.cstsi.pss.scireview.command;

import br.edu.ifpb.cstsi.pss.scireview.model.Artigo;
import br.edu.ifpb.cstsi.pss.scireview.model.Usuario;
import br.edu.ifpb.cstsi.pss.scireview.service.SubmissaoArtigo;

import java.time.LocalDateTime;
import java.util.List;

public class SubmeterArtigoCommand implements Command {
    private final SubmissaoArtigo submissaoArtigo;
    private final Usuario autor;
    private final String nomeArtigo;
    private final String resumo;
    private final List<String> coautores;
    private final List<String> areas;
    private final int quantidadePaginas;
    private final LocalDateTime dataExecucao;
    private Artigo artigoSubmetido;

    public SubmeterArtigoCommand(SubmissaoArtigo submissaoArtigo, Usuario autor,
                                 String nomeArtigo, String resumo, List<String> coautores,
                                 List<String> areas, int quantidadePaginas) {
        this.submissaoArtigo = submissaoArtigo;
        this.autor = autor;
        this.nomeArtigo = nomeArtigo;
        this.resumo = resumo;
        this.coautores = coautores;
        this.areas = areas;
        this.quantidadePaginas = quantidadePaginas;
        this.dataExecucao = LocalDateTime.now();
    }

    @Override
    public void executar() {
        artigoSubmetido = submissaoArtigo.submeter(
                autor, nomeArtigo, resumo, coautores, areas, quantidadePaginas);
        System.out.println("[OK] Artigo submetido: " + nomeArtigo + " (ID: " + artigoSubmetido.getId() + ")");
        CommandHistory.getInstance().adicionar(this);
    }

    @Override
    public void desfazer() {
        if (artigoSubmetido != null) {
            System.out.println("[DESFAZER] Submissao do artigo: " + nomeArtigo);
        }
    }

    @Override
    public String getDescricao() {
        return "Submeter Artigo: " + nomeArtigo + " (por: " + autor.getEmail() + ")";
    }

    @Override
    public LocalDateTime getDataExecucao() {
        return dataExecucao;
    }

    @Override
    public String getExecutor() {
        return autor != null ? autor.getEmail() : "Sistema";
    }

    @Override
    public boolean isReversivel() {
        return true;
    }

    public Artigo getArtigoSubmetido() {
        return artigoSubmetido;
    }
}
