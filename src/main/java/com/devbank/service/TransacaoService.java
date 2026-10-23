package com.devbank.service;

import com.devbank.dto.ContaResponse;
import com.devbank.dto.OperacaoRequest;
import com.devbank.entity.Conta;
import com.devbank.entity.TipoTransacao;
import com.devbank.entity.Transacao;
import com.devbank.exception.RecursoNaoEncontradoException;
import com.devbank.repository.ContaRepository;
import com.devbank.repository.TransacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransacaoService {

    private final ContaRepository contaRepository;
    private final TransacaoRepository transacaoRepository;

    @Transactional
    public ContaResponse depositar(Long contaId, OperacaoRequest request) {
        Conta conta = buscarConta(contaId);
        conta.depositar(request.valor());
        transacaoRepository.save(new Transacao(conta, TipoTransacao.DEPOSITO, request.valor()));
        return ContaResponse.from(conta);
    }

    @Transactional
    public ContaResponse sacar(Long contaId, OperacaoRequest request) {
        Conta conta = buscarConta(contaId);
        conta.sacar(request.valor());
        transacaoRepository.save(new Transacao(conta, TipoTransacao.SAQUE, request.valor()));
        return ContaResponse.from(conta);
    }

    private Conta buscarConta(Long id) {
        return contaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada"));
    }
}