package com.vr.mini_authorizer.application.port.in;

/**
 * Response criação do cartão
 */

// "record" foi escolhido pq essa classe será imutável e só carregará dados 
public record CartaoCriado(String numeroCartao, String senha) {
}
