package com.vr.mini_authorizer.adapter.out.persistence;

import com.vr.mini_authorizer.domain.exception.CartaoJaExisteException;
import com.vr.mini_authorizer.domain.model.Cartao;
import com.vr.mini_authorizer.domain.model.NumeroCartao;
import com.vr.mini_authorizer.domain.model.Senha;
import com.vr.mini_authorizer.domain.model.Valor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * testes do adapter de persistência, com o CartaoJpaRepository mockado (sem banco)
 */
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

    @Test
    void buscarConverteEntidadeEncontrada() {
        when(jpaRepository.findById("6549873025634501"))
                .thenReturn(Optional.of(new CartaoJpaEntity("6549873025634501", "1234", new BigDecimal("495.15"))));

        Optional<Cartao> encontrado = new CartaoPersistenceAdapter(jpaRepository).buscar(cartao.numero());

        assertThat(encontrado).hasValueSatisfying(c -> assertThat(c.saldo()).isEqualTo(Valor.de("495.15")));
    }

    @Test
    void buscarRetornaVazioQuandoNaoExiste() {
        when(jpaRepository.findById("6549873025634501")).thenReturn(Optional.empty());

        assertThat(new CartaoPersistenceAdapter(jpaRepository).buscar(cartao.numero())).isEmpty();
    }

    @Test
    void buscarParaDebitoUsaAConsultaComLock() {
        when(jpaRepository.buscarComLock("6549873025634501"))
                .thenReturn(Optional.of(new CartaoJpaEntity("6549873025634501", "1234", new BigDecimal("500.00"))));

        Optional<Cartao> encontrado = new CartaoPersistenceAdapter(jpaRepository).buscarParaDebito(cartao.numero());

        assertThat(encontrado).isPresent();
        verify(jpaRepository, never()).findById(any());
    }

    // save() viraria INSERT; o UPDATE vem do Hibernate no commit
    @Test
    void atualizarSaldoAlteraAEntidadeGerenciadaSemChamarSave() {
        CartaoJpaEntity entity = new CartaoJpaEntity("6549873025634501", "1234", new BigDecimal("500.00"));
        when(jpaRepository.findById("6549873025634501")).thenReturn(Optional.of(entity));
        Cartao debitado = Cartao.reconstituir(cartao.numero(), cartao.senha(), Valor.de("490.00"));

        new CartaoPersistenceAdapter(jpaRepository).atualizarSaldo(debitado);

        assertThat(entity.getSaldo()).isEqualByComparingTo("490.00");
        verify(jpaRepository, never()).save(any());
        verify(jpaRepository, never()).saveAndFlush(any());
    }

    @Test
    void atualizarSaldoDeCartaoInexistenteFalha() {
        when(jpaRepository.findById("6549873025634501")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> new CartaoPersistenceAdapter(jpaRepository).atualizarSaldo(cartao))
                .isInstanceOf(IllegalStateException.class);
    }
}
