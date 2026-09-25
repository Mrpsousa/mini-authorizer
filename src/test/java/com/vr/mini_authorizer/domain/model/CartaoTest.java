package com.vr.mini_authorizer.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CartaoTest {

    private final NumeroCartao numero = NumeroCartao.de("6549873025634501");
    private final Senha senha = Senha.de("1234");

    @Test
    void cartaoNovoNasceComSaldoDeQuinhentosReais() {
        Cartao cartao = Cartao.novo(numero, senha);

        assertThat(cartao.numero()).isEqualTo(numero);
        assertThat(cartao.senha()).isEqualTo(senha);
        assertThat(cartao.saldo()).isEqualTo(Valor.de("500.00"));
    }

    @Test
    void reconstituirPreservaOSaldoInformado() {
        Cartao cartao = Cartao.reconstituir(numero, senha, Valor.de("495.15"));

        assertThat(cartao.saldo()).isEqualTo(Valor.de("495.15"));
    }
}
