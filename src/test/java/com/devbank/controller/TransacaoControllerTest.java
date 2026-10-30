package com.devbank.controller;

import com.devbank.dto.ContaResponse;
import com.devbank.dto.OperacaoRequest;
import com.devbank.entity.StatusConta;
import com.devbank.exception.RecursoNaoEncontradoException;
import com.devbank.exception.RegraNegocioException;
import com.devbank.service.TransacaoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransacaoController.class)
class TransacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransacaoService transacaoService;

    @Test
    @DisplayName("POST /contas/{id}/deposito deve devolver 200 com a conta atualizada")
    void deveDepositar() throws Exception {
        ContaResponse resposta = new ContaResponse(
                1L, "10000001", new BigDecimal("1200.00"), StatusConta.ATIVA, 1L, "Diego", LocalDateTime.now());
        when(transacaoService.depositar(eq(1L), any(OperacaoRequest.class))).thenReturn(resposta);

        mockMvc.perform(post("/contas/1/deposito")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "valor": 200.00 }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numero").value("10000001"))
                .andExpect(jsonPath("$.saldo").value(1200.0));
    }

    @Test
    @DisplayName("Valor negativo deve devolver 400 e nem chamar o service")
    void deveRecusarValorNegativo() throws Exception {
        mockMvc.perform(post("/contas/1/deposito")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "valor": -50 }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensagem").value("Dados inválidos"))
                .andExpect(jsonPath("$.erros[0]").value("valor: O valor deve ser maior que zero"));

        verifyNoInteractions(transacaoService);
    }

    @Test
    @DisplayName("Saldo insuficiente no service deve virar 422")
    void deveDevolver422() throws Exception {
        when(transacaoService.sacar(eq(1L), any(OperacaoRequest.class)))
                .thenThrow(new RegraNegocioException("Saldo insuficiente"));

        mockMvc.perform(post("/contas/1/saque")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "valor": 5000.00 }
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.mensagem").value("Saldo insuficiente"));
    }

    @Test
    @DisplayName("Conta inexistente deve virar 404")
    void deveDevolver404() throws Exception {
        when(transacaoService.depositar(eq(99L), any(OperacaoRequest.class)))
                .thenThrow(new RecursoNaoEncontradoException("Conta não encontrada"));

        mockMvc.perform(post("/contas/99/deposito")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "valor": 100.00 }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Conta não encontrada"));
    }
}