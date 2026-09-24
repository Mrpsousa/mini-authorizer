package com.vr.mini_authorizer.application.port.in;

/**
 * objeto de entrada do caso de uso, em formato simples (Strings
 * cruas), separado do DTO HTTP (CartaoRequest).
 * existe para desacoplar o Controller (que fala JSON) do caso de uso 
 */
public record CriarCartaoCommand(String numeroCartao, String senha) {
}
