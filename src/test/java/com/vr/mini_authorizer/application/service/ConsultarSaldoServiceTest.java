package com.vr.mini_authorizer.application.service;

import com.vr.mini_authorizer.application.port.out.CartaoRepositoryPort;
import com.vr.mini_authorizer.domain.model.Cartao;
import com.vr.mini_authorizer.domain.model.NumeroCartao;
import com.vr.mini_authorizer.domain.model.Senha;
import com.vr.mini_authorizer.domain.model.Valor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * testes do caso de uso "consultar saldo", com o repositório mockado
 */
@ExtendWith(MockitoExtension.class)
class ConsultarSaldoServiceTest {

    @Mock
    private CartaoRepositoryPort repository;

    private final NumeroCartao numero = NumeroCartao.de("6549873025634501");

    @Test
    void retornaOSaldoDoCartao() {
        when(repository.buscar(numero))
                .thenReturn(Optional.of(Cartao.reconstituir(numero, Senha.de("1234"), Valor.de("495.15"))));

        assertThat(new ConsultarSaldoService(repository).consultar("6549873025634501"))
                .hasValueSatisfying(saldo -> assertThat(saldo).isEqualByComparingTo("495.15"));
    }

    @Test
    void retornaVazioQuandoCartaoNaoExiste() {
        when(repository.buscar(numero)).thenReturn(Optional.empty());

        assertThat(new ConsultarSaldoService(repository).consultar("6549873025634501")).isEmpty();
    }

    @Test
    void numeroMalformadoNemConsultaORepositorio() {
        assertThatThrownBy(() -> new ConsultarSaldoService(repository).consultar("123"))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(repository);
    }
}
