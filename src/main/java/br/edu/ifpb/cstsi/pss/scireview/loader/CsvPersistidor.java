package br.edu.ifpb.cstsi.pss.scireview.loader;

import br.edu.ifpb.cstsi.pss.scireview.exception.DadosInvalidosException;
import br.edu.ifpb.cstsi.pss.scireview.model.Papel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Grava, no sistema de arquivos, os dados criados em tempo de execução — de modo
 * que novas submissões, usuários e áreas fiquem persistidos nos mesmos arquivos
 * CSV usados na carga inicial (E1). Trabalha sobre um diretório de filesystem
 * (por padrão, {@code src/main/resources/dados}).
 */
public class CsvPersistidor {

    private static final String DELIMITADOR = ";";
    private static final String SEPARADOR_LISTA = "|";

    private final Path base;

    public CsvPersistidor(Path base) {
        this.base = base;
    }

    public void anexarUsuario(String email, String senha, String instituicao, java.util.Set<Papel> papeis) {
        String papeisTexto = papeis.stream().map(Enum::name).collect(Collectors.joining(SEPARADOR_LISTA));
        anexarLinha("usuarios.csv", juntar(email, senha, instituicao, papeisTexto));
    }

    public void anexarArea(String nome) {
        anexarLinha("areas.csv", sanitizar(nome));
    }

    public void anexarAssociacaoRevisorArea(String emailRevisor, String area) {
        anexarLinha("revisores_areas.csv", juntar(emailRevisor, area));
    }

    public void anexarArtigo(String emailAutor, String titulo, String resumo,
                             List<String> coautores, List<String> areas,
                             int paginas, boolean recomendado) {
        anexarLinha("artigos.csv", juntar(
                emailAutor,
                titulo,
                resumo,
                juntarLista(coautores),
                juntarLista(areas),
                String.valueOf(paginas),
                String.valueOf(recomendado)));
    }

    public void reescreverEvento(String nome, String cidade, String periodo,
                                 int diasInicioAntesHoje, int diasFimDepoisHoje) {
        String cabecalho = "nome;cidade;periodo;dias_inicio_antes_hoje;dias_fim_depois_hoje";
        String linha = juntar(nome, cidade, periodo,
                String.valueOf(diasInicioAntesHoje), String.valueOf(diasFimDepoisHoje));
        try {
            Files.writeString(base.resolve("evento.csv"), cabecalho + System.lineSeparator() + linha + System.lineSeparator(),
                    StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new DadosInvalidosException("Erro ao gravar evento.csv", e);
        }
    }

    private void anexarLinha(String arquivo, String linha) {
        Path caminho = base.resolve(arquivo);
        try {
            StringBuilder conteudo = new StringBuilder();
            if (Files.exists(caminho) && Files.size(caminho) > 0) {
                byte[] atual = Files.readAllBytes(caminho);
                if (atual[atual.length - 1] != '\n') {
                    conteudo.append(System.lineSeparator());
                }
            }
            conteudo.append(linha).append(System.lineSeparator());
            Files.writeString(caminho, conteudo.toString(),
                    StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new DadosInvalidosException("Erro ao gravar " + arquivo, e);
        }
    }

    private String juntar(String... campos) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < campos.length; i++) {
            if (i > 0) {
                sb.append(DELIMITADOR);
            }
            sb.append(sanitizar(campos[i]));
        }
        return sb.toString();
    }

    private String juntarLista(List<String> valores) {
        return valores.stream().map(this::sanitizar).collect(Collectors.joining(SEPARADOR_LISTA));
    }

    private String sanitizar(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.trim().replace(DELIMITADOR, ",").replace(SEPARADOR_LISTA, "/");
    }
}
