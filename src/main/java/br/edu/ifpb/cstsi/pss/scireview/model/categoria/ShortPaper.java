package br.edu.ifpb.cstsi.pss.scireview.model.categoria;

import br.edu.ifpb.cstsi.pss.scireview.exception.DadosInvalidosException;


public class ShortPaper implements CategoriaArtigo {

    private static final int LIMITE_PAGINAS = 6;
    private static final int TAMANHO_MINIMO_RESUMO = 50;

    @Override
    public String getNome() {
        return "Short Paper";
    }

    @Override
    public int getLimiteMaximoPaginas() {
        return LIMITE_PAGINAS;
    }

    @Override
    public void validarSubmissao(String resumo, int quantidadePaginas) {
        if (resumo == null || resumo.trim().length() < TAMANHO_MINIMO_RESUMO) {
            throw new DadosInvalidosException(
                    "Resumo de um Short Paper deve ter no mínimo " + TAMANHO_MINIMO_RESUMO + " caracteres.");
        }
        if (quantidadePaginas < 1 || quantidadePaginas > LIMITE_PAGINAS) {
            throw new DadosInvalidosException(
                    "Um Short Paper deve ter entre 1 e " + LIMITE_PAGINAS + " páginas.");
        }
    }
}
