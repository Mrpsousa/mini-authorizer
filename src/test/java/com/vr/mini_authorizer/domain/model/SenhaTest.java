package com.vr.mini_authorizer.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SenhaTest {

    @Test
    void aceitaQuatroDigitos() {
        assertThat(Senha.de("1234").valor()).isEqualTo("1234");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void rejeitaNulaVaziaOuEmBranco(String valor) {
        assertThatThrownBy(() -> Senha.de(valor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nula ou vazia");
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "123", "12345", "abcd", "s3nh@ l0nga!"})
    void aceitaSenhaDeQualquerTamanhoOuFormato(String valor) {
        assertThat(Senha.de(valor).valor()).isEqualTo(valor);
    }

    @Test
    void confereQuandoSenhasSaoIguais() {
        assertThat(Senha.de("1234").confere(Senha.de("1234"))).isTrue();
    }

    @Test
    void naoConfereQuandoSenhasSaoDiferentes() {
        assertThat(Senha.de("1234").confere(Senha.de("4321"))).isFalse();
    }

    @Test
    void naoConfereComSenhaNula() {
        assertThat(Senha.de("1234").confere(null)).isFalse();
    }

    @Test
    void toStringNaoVazaASenha() {
        assertThat(Senha.de("1234").toString()).isEqualTo("****").doesNotContain("1234");
    }
}
