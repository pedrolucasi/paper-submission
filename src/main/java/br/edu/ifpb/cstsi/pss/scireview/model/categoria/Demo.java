package br.edu.ifpb.cstsi.pss.scireview.model.categoria;

import br.edu.ifpb.cstsi.pss.scireview.exception.DadosInvalidosException;


public class Demo implements CategoriaArtigo {

    private static final int LIMITE_PAGINAS = 4;
    private static final int TAMANHO_MINIMO_RESUMO = 30;

    @Override
    public String getNome() {
        return "Demo";
    }

    @Override
    public int getLimiteMaximoPaginas() {
        return LIMITE_PAGINAS;
    }

    @Override
    public void validarSubmissao(String resumo, int quantidadePaginas) {
        if (resumo == null || resumo.trim().length() < TAMANHO_MINIMO_RESUMO) {
            throw new DadosInvalidosException(
                    "Resumo de uma Demo deve ter no mínimo " + TAMANHO_MINIMO_RESUMO + " caracteres.");
        }
        if (quantidadePaginas < 1 || quantidadePaginas > LIMITE_PAGINAS) {
            throw new DadosInvalidosException(
                    "Uma Demo deve ter entre 1 e " + LIMITE_PAGINAS + " páginas.");
        }
    }
}
