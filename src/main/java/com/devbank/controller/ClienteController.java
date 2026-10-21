package com.devbank.controller;

import com.devbank.dto.ClienteRequest;
import com.devbank.dto.ClienteResponse;
import com.devbank.dto.ClienteUpdateRequest;
import com.devbank.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteResponse criar(@RequestBody @Valid ClienteRequest request) {
        return clienteService.criar(request);
    }

    @GetMapping("/{id}")
    public ClienteResponse buscarPorId(@PathVariable Long id) {
        return clienteService.buscarPorId(id);
    }

    @GetMapping
    public List<ClienteResponse> listar() {
        return clienteService.listar();
    }

    @PutMapping("/{id}")
    public ClienteResponse atualizar(@PathVariable Long id, @RequestBody @Valid ClienteUpdateRequest request) {
        return clienteService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        clienteService.deletar(id);
    }
}
