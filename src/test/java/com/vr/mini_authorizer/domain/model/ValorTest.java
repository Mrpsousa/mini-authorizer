package com.vr.mini_authorizer.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValorTest {

    @Test
    void criaAPartirDeStringComDuasCasas() {
        assertThat(Valor.de("500.00").bigDecimal()).isEqualByComparingTo("500.00");
        assertThat(Valor.de("500.00").toString()).isEqualTo("500.00");
    }

    @Test
    void normalizaParaDuasCasasDecimais() {
        assertThat(Valor.de("10").toString()).isEqualTo("10.00");
        assertThat(Valor.de(new BigDecimal("10.5")).toString()).isEqualTo("10.50");
    }

    @Test
    void rejeitaMaisDeDuasCasasDecimaisSignificativas() {
        assertThatThrownBy(() -> Valor.de("10.005")).isInstanceOf(ArithmeticException.class);
        assertThatThrownBy(() -> Valor.de(new BigDecimal("10.005"))).isInstanceOf(ArithmeticException.class);
    }

    @Test
    void igualdadeIgnoraEscalaOriginal() {
        assertThat(Valor.de("10.0")).isEqualTo(Valor.de("10.00")).hasSameHashCodeAs(Valor.de("10.00"));
    }

    @Test
    void valoresDiferentesNaoSaoIguais() {
        assertThat(Valor.de("10.00")).isNotEqualTo(Valor.de("10.01"));
        assertThat(Valor.de("10.00")).isNotEqualTo("10.00").isNotEqualTo(null);
    }
}
