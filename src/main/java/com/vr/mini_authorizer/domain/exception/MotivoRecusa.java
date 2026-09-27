package com.vr.mini_authorizer.domain.exception;

/**
 * qual regra de autorização barrou a transação; o nome é o que vai no corpo da resposta 422
 */
public enum MotivoRecusa {
    CARTAO_INEXISTENTE,
    SENHA_INVALIDA,
    SALDO_INSUFICIENTE
}
