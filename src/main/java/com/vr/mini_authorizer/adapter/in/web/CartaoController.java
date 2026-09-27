package com.vr.mini_authorizer.adapter.in.web;

import com.vr.mini_authorizer.adapter.in.web.dto.CartaoRequest;
import com.vr.mini_authorizer.adapter.in.web.dto.CartaoResponse;
import com.vr.mini_authorizer.application.port.in.CartaoCriado;
import com.vr.mini_authorizer.application.port.in.ConsultarSaldoUseCase;
import com.vr.mini_authorizer.application.port.in.CriarCartaoCommand;
import com.vr.mini_authorizer.application.port.in.CriarCartaoUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * "interface" web para cartões
 */

@RestController
@RequestMapping("/cartoes")
public class CartaoController {

    private final CriarCartaoUseCase criarCartaoUseCase;
    private final ConsultarSaldoUseCase consultarSaldoUseCase;

    // injeção via construtor
    public CartaoController(CriarCartaoUseCase criarCartaoUseCase, ConsultarSaldoUseCase consultarSaldoUseCase) {
        this.criarCartaoUseCase = criarCartaoUseCase;
        this.consultarSaldoUseCase = consultarSaldoUseCase;
    }

    /**
     * {@code POST /cartoes} — (tenta) cria um novo cartão com saldo inicial de R$500,00.
     */
    @PostMapping
    public ResponseEntity<CartaoResponse> criar(@Valid @RequestBody CartaoRequest request) {
        CriarCartaoCommand command = new CriarCartaoCommand(request.numeroCartao(), request.senha());

        CartaoCriado criado = criarCartaoUseCase.criar(command);

        CartaoResponse response = new CartaoResponse(criado.senha(), criado.numeroCartao());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * {@code GET /cartoes/{numeroCartao}} — saldo do cartão (ex.: 495.15), ou 404 sem corpo.
     */
    @GetMapping("/{numeroCartao}")
    public ResponseEntity<BigDecimal> consultarSaldo(@PathVariable String numeroCartao) {
        return consultarSaldoUseCase.consultar(numeroCartao)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
