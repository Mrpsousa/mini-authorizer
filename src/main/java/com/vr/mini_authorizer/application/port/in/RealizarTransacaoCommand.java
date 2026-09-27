package com.vr.mini_authorizer.application.port.in;

import java.math.BigDecimal;

/**
 * objeto de entrada do caso de uso de transação, separado do DTO HTTP (TransacaoRequest).
 */
public record RealizarTransacaoCommand(String numeroCartao, String senhaCartao, BigDecimal valor) {
}
