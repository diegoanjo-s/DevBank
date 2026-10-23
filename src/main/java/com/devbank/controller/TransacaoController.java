package com.devbank.controller;

import com.devbank.dto.ContaResponse;
import com.devbank.dto.OperacaoRequest;
import com.devbank.dto.TransferenciaRequest;
import com.devbank.service.TransacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TransacaoController {

    private final TransacaoService transacaoService;

    @PostMapping("/contas/{id}/deposito")
    public ContaResponse depositar(@PathVariable Long id, @RequestBody @Valid OperacaoRequest request) {
        return transacaoService.depositar(id, request);
    }

    @PostMapping("/contas/{id}/saque")
    public ContaResponse sacar(@PathVariable Long id, @RequestBody @Valid OperacaoRequest request) {
        return transacaoService.sacar(id, request);
    }

    @PostMapping("/transferencias")
    public ContaResponse transferir(@RequestBody @Valid TransferenciaRequest request) {
        return transacaoService.transferir(request);
    }
}