package com.devbank.controller;

import com.devbank.dto.ContaResponse;
import com.devbank.dto.ExtratoResponse;
import com.devbank.dto.OperacaoRequest;
import com.devbank.dto.TransferenciaRequest;
import com.devbank.service.TransacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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

    @GetMapping("/contas/{id}/extrato")
    public ExtratoResponse extrato(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho) {
        return transacaoService.extrato(id, inicio, fim, pagina, tamanho);
    }
}