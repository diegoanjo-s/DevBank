package com.devbank.service;

import com.devbank.dto.LoginRequest;
import com.devbank.dto.LoginResponse;
import com.devbank.entity.Cliente;
import com.devbank.exception.CredenciaisInvalidasException;
import com.devbank.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();

        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(CredenciaisInvalidasException::new);

        if (!passwordEncoder.matches(request.senha(), cliente.getSenha())) {
            throw new CredenciaisInvalidasException();
        }

        return tokenService.gerar(cliente.getId());
    }
}