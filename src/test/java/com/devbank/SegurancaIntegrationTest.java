package com.devbank;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SegurancaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private String fazerLogin() throws Exception {
        String resposta = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "email": "diego@email.com", "senha": "senha123" }
                                """))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return JsonPath.read(resposta, "$.token");
    }

    @Test
    @DisplayName("Sem token deve devolver 401 no formato de erro da API")
    void deveRecusarSemToken() throws Exception {
        mockMvc.perform(get("/contas/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.mensagem").value("Token ausente, inválido ou vencido"));
    }

    @Test
    @DisplayName("Token falso deve devolver 401")
    void deveRecusarTokenFalso() throws Exception {
        mockMvc.perform(get("/contas/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token-falso"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensagem").value("Token ausente, inválido ou vencido"));
    }

    @Test
    @DisplayName("Token do login deve liberar o acesso")
    void deveAceitarTokenDoLogin() throws Exception {
        mockMvc.perform(get("/contas/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + fazerLogin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numero").value("10000001"));
    }

    @Test
    @DisplayName("Cadastro deve continuar público")
    void deveDeixarCadastroPublico() throws Exception {
        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Documentação da API deve continuar pública")
    void deveDeixarDocumentacaoPublica() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }
}