package com.vr.mini_authorizer.adapter.out.persistence;

import com.vr.mini_authorizer.domain.exception.CartaoJaExisteException;
import com.vr.mini_authorizer.domain.model.Cartao;
import com.vr.mini_authorizer.domain.model.NumeroCartao;
import com.vr.mini_authorizer.domain.model.Senha;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartaoPersistenceAdapterTest {

    @Mock
    private CartaoJpaRepository jpaRepository;

    private final Cartao cartao = Cartao.novo(NumeroCartao.de("6549873025634501"), Senha.de("1234"));

    @Test
    void salvaERetornaCartaoDeDominio() {
        when(jpaRepository.saveAndFlush(any(CartaoJpaEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        Cartao salvo = new CartaoPersistenceAdapter(jpaRepository).salvar(cartao);

        assertThat(salvo.numero()).isEqualTo(cartao.numero());
        assertThat(salvo.saldo()).isEqualTo(cartao.saldo());
    }

    @Test
    void violacaoDeIntegridadeViraCartaoJaExiste() {
        when(jpaRepository.saveAndFlush(any(CartaoJpaEntity.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate key"));

        assertThatThrownBy(() -> new CartaoPersistenceAdapter(jpaRepository).salvar(cartao))
                .isInstanceOfSatisfying(CartaoJaExisteException.class, e -> {
                    assertThat(e.numeroCartao()).isEqualTo("6549873025634501");
                    assertThat(e.senha()).isEqualTo("1234");
                });
    }
}
