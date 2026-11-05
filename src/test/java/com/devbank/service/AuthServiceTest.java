package com.devbank.service;

import com.devbank.dto.ClienteResponse;
import com.devbank.dto.LoginRequest;
import com.devbank.entity.Cliente;
import com.devbank.exception.CredenciaisInvalidasException;
import com.devbank.repository.ClienteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private Cliente clienteDeTeste() {
        Cliente cliente = new Cliente();
        cliente.setNome("Diego");
        cliente.setCpf("52998224725");
        cliente.setEmail("diego@email.com");
        cliente.setSenha("hash");
        return cliente;
    }

    @Test
    @DisplayName("Login com senha correta deve devolver o cliente")
    void deveFazerLogin() {
        when(clienteRepository.findByEmail("diego@email.com")).thenReturn(Optional.of(clienteDeTeste()));
        when(passwordEncoder.matches("senha123", "hash")).thenReturn(true);

        ClienteResponse resposta = authService.login(new LoginRequest(" Diego@Email.com ", "senha123"));

        assertThat(resposta.email()).isEqualTo("diego@email.com");
    }

    @Test
    @DisplayName("Senha errada deve ser recusada")
    void deveRecusarSenhaErrada() {
        when(clienteRepository.findByEmail("diego@email.com")).thenReturn(Optional.of(clienteDeTeste()));
        when(passwordEncoder.matches("errada", "hash")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequest("diego@email.com", "errada")))
                .isInstanceOf(CredenciaisInvalidasException.class)
                .hasMessage("E-mail ou senha inválidos");
    }

    @Test
    @DisplayName("E-mail inexistente deve dar a mesma mensagem da senha errada")
    void deveRecusarEmailInexistente() {
        when(clienteRepository.findByEmail("ninguem@email.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginRequest("ninguem@email.com", "senha123")))
                .isInstanceOf(CredenciaisInvalidasException.class)
                .hasMessage("E-mail ou senha inválidos");
    }
}