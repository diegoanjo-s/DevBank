package com.devbank.service;

import com.devbank.dto.*;
import com.devbank.entity.Conta;
import com.devbank.entity.TipoTransacao;
import com.devbank.entity.Transacao;
import com.devbank.exception.RecursoNaoEncontradoException;
import com.devbank.exception.RegraNegocioException;
import com.devbank.repository.ContaRepository;
import com.devbank.repository.TransacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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

    @Transactional
    public ContaResponse transferir(TransferenciaRequest request) {
        if (request.contaOrigemId().equals(request.contaDestinoId())) {
            throw new RegraNegocioException("Não é possível transferir para a mesma conta");
        }

        Conta origem = buscarConta(request.contaOrigemId());
        Conta destino = buscarConta(request.contaDestinoId());

        origem.sacar(request.valor());
        destino.depositar(request.valor());

        transacaoRepository.save(new Transacao(origem, TipoTransacao.TRANSFERENCIA_ENVIADA, request.valor()));
        transacaoRepository.save(new Transacao(destino, TipoTransacao.TRANSFERENCIA_RECEBIDA, request.valor()));

        return ContaResponse.from(origem);
    }

    @Transactional(readOnly = true)
    public ExtratoResponse extrato(Long contaId, LocalDate inicio, LocalDate fim, int pagina, int tamanho) {
        Conta conta = buscarConta(contaId);

        LocalDate dataFim = (fim != null) ? fim : LocalDate.now();
        LocalDate dataInicio = (inicio != null) ? inicio : dataFim.minusDays(30);

        if (dataInicio.isAfter(dataFim)) {
            throw new RegraNegocioException("A data inicial não pode ser depois da data final");
        }
        if (pagina < 0 || tamanho < 1 || tamanho > 50) {
            throw new RegraNegocioException("A página deve ser 0 ou mais e o tamanho entre 1 e 50");
        }

        Page<Transacao> resultado = transacaoRepository.findByContaIdAndDataHoraBetweenOrderByDataHoraDesc(
                contaId,
                dataInicio.atStartOfDay(),
                dataFim.atTime(LocalTime.MAX),
                PageRequest.of(pagina, tamanho)
        );

        List<TransacaoResponse> transacoes = resultado.getContent()
                .stream()
                .map(TransacaoResponse::from)
                .toList();

        return new ExtratoResponse(
                conta.getNumero(),
                conta.getSaldo(),
                transacoes,
                resultado.getNumber(),
                resultado.getTotalPages(),
                resultado.getTotalElements()
        );
    }

    private Conta buscarConta(Long id) {
        return contaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada"));
    }
}