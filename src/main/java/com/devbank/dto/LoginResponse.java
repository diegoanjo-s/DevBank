package com.devbank.dto;

public record LoginResponse(
        String token,
        String tipo,
        long expiraEmSegundos
) {
}