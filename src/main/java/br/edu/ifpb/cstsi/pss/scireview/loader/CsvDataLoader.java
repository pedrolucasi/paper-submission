package br.edu.ifpb.cstsi.pss.scireview.loader;

import br.edu.ifpb.cstsi.pss.scireview.exception.DadosInvalidosException;
import br.edu.ifpb.cstsi.pss.scireview.model.Papel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CsvDataLoader {

    private static final String DELIMITADOR = ";";
    private final String diretorioBase;

    public CsvDataLoader() {
        this("/dados");
    }

    public CsvDataLoader(String diretorioBase) {
        this.diretorioBase = diretorioBase.endsWith("/") ? diretorioBase : diretorioBase + "/";
    }

    public DadosCarregados carregar() {
        List<DadosUsuario> usuarios = carregarUsuarios();
        DadosEvento evento = carregarEvento();
        List<String> areas = carregarAreas();
        List<AssociacaoRevisorArea> associacoes = carregarAssociacoesRevisores();
        List<DadosArtigo> artigos = carregarArtigos();
        return new DadosCarregados(usuarios, evento, areas, associacoes, artigos);
    }

    private List<DadosUsuario> carregarUsuarios() {
        List<String[]> linhas = lerCsv("usuarios.csv");
        List<DadosUsuario> usuarios = new ArrayList<>();
        for (String[] campos : linhas) {
            validarQuantidadeCampos("usuarios.csv", campos, 4);
            Set<Papel> papeis = parsePapeis(campos[3]);
            usuarios.add(new DadosUsuario(campos[0], campos[1], campos[2], papeis));
        }
        return usuarios;
    }

    private DadosEvento carregarEvento() {
        List<String[]> linhas = lerCsv("evento.csv");
        if (linhas.isEmpty()) {
            throw new DadosInvalidosException("Arquivo evento.csv nao possui dados.");
        }
        String[] campos = linhas.get(0);
        validarQuantidadeCampos("evento.csv", campos, 5);
        return new DadosEvento(
                campos[0],
                campos[1],
                campos[2],
                Integer.parseInt(campos[3]),
                Integer.parseInt(campos[4])
        );
    }

    private List<String> carregarAreas() {
        List<String[]> linhas = lerCsv("areas.csv");
        List<String> areas = new ArrayList<>();
        for (String[] campos : linhas) {
            validarQuantidadeCampos("areas.csv", campos, 1);
            areas.add(campos[0]);
        }
        return areas;
    }

    private List<AssociacaoRevisorArea> carregarAssociacoesRevisores() {
        List<String[]> linhas = lerCsv("revisores_areas.csv");
        List<AssociacaoRevisorArea> associacoes = new ArrayList<>();
        for (String[] campos : linhas) {
            validarQuantidadeCampos("revisores_areas.csv", campos, 2);
            associacoes.add(new AssociacaoRevisorArea(campos[0], campos[1]));
        }
        return associacoes;
    }

    private List<DadosArtigo> carregarArtigos() {
        List<String[]> linhas = lerCsv("artigos.csv");
        List<DadosArtigo> artigos = new ArrayList<>();
        for (String[] campos : linhas) {
            validarQuantidadeCampos("artigos.csv", campos, 6);
            List<String> coautores = parseListaSeparadaPorPipe(campos[3]);
            boolean recomendado = Boolean.parseBoolean(campos[5]);
            artigos.add(new DadosArtigo(
                    campos[0],
                    campos[1],
                    campos[2],
                    coautores,
                    Integer.parseInt(campos[4]),
                    recomendado
            ));
        }
        return artigos;
    }

    private Set<Papel> parsePapeis(String valor) {
        return Arrays.stream(valor.split("\\|"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Papel::valueOf)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(Papel.class)));
    }

    private List<String> parseListaSeparadaPorPipe(String valor) {
        if (valor == null || valor.isBlank()) {
            return List.of();
        }
        return Arrays.stream(valor.split("\\|"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    private List<String[]> lerCsv(String nomeArquivo) {
        String caminho = diretorioBase + nomeArquivo;
        InputStream inputStream = getClass().getResourceAsStream(caminho);
        if (inputStream == null) {
            throw new DadosInvalidosException("Arquivo CSV nao encontrado no classpath: " + caminho);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            List<String[]> linhas = new ArrayList<>();
            String linha;
            boolean cabecalho = true;
            while ((linha = reader.readLine()) != null) {
                linha = linha.trim();
                if (linha.isEmpty() || linha.startsWith("#")) {
                    continue;
                }
                if (cabecalho) {
                    cabecalho = false;
                    continue;
                }
                linhas.add(parseLinha(linha));
            }
            return linhas;
        } catch (IOException e) {
            throw new DadosInvalidosException("Erro ao ler arquivo CSV: " + caminho, e);
        }
    }

    private String[] parseLinha(String linha) {
        return Arrays.stream(linha.split(DELIMITADOR, -1))
                .map(String::trim)
                .toArray(String[]::new);
    }

    private void validarQuantidadeCampos(String arquivo, String[] campos, int esperado) {
        if (campos.length < esperado) {
            throw new DadosInvalidosException(
                    "Linha invalida em " + arquivo + ": esperado " + esperado + " campos, encontrado " + campos.length);
        }
    }
}
