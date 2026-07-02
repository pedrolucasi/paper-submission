package br.edu.ifpb.cstsi.pss.scireview.service;

import br.edu.ifpb.cstsi.pss.scireview.exception.AcessoNaoAutorizadoException;
import br.edu.ifpb.cstsi.pss.scireview.exception.DadosInvalidosException;
import br.edu.ifpb.cstsi.pss.scireview.model.AreaTematica;
import br.edu.ifpb.cstsi.pss.scireview.model.Papel;
import br.edu.ifpb.cstsi.pss.scireview.model.Usuario;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class CadastroAreaTematica {

    private final Map<String, AreaTematica> areasPorNome = new LinkedHashMap<>();

    public AreaTematica cadastrar(Usuario solicitante, String nome) {
        validarCoordenador(solicitante);
        AreaTematica area = new AreaTematica(nome);
        areasPorNome.putIfAbsent(area.getNome(), area);
        return areasPorNome.get(area.getNome());
    }

    public AreaTematica associarRevisor(Usuario revisor, String nomeArea) {
        if (revisor == null || !revisor.possuiPapel(Papel.REVISOR)) {
            throw new AcessoNaoAutorizadoException(
                    "Apenas usuários com o papel de revisor podem declarar áreas de interesse.");
        }
        AreaTematica area = buscarPorNome(nomeArea)
                .orElseThrow(() -> new DadosInvalidosException(
                        "Área temática não cadastrada: " + nomeArea));
        revisor.adicionarAreaDeInteresse(area);
        return area;
    }

    public boolean existe(String nome) {
        return buscarPorNome(nome).isPresent();
    }

    public Optional<AreaTematica> buscarPorNome(String nome) {
        if (nome == null || nome.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(areasPorNome.get(nome.trim().toLowerCase()));
    }

    public Collection<AreaTematica> listar() {
        return Collections.unmodifiableCollection(areasPorNome.values());
    }

    public void limpar() {
        areasPorNome.clear();
    }

    private static void validarCoordenador(Usuario solicitante) {
        if (solicitante == null || !solicitante.possuiPapel(Papel.COORDENADOR)) {
            throw new AcessoNaoAutorizadoException(
                    "Apenas o coordenador pode cadastrar áreas temáticas.");
        }
    }
}
