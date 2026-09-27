package com.vr.mini_authorizer.application.port.in;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * para o caso de uso de consulta de saldo; vazio se o cartão não existir.
 */
public interface ConsultarSaldoUseCase {

    Optional<BigDecimal> consultar(String numeroCartao);
}
