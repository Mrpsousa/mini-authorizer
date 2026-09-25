package com.vr.mini_authorizer.adapter.in.web;

import com.vr.mini_authorizer.adapter.in.web.dto.CartaoResponse;
import com.vr.mini_authorizer.domain.exception.CartaoJaExisteException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * tratamento de erros para os Controllers
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * cartão duplicado → HTTP 422
     */
    @ExceptionHandler(CartaoJaExisteException.class)
    public ResponseEntity<CartaoResponse> tratarCartaoJaExiste(CartaoJaExisteException ex) {
        CartaoResponse response = new CartaoResponse(ex.senha(), ex.numeroCartao());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(response);
    }

    /**
     * corpo da requisição inválido (com menos de 16 dígitos) ou campo faltando → HTTP 400
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> tratarValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> erros = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> fe.getDefaultMessage() == null ? "inválido" : fe.getDefaultMessage(),
                        (mensagem1, mensagem2) -> mensagem1
                ));
        return ResponseEntity.badRequest().body(erros);
    }

    /**
     * se algum dado inválido escapar da validação do DTO
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> tratarArgumentoInvalido(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("erro", ex.getMessage()));
    }
}
