package com.devbank.controller;

import com.devbank.dto.ClienteResponse;
import com.devbank.dto.LoginRequest;
import com.devbank.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Autenticação", description = "Login de clientes")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Confere o e-mail e a senha do cliente")
    @PostMapping("/login")
    public ClienteResponse login(@RequestBody @Valid LoginRequest request) {
        return authService.login(request);
    }
}