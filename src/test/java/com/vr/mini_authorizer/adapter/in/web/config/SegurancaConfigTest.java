package com.vr.mini_authorizer.adapter.in.web.config;

import com.vr.mini_authorizer.adapter.in.web.CartaoController;
import com.vr.mini_authorizer.adapter.in.web.TransacaoController;
import com.vr.mini_authorizer.application.port.in.CartaoCriado;
import com.vr.mini_authorizer.application.port.in.ConsultarSaldoUseCase;
import com.vr.mini_authorizer.application.port.in.CriarCartaoUseCase;
import com.vr.mini_authorizer.application.port.in.RealizarTransacaoUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * sobe só a camada web com a SegurancaConfig real, para testar o BASIC de verdade.
 */
@WebMvcTest(controllers = {CartaoController.class, TransacaoController.class})
@Import(SegurancaConfig.class)
class SegurancaConfigTest {

    private static final String CARTAO = "{\"numeroCartao\":\"6549873025634501\",\"senha\":\"1234\"}";
    private static final String TRANSACAO = "{\"numeroCartao\":\"6549873025634501\",\"senhaCartao\":\"1234\",\"valor\":10.00}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CriarCartaoUseCase criarCartao;

    @MockitoBean
    private ConsultarSaldoUseCase consultarSaldo;

    @MockitoBean
    private RealizarTransacaoUseCase realizarTransacao;

    @Test
    void semCredencialTodosOsEndpointsRetornam401() throws Exception {
        mockMvc.perform(post("/cartoes").contentType(MediaType.APPLICATION_JSON).content(CARTAO))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/cartoes/6549873025634501"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(post("/transacoes").contentType(MediaType.APPLICATION_JSON).content(TRANSACAO))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(criarCartao, consultarSaldo, realizarTransacao);
    }

    @Test
    void credencialErradaRetorna401() throws Exception {
        mockMvc.perform(get("/cartoes/6549873025634501").with(httpBasic("username", "errada")))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(consultarSaldo);
    }

    @Test
    void credencialCorretaLiberaOsEndpointsSemCsrf() throws Exception {
        when(criarCartao.criar(any())).thenReturn(new CartaoCriado("6549873025634501", "1234"));
        when(consultarSaldo.consultar("6549873025634501")).thenReturn(Optional.of(new BigDecimal("500.00")));

        mockMvc.perform(post("/cartoes").with(httpBasic("username", "password"))
                        .contentType(MediaType.APPLICATION_JSON).content(CARTAO))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/cartoes/6549873025634501").with(httpBasic("username", "password")))
                .andExpect(status().isOk());
        mockMvc.perform(post("/transacoes").with(httpBasic("username", "password"))
                        .contentType(MediaType.APPLICATION_JSON).content(TRANSACAO))
                .andExpect(status().isCreated());
    }
}
