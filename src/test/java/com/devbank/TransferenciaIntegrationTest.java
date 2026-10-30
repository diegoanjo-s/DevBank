package com.devbank;

import com.devbank.dto.TransferenciaRequest;
import com.devbank.exception.RegraNegocioException;
import com.devbank.repository.ContaRepository;
import com.devbank.repository.TransacaoRepository;
import com.devbank.service.TransacaoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class TransferenciaIntegrationTest {

    @Autowired
    private TransacaoService transacaoService;

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private TransacaoRepository transacaoRepository;

    private BigDecimal saldoNoBanco(Long contaId) {
        return contaRepository.findById(contaId).orElseThrow().getSaldo();
    }

    @Test
    @DisplayName("Transferência deve ser gravada no banco: saldos e 2 transações")
    void deveGravarTransferenciaNoBanco() {
        BigDecimal origemAntes = saldoNoBanco(1L);
        BigDecimal destinoAntes = saldoNoBanco(2L);
        long transacoesAntes = transacaoRepository.count();

        transacaoService.transferir(new TransferenciaRequest(1L, 2L, new BigDecimal("100.00")));

        assertThat(saldoNoBanco(1L)).isEqualByComparingTo(origemAntes.subtract(new BigDecimal("100.00")));
        assertThat(saldoNoBanco(2L)).isEqualByComparingTo(destinoAntes.add(new BigDecimal("100.00")));
        assertThat(transacaoRepository.count()).isEqualTo(transacoesAntes + 2);
    }

    @Test
    @DisplayName("Se o destino falhar, o saque da origem é desfeito (rollback)")
    void deveDesfazerTudoSeODestinoFalhar() {
        BigDecimal origemAntes = saldoNoBanco(1L);
        long transacoesAntes = transacaoRepository.count();

        assertThatThrownBy(() -> transacaoService.transferir(
                new TransferenciaRequest(1L, 3L, new BigDecimal("100.00"))))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Conta encerrada não pode movimentar valores");

        assertThat(saldoNoBanco(1L)).isEqualByComparingTo(origemAntes);
        assertThat(transacaoRepository.count()).isEqualTo(transacoesAntes);
    }
}
