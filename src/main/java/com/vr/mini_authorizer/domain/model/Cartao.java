package com.vr.mini_authorizer.domain.model;

/**
 * entidade de domínio "Cartão", classe não tem nenhuma annotations,
 * isolando o domínio, e pode ser testado sem subir nada além do
 * próprio Java.
 */

public class Cartao {

    private static final Valor SALDO_INICIAL = Valor.de("500.00");

    private final NumeroCartao numero;
    private final Senha senha;
    private Valor saldo;

    /**
     * private, obriga quem quiser criar um Cartao a usar uma das
     * "factories" abaixo, criação ou reconstrução
     */
    private Cartao(NumeroCartao numero, Senha senha, Valor saldo) {
        this.numero = numero;
        this.senha = senha;
        this.saldo = saldo;
    }

    /**
     * factory para criação  pela primeira vez (fluxo do do post),
     * ninguém de fora consegue criar um Cartao com outro saldo inicial.
     */
    public static Cartao novo(NumeroCartao numero, Senha senha) {
        return new Cartao(numero, senha, SALDO_INICIAL);
    }

    /**
     * factory para um cartão já existente para quando RECARREGADO (a partir do
     * DB, por exemplo)
     */
    public static Cartao reconstituir(NumeroCartao numero, Senha senha, Valor saldo) {
        return new Cartao(numero, senha, saldo);
    }

    public NumeroCartao numero() {
        return numero;
    }

    public Senha senha() {
        return senha;
    }

    public Valor saldo() {
        return saldo;
    }
}
