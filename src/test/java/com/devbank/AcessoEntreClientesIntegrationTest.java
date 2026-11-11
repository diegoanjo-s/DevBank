package com.devbank;

import com.devbank.repository.ContaRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AcessoEntreClientesIntegrationTest {

    // No data.sql: conta 1 é do Diego e conta 2 é da Maria

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContaRepository contaRepository;

    private String tokenDaMaria() throws Exception {
        String resposta = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "email": "maria@email.com", "senha": "senha123" }
                                """))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return "Bearer " + JsonPath.read(resposta, "$.token");
    }

    @Test
    @DisplayName("Maria deve conseguir ver a própria conta")
    void deveVerAPropriaConta() throws Exception {
        mockMvc.perform(get("/contas/2").header(HttpHeaders.AUTHORIZATION, tokenDaMaria()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numero").value("10000002"));
    }

    @Test
    @DisplayName("Maria não pode ver a conta do Diego")
    void naoDeveVerContaDeOutroCliente() throws Exception {
        mockMvc.perform(get("/contas/1").header(HttpHeaders.AUTHORIZATION, tokenDaMaria()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.mensagem").value("Você não tem permissão para acessar este recurso"));
    }

    @Test
    @DisplayName("Maria não pode ver o extrato do Diego")
    void naoDeveVerExtratoDeOutroCliente() throws Exception {
        mockMvc.perform(get("/contas/1/extrato").header(HttpHeaders.AUTHORIZATION, tokenDaMaria()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Maria não pode ver os dados cadastrais do Diego")
    void naoDeveVerDadosDeOutroCliente() throws Exception {
        mockMvc.perform(get("/clientes/1").header(HttpHeaders.AUTHORIZATION, tokenDaMaria()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Maria não pode sacar da conta do Diego, e o saldo não muda")
    void naoDeveSacarDeOutroCliente() throws Exception {
        BigDecimal saldoAntes = contaRepository.findById(1L).orElseThrow().getSaldo();

        mockMvc.perform(post("/contas/1/saque")
                        .header(HttpHeaders.AUTHORIZATION, tokenDaMaria())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "valor": 10.00 }
                                """))
                .andExpect(status().isForbidden());

        assertThat(contaRepository.findById(1L).orElseThrow().getSaldo()).isEqualByComparingTo(saldoAntes);
    }

    @Test
    @DisplayName("Maria não pode transferir usando a conta do Diego como origem")
    void naoDeveTransferirDeOutroCliente() throws Exception {
        BigDecimal saldoAntes = contaRepository.findById(1L).orElseThrow().getSaldo();

        mockMvc.perform(post("/transferencias")
                        .header(HttpHeaders.AUTHORIZATION, tokenDaMaria())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "contaOrigemId": 1, "contaDestinoId": 2, "valor": 10.00 }
                                """))
                .andExpect(status().isForbidden());

        assertThat(contaRepository.findById(1L).orElseThrow().getSaldo()).isEqualByComparingTo(saldoAntes);
    }

    @Test
    @DisplayName("Maria pode transferir da própria conta para a conta do Diego")
    void deveTransferirDaPropriaConta() throws Exception {
        mockMvc.perform(post("/transferencias")
                        .header(HttpHeaders.AUTHORIZATION, tokenDaMaria())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "contaOrigemId": 2, "contaDestinoId": 1, "valor": 1.00 }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Maria não pode abrir conta em nome do Diego")
    void naoDeveAbrirContaParaOutroCliente() throws Exception {
        mockMvc.perform(post("/contas")
                        .header(HttpHeaders.AUTHORIZATION, tokenDaMaria())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "clienteId": 1 }
                                """))
                .andExpect(status().isForbidden());
    }
}