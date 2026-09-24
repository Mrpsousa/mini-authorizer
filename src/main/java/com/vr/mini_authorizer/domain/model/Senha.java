package com.vr.mini_authorizer.domain.model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * representa a senha de um cartão.
 */
public record Senha(String valor) {

    public Senha {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Senha não pode ser nula ou vazia");
        }
    }

    public static Senha de(String valor) {
        return new Senha(valor);
    }

    /**
     * compara senha do cartão com a digitada em "transações"
     */
    public boolean confere(Senha outra) {
        if (outra == null) {
            return false;
        }
        return MessageDigest.isEqual(
                valor.getBytes(StandardCharsets.UTF_8),
                outra.valor().getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * evitar vazar a senha em log
     * por exemplo, se alguém escrever {log.info("cartao={}", cartao)}.
     * segurança, LGPD.
     */
    @Override
    public String toString() {
        return "****";
    }
}
