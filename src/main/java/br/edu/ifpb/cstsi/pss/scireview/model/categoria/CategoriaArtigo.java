package br.edu.ifpb.cstsi.pss.scireview.model.categoria;

/**
 * Estratégia (Strategy) que representa a categoria de um evento — Full Paper,
 * Short Paper ou Demo. Cada categoria encapsula as regras próprias de submissão,
 * permitindo que o restante do sistema trate os artigos de forma uniforme,
 * delegando a validação à categoria escolhida pelo coordenador (RF04).
 */
public interface CategoriaArtigo {

    String getNome();

    int getLimiteMaximoPaginas();

    void validarSubmissao(String resumo, int quantidadePaginas);
}
