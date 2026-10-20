package com.devbank.dto;

import com.devbank.entity.Cliente;

import java.time.LocalDateTime;

public record ClienteResponse(
        Long id,
        String nome,
        String cpf,
        String email,
        LocalDateTime dataCadastro
) {

    public static ClienteResponse from(Cliente cliente){
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNome(),
                mascararCpf(cliente.getCpf()),
                cliente.getEmail(),
                cliente.getDataCadastro()
        );
    }

    private static String mascararCpf(String cpf){
        return "***." + cpf.substring(3,6) + "." + cpf.substring(6,9) + "-**";
    }
}
