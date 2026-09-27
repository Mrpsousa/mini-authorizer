package com.vr.mini_authorizer.adapter.in.web;

import com.vr.mini_authorizer.adapter.in.web.dto.TransacaoRequest;
import com.vr.mini_authorizer.application.port.in.RealizarTransacaoCommand;
import com.vr.mini_authorizer.application.port.in.RealizarTransacaoUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * "interface" web para transações
 */
@RestController
@RequestMapping("/transacoes")
public class TransacaoController {

    private final RealizarTransacaoUseCase realizarTransacaoUseCase;

    // injeção via construtor
    public TransacaoController(RealizarTransacaoUseCase realizarTransacaoUseCase) {
        this.realizarTransacaoUseCase = realizarTransacaoUseCase;
    }

    /**
     * {@code POST /transacoes} — 201 "OK" se autorizada; recusas viram 422 no GlobalExceptionHandler.
     */
    @PostMapping
    public ResponseEntity<String> realizar(@Valid @RequestBody TransacaoRequest request) {
        realizarTransacaoUseCase.realizar(
                new RealizarTransacaoCommand(request.numeroCartao(), request.senhaCartao(), request.valor()));

        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.TEXT_PLAIN)
                .body("OK");
    }
}
