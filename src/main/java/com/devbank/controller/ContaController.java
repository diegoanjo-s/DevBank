package com.devbank.controller;

import com.devbank.dto.AbrirContaRequest;
import com.devbank.dto.ContaResponse;
import com.devbank.service.ContaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contas")
@RequiredArgsConstructor
public class ContaController {

    private final ContaService contaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContaResponse abrir(@RequestBody @Valid AbrirContaRequest request) {
        return contaService.abrir(request);
    }

    @GetMapping("/{id}")
    public ContaResponse buscarPorId(@PathVariable Long id) {
        return contaService.buscarPorId(id);
    }

    @PatchMapping("/{id}/encerrar")
    public ContaResponse encerrar(@PathVariable Long id) {
        return contaService.encerrar(id);
    }
}