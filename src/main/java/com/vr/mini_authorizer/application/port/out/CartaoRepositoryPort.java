package com.vr.mini_authorizer.application.port.out;

import com.vr.mini_authorizer.domain.model.Cartao;

/**
 * interface para persistência
 */
public interface CartaoRepositoryPort {

    /**
     * persiste um cartão novo.
     */
    Cartao salvar(Cartao cartao);
}
