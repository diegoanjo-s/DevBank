package com.devbank.dto;

import java.math.BigDecimal;
import java.util.List;

public record ExtratoResponse(
        String numeroConta,
        BigDecimal saldoAtual,
        List<TransacaoResponse> transacoes,
        int pagina,
        int totalPaginas,
        long totalTransacoes
) {
}