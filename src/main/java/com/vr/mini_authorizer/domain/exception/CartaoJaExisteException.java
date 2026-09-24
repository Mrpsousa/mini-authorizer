package com.vr.mini_authorizer.domain.exception;

/**
 * para quando se tenta criar um cartão com um número que já existe.
 */
public class CartaoJaExisteException extends RuntimeException {

    private final String numeroCartao;
    private final String senha;

    public CartaoJaExisteException(String numeroCartao, String senha) {
        super("Cartão já existe: " + numeroCartao);
        this.numeroCartao = numeroCartao;
        this.senha = senha;
    }

    public String numeroCartao() {
        return numeroCartao;
    }

    public String senha() {
        return senha;
    }
}
