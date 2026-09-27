package com.vr.mini_authorizer.adapter.in.web.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * DTO de entrada da transação
 */
public record TransacaoRequest(

        @NotBlank(message = "numeroCartao é obrigatório")
        @Pattern(regexp = "\\d{16}", message = "numeroCartao deve conter exatamente 16 dígitos")
        String numeroCartao,

        @NotBlank(message = "senhaCartao é obrigatória")
        String senhaCartao,

        @NotNull(message = "valor é obrigatório")
        @Positive(message = "valor deve ser maior que zero")
        @Digits(integer = 17, fraction = 2, message = "valor deve ter no máximo 2 casas decimais")
        BigDecimal valor
) {
}
