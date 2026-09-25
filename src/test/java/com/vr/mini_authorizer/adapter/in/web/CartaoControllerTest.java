package com.vr.mini_authorizer.adapter.in.web;

import com.vr.mini_authorizer.application.port.in.CartaoCriado;
import com.vr.mini_authorizer.application.port.in.CriarCartaoCommand;
import com.vr.mini_authorizer.application.port.in.CriarCartaoUseCase;
import com.vr.mini_authorizer.domain.exception.CartaoJaExisteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CartaoControllerTest {

    @Mock
    private CriarCartaoUseCase useCase;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new CartaoController(useCase))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private org.springframework.test.web.servlet.ResultActions postar(String json) throws Exception {
        return mockMvc.perform(post("/cartoes").contentType(MediaType.APPLICATION_JSON).content(json));
    }

    @Test
    void criaCartaoRetorna201ComCorpoDoContrato() throws Exception {
        when(useCase.criar(any())).thenReturn(new CartaoCriado("6549873025634501", "1234"));

        postar("{\"numeroCartao\":\"6549873025634501\",\"senha\":\"1234\"}")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroCartao").value("6549873025634501"))
                .andExpect(jsonPath("$.senha").value("1234"));

        verify(useCase).criar(new CriarCartaoCommand("6549873025634501", "1234"));
    }

    @Test
    void cartaoDuplicadoRetorna422ComMesmoCorpo() throws Exception {
        when(useCase.criar(any())).thenThrow(new CartaoJaExisteException("6549873025634501", "1234"));

        postar("{\"numeroCartao\":\"6549873025634501\",\"senha\":\"1234\"}")
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.numeroCartao").value("6549873025634501"))
                .andExpect(jsonPath("$.senha").value("1234"));
    }

    @Test
    void numeroComMenosDeDezesseisDigitosRetorna400() throws Exception {
        postar("{\"numeroCartao\":\"123\",\"senha\":\"1234\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.numeroCartao").value("numeroCartao deve conter exatamente 16 dígitos"));

        verifyNoInteractions(useCase);
    }

    @Test
    void senhaAusenteRetorna400() throws Exception {
        postar("{\"numeroCartao\":\"6549873025634501\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.senha").value("senha é obrigatória"));

        verifyNoInteractions(useCase);
    }

    @Test
    void senhaDeQualquerTamanhoEhAceita() throws Exception {
        when(useCase.criar(any())).thenReturn(new CartaoCriado("6549873025634501", "12345"));

        postar("{\"numeroCartao\":\"6549873025634501\",\"senha\":\"12345\"}")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.senha").value("12345"));

        verify(useCase).criar(new CriarCartaoCommand("6549873025634501", "12345"));
    }

    @Test
    void argumentoInvalidoQueEscapaDoDtoRetorna400() throws Exception {
        when(useCase.criar(any())).thenThrow(new IllegalArgumentException("dado inválido"));

        postar("{\"numeroCartao\":\"6549873025634501\",\"senha\":\"1234\"}")
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"erro\":\"dado inválido\"}"));
    }
}
