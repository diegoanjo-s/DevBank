package com.devbank.controller;

import com.devbank.dto.AbrirContaRequest;
import com.devbank.dto.ContaResponse;
import com.devbank.service.ContaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Contas", description = "Abertura, consulta e encerramento de contas")
@RestController
@RequestMapping("/contas")
@RequiredArgsConstructor
public class ContaController {

    private final ContaService contaService;

    @Operation(summary = "Abre uma conta")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContaResponse abrir(@RequestBody @Valid AbrirContaRequest request) {
        return contaService.abrir(request);
    }

    @Operation(summary = "Lista as contas do cliente logado")
    @GetMapping
    public List<ContaResponse> listarMinhas() {
        return contaService.listarDoClienteLogado();
    }

    @Operation(summary = "Consulta a conta e o saldo")
    @GetMapping("/{id}")
    public ContaResponse buscarPorId(@PathVariable Long id) {
        return contaService.buscarPorId(id);
    }

    @Operation(summary = "Encerra uma conta com saldo zero")
    @PatchMapping("/{id}/encerrar")
    public ContaResponse encerrar(@PathVariable Long id) {
        return contaService.encerrar(id);
    }
}