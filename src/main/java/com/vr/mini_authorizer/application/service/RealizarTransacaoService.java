package com.vr.mini_authorizer.application.service;

import com.vr.mini_authorizer.application.port.in.RealizarTransacaoCommand;
import com.vr.mini_authorizer.application.port.in.RealizarTransacaoUseCase;
import com.vr.mini_authorizer.application.port.out.CartaoRepositoryPort;
import com.vr.mini_authorizer.domain.exception.MotivoRecusa;
import com.vr.mini_authorizer.domain.exception.TransacaoRecusadaException;
import com.vr.mini_authorizer.domain.model.Cartao;
import com.vr.mini_authorizer.domain.model.NumeroCartao;
import com.vr.mini_authorizer.domain.model.Senha;
import com.vr.mini_authorizer.domain.model.Valor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * implementação do caso de uso "realizar transação".
 * regras, na ordem do README: cartão existe -> senha confere -> saldo suficiente.
 */
@Service
public class RealizarTransacaoService implements RealizarTransacaoUseCase {

    private final CartaoRepositoryPort repository;

    public RealizarTransacaoService(CartaoRepositoryPort repository) {
        this.repository = repository;
    }

    /**
     * a leitura com lock, o débito e a gravação acontecem na mesma transação
     * de banco: o lock só é liberado no commit (ou rollback).
     */
    @Override
    @Transactional
    public void realizar(RealizarTransacaoCommand comando) {
        Cartao cartao = repository.buscarParaDebito(NumeroCartao.de(comando.numeroCartao()))
                .orElseThrow(() -> new TransacaoRecusadaException(MotivoRecusa.CARTAO_INEXISTENTE));

        cartao.debitar(Senha.de(comando.senhaCartao()), Valor.de(comando.valor()));

        repository.atualizarSaldo(cartao);
    }
}
