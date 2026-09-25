package com.vr.mini_authorizer.application.service;

import com.vr.mini_authorizer.application.port.in.CartaoCriado;
import com.vr.mini_authorizer.application.port.in.CriarCartaoCommand;
import com.vr.mini_authorizer.application.port.out.CartaoRepositoryPort;
import com.vr.mini_authorizer.domain.exception.CartaoJaExisteException;
import com.vr.mini_authorizer.domain.model.Cartao;
import com.vr.mini_authorizer.domain.model.Valor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CriarCartaoServiceTest {

    @Mock
    private CartaoRepositoryPort repository;

    @Test
    void criaCartaoComSaldoInicialEPersiste() {
        when(repository.salvar(any(Cartao.class))).thenAnswer(inv -> inv.getArgument(0));

        CartaoCriado criado = new CriarCartaoService(repository)
                .criar(new CriarCartaoCommand("6549873025634501", "1234"));

        ArgumentCaptor<Cartao> captor = ArgumentCaptor.forClass(Cartao.class);
        verify(repository).salvar(captor.capture());
        assertThat(captor.getValue().numero().valor()).isEqualTo("6549873025634501");
        assertThat(captor.getValue().senha().valor()).isEqualTo("1234");
        assertThat(captor.getValue().saldo()).isEqualTo(Valor.de("500.00"));
        assertThat(criado).isEqualTo(new CartaoCriado("6549873025634501", "1234"));
    }

    @Test
    void propagaExcecaoQuandoCartaoJaExiste() {
        when(repository.salvar(any(Cartao.class)))
                .thenThrow(new CartaoJaExisteException("6549873025634501", "1234"));

        assertThatThrownBy(() -> new CriarCartaoService(repository)
                .criar(new CriarCartaoCommand("6549873025634501", "1234")))
                .isInstanceOf(CartaoJaExisteException.class);
    }

    @Test
    void naoPersisteQuandoNumeroInvalido() {
        assertThatThrownBy(() -> new CriarCartaoService(repository)
                .criar(new CriarCartaoCommand("123", "1234")))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(repository);
    }

    @Test
    void naoPersisteQuandoSenhaVazia() {
        assertThatThrownBy(() -> new CriarCartaoService(repository)
                .criar(new CriarCartaoCommand("6549873025634501", " ")))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(repository);
    }
}
