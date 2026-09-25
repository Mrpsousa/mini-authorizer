package com.vr.mini_authorizer.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NumeroCartaoTest {

    @Test
    void aceitaDezesseisDigitos() {
        assertThat(NumeroCartao.de("6549873025634501").valor()).isEqualTo("6549873025634501");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void rejeitaNuloVazioOuEmBranco(String valor) {
        assertThatThrownBy(() -> NumeroCartao.de(valor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nulo ou vazio");
    }

    @ParameterizedTest
    @ValueSource(strings = {"123", "65498730256345012", "654987302563450a", "6549 8730 2563 450"})
    void rejeitaQuantidadeOuFormatoInvalido(String valor) {
        assertThatThrownBy(() -> NumeroCartao.de(valor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("16 dígitos");
    }

    @Test
    void numerosIguaisSaoEquivalentes() {
        assertThat(NumeroCartao.de("6549873025634501")).isEqualTo(NumeroCartao.de("6549873025634501"));
    }
}
