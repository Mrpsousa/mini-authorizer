package com.vr.mini_authorizer.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * interface que já herda save(), findById() (...) do JpaRepository
 */
public interface CartaoJpaRepository extends JpaRepository<CartaoJpaEntity, String> {
}
