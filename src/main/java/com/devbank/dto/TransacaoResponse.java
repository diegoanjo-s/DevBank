package com.devbank.dto;

import com.devbank.entity.TipoTransacao;
import com.devbank.entity.Transacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransacaoResponse(
        Long id,
        TipoTransacao tipo,
        BigDecimal valor,
        LocalDateTime dataHora
) {

    public static TransacaoResponse from(Transacao transacao) {
        return new TransacaoResponse(
                transacao.getId(),
                transacao.getTipo(),
                transacao.getValor(),
                transacao.getDataHora()
        );
    }
}