package com.devbank.service;

import com.devbank.exception.AcessoNegadoException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class ClienteAutenticado {

    public void validarDono(Long donoId) {
        if (!id().equals(donoId)) {
            throw new AcessoNegadoException();
        }
    }

    private Long id() {
        Jwt token = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Long.valueOf(token.getSubject());
    }
}