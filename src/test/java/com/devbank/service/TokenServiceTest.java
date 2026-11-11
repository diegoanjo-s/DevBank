package com.devbank.service;

import com.devbank.dto.LoginResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TokenServiceTest {

    private static final SecretKey CHAVE = chave("segredo-de-teste-com-mais-de-32-caracteres");
    private static final SecretKey OUTRA_CHAVE = chave("outro-segredo-de-teste-com-mais-de-32-caracteres");

    private final JwtEncoder encoder = NimbusJwtEncoder.withSecretKey(CHAVE).build();

    private static SecretKey chave(String segredo) {
        return new SecretKeySpec(segredo.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    private JwtDecoder decoderCom(SecretKey chave) {
        return NimbusJwtDecoder.withSecretKey(chave).macAlgorithm(MacAlgorithm.HS256).build();
    }

    @Test
    @DisplayName("Token deve carregar o id do cliente e a validade")
    void deveGerarTokenValido() {
        TokenService tokenService = new TokenService(encoder, 60);

        LoginResponse resposta = tokenService.gerar(1L);
        Jwt jwt = decoderCom(CHAVE).decode(resposta.token());

        assertThat(jwt.getSubject()).isEqualTo("1");
        assertThat(resposta.tipo()).isEqualTo("Bearer");
        assertThat(resposta.expiraEmSegundos()).isEqualTo(3600);
    }

    @Test
    @DisplayName("Token assinado com outra chave deve ser recusado")
    void deveRecusarTokenDeOutraChave() {
        TokenService tokenService = new TokenService(encoder, 60);

        String token = tokenService.gerar(1L).token();

        assertThatThrownBy(() -> decoderCom(OUTRA_CHAVE).decode(token))
                .isInstanceOf(JwtException.class);
    }

    @Test
    @DisplayName("Token vencido deve ser recusado")
    void deveRecusarTokenVencido() {
        Instant agora = Instant.now();
        JwtClaimsSet dados = JwtClaimsSet.builder()
                .subject("1")
                .issuedAt(agora.minus(Duration.ofHours(2)))
                .expiresAt(agora.minus(Duration.ofHours(1)))
                .build();
        JwsHeader cabecalho = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = encoder.encode(JwtEncoderParameters.from(cabecalho, dados)).getTokenValue();

        assertThatThrownBy(() -> decoderCom(CHAVE).decode(token))
                .isInstanceOf(JwtException.class);
    }
}