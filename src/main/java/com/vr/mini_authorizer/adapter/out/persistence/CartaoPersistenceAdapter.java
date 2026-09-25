package com.vr.mini_authorizer.adapter.out.persistence;

import com.vr.mini_authorizer.application.port.out.CartaoRepositoryPort;
import com.vr.mini_authorizer.domain.exception.CartaoJaExisteException;
import com.vr.mini_authorizer.domain.model.Cartao;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

/**
 * implementação do CartaoRepositoryPort
 */
@Component
public class CartaoPersistenceAdapter implements CartaoRepositoryPort {

    private final CartaoJpaRepository jpaRepository;

    public CartaoPersistenceAdapter(CartaoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Cartao salvar(Cartao cartao) {
        try {
            // flush para força o INSERT a rodar ainda no bloco try
            CartaoJpaEntity salvo = jpaRepository.saveAndFlush(CartaoMapper.toEntity(cartao));
            return CartaoMapper.toDomain(salvo);
        } catch (DataIntegrityViolationException e) {
            // se violação de chave primária 
            throw new CartaoJaExisteException(cartao.numero().valor(), cartao.senha().valor());
        }
    }
}
