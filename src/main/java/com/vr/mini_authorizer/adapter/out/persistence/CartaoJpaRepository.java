package com.vr.mini_authorizer.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * interface que já herda save(), findById() (...) do JpaRepository
 */
public interface CartaoJpaRepository extends JpaRepository<CartaoJpaEntity, String> {

    /**
     * SELECT ... FOR UPDATE: bloqueia a linha do cartão até o fim da transação.
     * outra transação que tentar o mesmo cartão espera o commit desta.
     * SQL nativo porque o @Lock do Hibernate gera "FOR UPDATE OF <alias>",
     * sintaxe que só existe a partir do MySQL 8 (o docker-compose usa o 5.7).
     */
    @Query(value = "select * from cartao where numero_cartao = :numero for update", nativeQuery = true)
    Optional<CartaoJpaEntity> buscarComLock(@Param("numero") String numero);
}
