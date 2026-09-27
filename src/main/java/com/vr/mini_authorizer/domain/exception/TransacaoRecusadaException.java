package com.vr.mini_authorizer.domain.exception;

/**
 * para quando uma regra de autorização impede a transação.
 */
public class TransacaoRecusadaException extends RuntimeException {

    private final MotivoRecusa motivo;

    public TransacaoRecusadaException(MotivoRecusa motivo) {
        super("Transação recusada: " + motivo);
        this.motivo = motivo;
    }

    // vai no corpo da resposta 422
    public MotivoRecusa motivo() {
        return motivo;
    }
}
