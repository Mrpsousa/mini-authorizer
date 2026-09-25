package com.vr.mini_authorizer.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO de entrada
 */
public record CartaoRequest(

        @NotBlank(message = "numeroCartao é obrigatório")
        @Pattern(regexp = "\\d{16}", message = "numeroCartao deve conter exatamente 16 dígitos")
        String numeroCartao,

        @NotBlank(message = "senha é obrigatória")
        String senha
) {
}
