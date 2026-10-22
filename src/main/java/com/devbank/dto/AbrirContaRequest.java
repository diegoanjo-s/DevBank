package com.devbank.dto;

import jakarta.validation.constraints.NotNull;

public record AbrirContaRequest(
        @NotNull(message= "O id do cliente é obrigatório")
        Long clienteId
) {
}
