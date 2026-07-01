package br.edu.ifpb.cstsi.pss.scireview.model.estado;

import br.edu.ifpb.cstsi.pss.scireview.exception.DadosInvalidosException;
import br.edu.ifpb.cstsi.pss.scireview.model.Artigo;

abstract class EstadoArtigoBase implements EstadoArtigo {

    private final StatusArtigo status;

    protected EstadoArtigoBase(StatusArtigo status) {
        this.status = status;
    }

    @Override
    public StatusArtigo getStatus() {
        return status;
    }

    @Override
    public void enviarParaRevisao(Artigo artigo) {
        throw transicaoInvalida("enviado para revisão");
    }

    @Override
    public void aceitar(Artigo artigo) {
        throw transicaoInvalida("aceito");
    }

    @Override
    public void rejeitar(Artigo artigo) {
        throw transicaoInvalida("rejeitado");
    }

    private DadosInvalidosException transicaoInvalida(String destino) {
        return new DadosInvalidosException(
                "Artigo não pode ser " + destino + " no estado " + status + ".");
    }
}
