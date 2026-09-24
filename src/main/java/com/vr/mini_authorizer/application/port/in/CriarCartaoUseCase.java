package com.vr.mini_authorizer.application.port.in;

/**
 * para o caso de uso de criação de cartão.
 */
public interface CriarCartaoUseCase {

    CartaoCriado criar(CriarCartaoCommand comando);
}
