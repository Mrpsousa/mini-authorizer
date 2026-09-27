package com.vr.mini_authorizer.application.service;

import com.vr.mini_authorizer.application.port.in.RealizarTransacaoCommand;
import com.vr.mini_authorizer.application.port.out.CartaoRepositoryPort;
import com.vr.mini_authorizer.domain.exception.MotivoRecusa;
import com.vr.mini_authorizer.domain.exception.TransacaoRecusadaException;
import com.vr.mini_authorizer.domain.model.Cartao;
import com.vr.mini_authorizer.domain.model.NumeroCartao;
import com.vr.mini_authorizer.domain.model.Senha;
import com.vr.mini_authorizer.domain.model.Valor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * testes do caso de uso "realizar transação": uma aprovação e uma recusa
 * para cada regra de autorização, com o repositório mockado
 */
@ExtendWith(MockitoExtension.class)
class RealizarTransacaoServiceTest {

    @Mock
    private CartaoRepositoryPort repository;

    private final NumeroCartao numero = NumeroCartao.de("6549873025634501");

    private RealizarTransacaoCommand comando(String senha, String valor) {
        return new RealizarTransacaoCommand("6549873025634501", senha, new BigDecimal(valor));
    }

    private void cartaoComSaldo(String saldo) {
        when(repository.buscarParaDebito(numero))
                .thenReturn(Optional.of(Cartao.reconstituir(numero, Senha.de("1234"), Valor.de(saldo))));
    }

    @Test
    void transacaoAutorizadaDebitaEGravaONovoSaldo() {
        cartaoComSaldo("500.00");

        new RealizarTransacaoService(repository).realizar(comando("1234", "10.00"));

        ArgumentCaptor<Cartao> captor = ArgumentCaptor.forClass(Cartao.class);
        verify(repository).atualizarSaldo(captor.capture());
        assertThat(captor.getValue().saldo()).isEqualTo(Valor.de("490.00"));
    }

    @Test
    void cartaoInexistenteRecusaComCartaoInexistente() {
        when(repository.buscarParaDebito(numero)).thenReturn(Optional.empty());

        assertRecusa(comando("1234", "10.00"), MotivoRecusa.CARTAO_INEXISTENTE);
    }

    @Test
    void senhaErradaRecusaComSenhaInvalida() {
        cartaoComSaldo("500.00");

        assertRecusa(comando("9999", "10.00"), MotivoRecusa.SENHA_INVALIDA);
    }

    @Test
    void saldoMenorQueOValorRecusaComSaldoInsuficiente() {
        cartaoComSaldo("5.00");

        assertRecusa(comando("1234", "10.00"), MotivoRecusa.SALDO_INSUFICIENTE);
    }

    // toda recusa: exceção com o motivo certo e saldo não gravado
    private void assertRecusa(RealizarTransacaoCommand comando, MotivoRecusa motivo) {
        assertThatThrownBy(() -> new RealizarTransacaoService(repository).realizar(comando))
                .isInstanceOfSatisfying(TransacaoRecusadaException.class,
                        e -> assertThat(e.motivo()).isEqualTo(motivo));
        verify(repository, never()).atualizarSaldo(any());
    }
}
