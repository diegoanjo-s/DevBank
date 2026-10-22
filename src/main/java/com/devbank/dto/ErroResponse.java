package com.devbank.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ErroResponse(
        int status,
        String mensagem,
        List<String> erros,
        LocalDateTime timestamp
) {
}