package com.vr.mini_authorizer.adapter.out.persistence;

import com.vr.mini_authorizer.application.port.out.CartaoRepositoryPort;
import com.vr.mini_authorizer.domain.exception.CartaoJaExisteException;
import com.vr.mini_authorizer.domain.model.Cartao;
import com.vr.mini_authorizer.domain.model.NumeroCartao;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.Optional;

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

    @Override
    public Optional<Cartao> buscar(NumeroCartao numero) {
        return jpaRepository.findById(numero.valor()).map(CartaoMapper::toDomain);
    }

    @Override
    public Optional<Cartao> buscarParaDebito(NumeroCartao numero) {
        return jpaRepository.buscarComLock(numero.valor()).map(CartaoMapper::toDomain);
    }

    /**
     * não usa save(): o mapper sempre cria uma entidade "nova", o que viraria
     * INSERT. aqui a entidade vem do contexto de persistência (já carregada e
     * bloqueada em buscarParaDebito, sem nova consulta) e só o saldo muda.
     */
    @Override
    public void atualizarSaldo(Cartao cartao) {
        CartaoJpaEntity entity = jpaRepository.findById(cartao.numero().valor())
                .orElseThrow(() -> new IllegalStateException("Cartão não encontrado para atualizar: " + cartao.numero().valor()));
        entity.atualizarSaldo(cartao.saldo().bigDecimal());
    }
}
