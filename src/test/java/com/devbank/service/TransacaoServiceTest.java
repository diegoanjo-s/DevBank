package com.devbank.service;

import com.devbank.dto.ContaResponse;
import com.devbank.dto.TransferenciaRequest;
import com.devbank.entity.Cliente;
import com.devbank.entity.Conta;
import com.devbank.entity.Transacao;
import com.devbank.exception.RecursoNaoEncontradoException;
import com.devbank.exception.RegraNegocioException;
import com.devbank.repository.ContaRepository;
import com.devbank.repository.TransacaoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransacaoServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private TransacaoRepository transacaoRepository;

    @InjectMocks
    private TransacaoService transacaoService;

    private Conta criarConta(String saldo) {
        return new Conta(new Cliente(), "10000001", new BigDecimal(saldo));
    }

    @Test
    @DisplayName("Deve transferir: tira da origem, coloca no destino e registra 2 transações")
    void deveTransferir() {
        // Dado
        Conta origem = criarConta("1000.00");
        Conta destino = criarConta("500.00");
        when(contaRepository.findById(1L)).thenReturn(Optional.of(origem));
        when(contaRepository.findById(2L)).thenReturn(Optional.of(destino));

        // Quando
        ContaResponse resposta = transacaoService.transferir(
                new TransferenciaRequest(1L, 2L, new BigDecimal("200.00")));

        // Então
        assertThat(origem.getSaldo()).isEqualByComparingTo("800.00");
        assertThat(destino.getSaldo()).isEqualByComparingTo("700.00");
        assertThat(resposta.saldo()).isEqualByComparingTo("800.00");
        verify(transacaoRepository, times(2)).save(any(Transacao.class));
    }

    @Test
    @DisplayName("Não deve transferir para a mesma conta, e nem chega a ir ao banco")
    void naoDeveTransferirParaMesmaConta() {
        TransferenciaRequest request = new TransferenciaRequest(1L, 1L, new BigDecimal("50.00"));

        assertThatThrownBy(() -> transacaoService.transferir(request))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Não é possível transferir para a mesma conta");

        verifyNoInteractions(contaRepository, transacaoRepository);
    }

    @Test
    @DisplayName("Não deve transferir para conta inexistente, e não registra nada")
    void naoDeveTransferirParaContaInexistente() {
        when(contaRepository.findById(1L)).thenReturn(Optional.of(criarConta("1000.00")));
        when(contaRepository.findById(99L)).thenReturn(Optional.empty());
        TransferenciaRequest request = new TransferenciaRequest(1L, 99L, new BigDecimal("50.00"));

        assertThatThrownBy(() -> transacaoService.transferir(request))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Conta não encontrada");

        verify(transacaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Não deve transferir sem saldo, e não registra nada")
    void naoDeveTransferirSemSaldo() {
        Conta origem = criarConta("100.00");
        Conta destino = criarConta("0");
        when(contaRepository.findById(1L)).thenReturn(Optional.of(origem));
        when(contaRepository.findById(2L)).thenReturn(Optional.of(destino));
        TransferenciaRequest request = new TransferenciaRequest(1L, 2L, new BigDecimal("500.00"));

        assertThatThrownBy(() -> transacaoService.transferir(request))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Saldo insuficiente");

        assertThat(origem.getSaldo()).isEqualByComparingTo("100.00");
        assertThat(destino.getSaldo()).isEqualByComparingTo("0");
        verify(transacaoRepository, never()).save(any());
    }
}