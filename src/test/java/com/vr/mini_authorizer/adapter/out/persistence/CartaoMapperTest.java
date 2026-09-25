package com.vr.mini_authorizer.adapter.out.persistence;

import com.vr.mini_authorizer.domain.model.Cartao;
import com.vr.mini_authorizer.domain.model.NumeroCartao;
import com.vr.mini_authorizer.domain.model.Senha;
import com.vr.mini_authorizer.domain.model.Valor;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CartaoMapperTest {

    @Test
    void converteDominioParaEntidadeMarcandoComoNova() {
        Cartao cartao = Cartao.novo(NumeroCartao.de("6549873025634501"), Senha.de("1234"));

        CartaoJpaEntity entity = CartaoMapper.toEntity(cartao);

        assertThat(entity.getNumeroCartao()).isEqualTo("6549873025634501");
        assertThat(entity.getSenha()).isEqualTo("1234");
        assertThat(entity.getSaldo()).isEqualByComparingTo("500.00");
        assertThat(entity.getId()).isEqualTo("6549873025634501");
        assertThat(entity.isNew()).isTrue();
    }

    @Test
    void converteEntidadeParaDominio() {
        CartaoJpaEntity entity = new CartaoJpaEntity("6549873025634501", "1234", new BigDecimal("495.15"));

        Cartao cartao = CartaoMapper.toDomain(entity);

        assertThat(cartao.numero()).isEqualTo(NumeroCartao.de("6549873025634501"));
        assertThat(cartao.senha()).isEqualTo(Senha.de("1234"));
        assertThat(cartao.saldo()).isEqualTo(Valor.de("495.15"));
    }
}
