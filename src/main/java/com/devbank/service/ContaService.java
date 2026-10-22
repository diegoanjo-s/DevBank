package com.devbank.service;

import com.devbank.dto.AbrirContaRequest;
import com.devbank.dto.ContaResponse;
import com.devbank.entity.Cliente;
import com.devbank.entity.Conta;
import com.devbank.entity.StatusConta;
import com.devbank.exception.ConflitoException;
import com.devbank.exception.RecursoNaoEncontradoException;
import com.devbank.repository.ClienteRepository;
import com.devbank.repository.ContaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class ContaService {

    private final ContaRepository contaRepository;
    private final ClienteRepository clienteRepository;

    @Value("${devbank.saldo-inicial}")
    private BigDecimal saldoInicial;

    @Transactional
    public ContaResponse abrir(AbrirContaRequest request) {
        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado"));

        if (contaRepository.existsByClienteIdAndStatus(cliente.getId(), StatusConta.ATIVA)) {
            throw new ConflitoException("Cliente já possui uma conta ativa");
        }

        Conta conta = new Conta(cliente, gerarNumeroConta(), saldoInicial);
        Conta salva = contaRepository.save(conta);
        return ContaResponse.from(salva);
    }

    @Transactional(readOnly = true)
    public ContaResponse buscarPorId(Long id) {
        return ContaResponse.from(buscarContaPorId(id));
    }

    @Transactional
    public ContaResponse encerrar(Long id) {
        Conta conta = buscarContaPorId(id);
        conta.encerrar();
        return ContaResponse.from(conta);
    }

    private Conta buscarContaPorId(Long id) {
        return contaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada"));
    }

    private String gerarNumeroConta() {
        String numero;
        do {
            numero = String.valueOf(ThreadLocalRandom.current().nextInt(10_000_000, 100_000_000));
        } while (contaRepository.existsByNumero(numero));
        return numero;
    }
}