package com.vr.mini_authorizer.application.service;

import com.vr.mini_authorizer.application.port.in.CartaoCriado;
import com.vr.mini_authorizer.application.port.in.CriarCartaoCommand;
import com.vr.mini_authorizer.application.port.in.CriarCartaoUseCase;
import com.vr.mini_authorizer.application.port.out.CartaoRepositoryPort;
import com.vr.mini_authorizer.domain.model.Cartao;
import com.vr.mini_authorizer.domain.model.NumeroCartao;
import com.vr.mini_authorizer.domain.model.Senha;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * implementação do orquestrador" caso de uso "criar cartão".
 */
@Service
public class CriarCartaoService implements CriarCartaoUseCase {

    private final CartaoRepositoryPort repository;

    // injeção do repository
    public CriarCartaoService(CartaoRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public CartaoCriado criar(CriarCartaoCommand comando) {
        NumeroCartao numero = NumeroCartao.de(comando.numeroCartao());
        Senha senha = Senha.de(comando.senha());

        Cartao cartao = Cartao.novo(numero, senha);

        Cartao salvo = repository.salvar(cartao);

        return new CartaoCriado(salvo.numero().valor(), salvo.senha().valor());
    }
}
