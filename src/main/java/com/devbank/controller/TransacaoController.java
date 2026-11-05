package com.devbank.controller;

import com.devbank.dto.ContaResponse;
import com.devbank.dto.ExtratoResponse;
import com.devbank.dto.OperacaoRequest;
import com.devbank.dto.TransferenciaRequest;
import com.devbank.service.TransacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "Transações", description = "Depósito, saque, transferência e extrato")
@RestController
@RequiredArgsConstructor
public class TransacaoController {

    private final TransacaoService transacaoService;

    @Operation(summary = "Deposita um valor na conta")
    @PostMapping("/contas/{id}/deposito")
    public ContaResponse depositar(@PathVariable Long id, @RequestBody @Valid OperacaoRequest request) {
        return transacaoService.depositar(id, request);
    }

    @Operation(summary = "Saca um valor da conta")
    @PostMapping("/contas/{id}/saque")
    public ContaResponse sacar(@PathVariable Long id, @RequestBody @Valid OperacaoRequest request) {
        return transacaoService.sacar(id, request);
    }

    @Operation(summary = "Transfere um valor entre duas contas", description = "Operação tudo ou nada: se falhar, nada muda.")
    @PostMapping("/transferencias")
    public ContaResponse transferir(@RequestBody @Valid TransferenciaRequest request) {
        return transacaoService.transferir(request);
    }

    @Operation(summary = "Consulta o extrato da conta", description = "Sem período informado, mostra os últimos 30 dias.")
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