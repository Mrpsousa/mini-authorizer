package com.vr.mini_authorizer.adapter.in.web;

import com.vr.mini_authorizer.application.port.in.RealizarTransacaoCommand;
import com.vr.mini_authorizer.application.port.in.RealizarTransacaoUseCase;
import com.vr.mini_authorizer.domain.exception.MotivoRecusa;
import com.vr.mini_authorizer.domain.exception.TransacaoRecusadaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * testes da camada web de transações: status e corpo de cada resposta do contrato
 */
@ExtendWith(MockitoExtension.class)
class TransacaoControllerTest {

    @Mock
    private RealizarTransacaoUseCase useCase;

    private MockMvc mockMvc;

    // MockMvc standalone: sem subir o Spring nem a segurança
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TransacaoController(useCase))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private ResultActions postar(String json) throws Exception {
        return mockMvc.perform(post("/transacoes").contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private static String corpo(String valor) {
        return "{\"numeroCartao\":\"6549873025634501\",\"senhaCartao\":\"1234\",\"valor\":" + valor + "}";
    }

    @Test
    void transacaoAutorizadaRetorna201ComOK() throws Exception {
        postar(corpo("10.00"))
                .andExpect(status().isCreated())
                .andExpect(content().string("OK"));

        verify(useCase).realizar(new RealizarTransacaoCommand("6549873025634501", "1234", new BigDecimal("10.00")));
    }

    // roda uma vez para cada motivo de recusa
    @ParameterizedTest
    @EnumSource(MotivoRecusa.class)
    void transacaoRecusadaRetorna422ComOMotivoNoCorpo(MotivoRecusa motivo) throws Exception {
        doThrow(new TransacaoRecusadaException(motivo)).when(useCase).realizar(any());

        postar(corpo("10.00"))
                .andExpect(status().isUnprocessableContent())
                .andExpect(content().string(motivo.name()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1.00", "10.001"})
    void valorZeroNegativoOuComMaisDeDuasCasasRetorna400(String valor) throws Exception {
        postar(corpo(valor))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.valor").exists());

        verifyNoInteractions(useCase);
    }

    @Test
    void camposObrigatoriosAusentesRetornam400() throws Exception {
        postar("{}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.numeroCartao").exists())
                .andExpect(jsonPath("$.senhaCartao").exists())
                .andExpect(jsonPath("$.valor").exists());

        verifyNoInteractions(useCase);
    }
}
