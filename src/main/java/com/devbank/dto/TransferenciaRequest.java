package com.devbank.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferenciaRequest(
        @NotNull(message = "A conta de origem é obrigatória")
        Long contaOrigemId,

        @NotNull(message = "A conta de destino é obrigatória")
        Long contaDestinoId,

        @NotNull(message = "O valor é obrigatório")
        @Positive(message = "O valor deve ser maior que zero")
        BigDecimal valor
) {
}