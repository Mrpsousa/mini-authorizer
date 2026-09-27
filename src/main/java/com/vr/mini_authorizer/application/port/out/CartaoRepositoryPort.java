package com.vr.mini_authorizer.application.port.out;

import com.vr.mini_authorizer.domain.model.Cartao;
import com.vr.mini_authorizer.domain.model.NumeroCartao;

import java.util.Optional;

/**
 * interface para persistência
 */
public interface CartaoRepositoryPort {

    /**
     * persiste um cartão novo.
     */
    Cartao salvar(Cartao cartao);

    /**
     * busca um cartão só para leitura.
     */
    Optional<Cartao> buscar(NumeroCartao numero);

    /**
     * busca um cartão bloqueando-o até o fim da transação, para que dois
     * débitos simultâneos (mesmo em instâncias diferentes) não se sobreponham.
     */
    Optional<Cartao> buscarParaDebito(NumeroCartao numero);

    /**
     * grava o saldo atual de um cartão já existente.
     */
    void atualizarSaldo(Cartao cartao);
}
