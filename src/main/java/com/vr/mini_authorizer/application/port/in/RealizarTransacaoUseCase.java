package com.vr.mini_authorizer.application.port.in;

/**
 * para o caso de uso de autorização de transação; lança
 * TransacaoRecusadaException se alguma regra de autorização falhar.
 */
public interface RealizarTransacaoUseCase {

    void realizar(RealizarTransacaoCommand comando);
}
