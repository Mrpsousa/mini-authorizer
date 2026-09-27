package com.vr.mini_authorizer.adapter.out.persistence;

import com.vr.mini_authorizer.domain.model.Cartao;
import com.vr.mini_authorizer.domain.model.NumeroCartao;
import com.vr.mini_authorizer.domain.model.Senha;
import com.vr.mini_authorizer.domain.model.Valor;

/**
 * converte domínio <-> persistência 
 */
public final class CartaoMapper {

    private CartaoMapper() {
    }

    // só para cartão novo: a entidade sai marcada como nova (INSERT)
    public static CartaoJpaEntity toEntity(Cartao cartao) {
        return new CartaoJpaEntity(
                cartao.numero().valor(),
                cartao.senha().valor(),
                cartao.saldo().bigDecimal()
        );
    }

    // usa reconstituir, para manter o saldo que veio do banco
    public static Cartao toDomain(CartaoJpaEntity entity) {
        return Cartao.reconstituir(
                NumeroCartao.de(entity.getNumeroCartao()),
                Senha.de(entity.getSenha()),
                Valor.de(entity.getSaldo())
        );
    }
}
