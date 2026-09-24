package com.vr.mini_authorizer.domain.model;

import java.util.regex.Pattern;

/**
 * representa o número de um cartão VR.
 */
public record NumeroCartao(String valor) {

    // regex para validação (16 dígitos numéricos do cartão)
    private static final Pattern DEZESSEIS_DIGITOS = Pattern.compile("\\d{16}");

    // construtor e validações
    public NumeroCartao {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Número do cartão não pode ser nulo ou vazio");
        }
        if (!DEZESSEIS_DIGITOS.matcher(valor).matches()) {
            throw new IllegalArgumentException("Número do cartão deve conter exatamente 16 dígitos numéricos");
        }
    }

    // factory
    public static NumeroCartao de(String valor) {
        return new NumeroCartao(valor);
    }
}
