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

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiParaFrontIntegrationTest {

    // No data.sql: conta 1 é do Diego e conta 2 é da Maria

    @Autowired
    private MockMvc mockMvc;

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
    @DisplayName("GET /contas deve listar só as contas do cliente logado")
    void deveListarSoAsPropriasContas() throws Exception {
        mockMvc.perform(get("/contas").header(HttpHeaders.AUTHORIZATION, tokenDaMaria()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[*].numero", everyItem(is("10000002"))));
    }

    @Test
    @DisplayName("GET /contas sem token deve retornar 401")
    void naoDeveListarContasSemToken() throws Exception {
        mockMvc.perform(get("/contas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("O site permitido deve passar no CORS")
    void deveLiberarCorsParaOFront() throws Exception {
        mockMvc.perform(options("/contas")
                        .header(HttpHeaders.ORIGIN, "http://localhost:5173")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "Authorization"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5173"));
    }

    @Test
    @DisplayName("Um site desconhecido deve ser bloqueado pelo CORS")
    void deveBloquearSiteDesconhecido() throws Exception {
        mockMvc.perform(options("/contas")
                        .header(HttpHeaders.ORIGIN, "https://site-malicioso.com")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andExpect(status().isForbidden());
    }
}
