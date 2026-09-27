package com.vr.mini_authorizer.application.service;

import com.vr.mini_authorizer.application.port.in.ConsultarSaldoUseCase;
import com.vr.mini_authorizer.application.port.out.CartaoRepositoryPort;
import com.vr.mini_authorizer.domain.model.NumeroCartao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * implementação do caso de uso "consultar saldo".
 */
@Service
public class ConsultarSaldoService implements ConsultarSaldoUseCase {

    private final CartaoRepositoryPort repository;

    public ConsultarSaldoService(CartaoRepositoryPort repository) {
        this.repository = repository;
    }

    // readOnly: só leitura, sem lock
    @Override
    @Transactional(readOnly = true)
    public Optional<BigDecimal> consultar(String numeroCartao) {
        return repository.buscar(NumeroCartao.de(numeroCartao))
                .map(cartao -> cartao.saldo().bigDecimal());
    }
}
