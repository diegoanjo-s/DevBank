package com.devbank.dto;

import com.devbank.entity.Conta;
import com.devbank.entity.StatusConta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ContaResponse(
        Long id,
        String numero,
        BigDecimal saldo,
        StatusConta status,
        Long clienteId,
        String clienteNome,
        LocalDateTime dataAbertura
) {

    public static ContaResponse from(Conta conta){
        return new ContaResponse(
                conta.getId(),
                conta.getNumero(),
                conta.getSaldo(),
                conta.getStatus(),
                conta.getCliente().getId(),
                conta.getCliente().getNome(),
                conta.getDataAbertura()
        );
    }
}
