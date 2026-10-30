package com.devbank.entity;

import com.devbank.exception.RegraNegocioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ContaTest {

    private Conta criarConta(String saldo) {
        return new Conta(new Cliente(), "12345678", new BigDecimal(saldo));
    }

    // ---------- depositar ----------

    @Test
    @DisplayName("Deve somar o valor depositado ao saldo")
    void deveDepositar() {
        // Dado
        Conta conta = criarConta("100.00");

        // Quando
        conta.depositar(new BigDecimal("50.00"));

        // Então
        assertThat(conta.getSaldo()).isEqualByComparingTo("150.00");
    }

    @Test
    @DisplayName("Não deve aceitar depósito de valor zero")
    void naoDeveDepositarValorZero() {
        Conta conta = criarConta("100.00");

        assertThatThrownBy(() -> conta.depositar(BigDecimal.ZERO))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("O valor deve ser maior que zero");
    }

    // ---------- sacar ----------

    @Test
    @DisplayName("Deve subtrair o valor sacado do saldo")
    void deveSacar() {
        Conta conta = criarConta("100.00");

        conta.sacar(new BigDecimal("30.00"));

        assertThat(conta.getSaldo()).isEqualByComparingTo("70.00");
    }

    @Test
    @DisplayName("Deve permitir sacar exatamente o saldo todo")
    void deveSacarSaldoTodo() {
        Conta conta = criarConta("100.00");

        conta.sacar(new BigDecimal("100.00"));

        assertThat(conta.getSaldo()).isEqualByComparingTo("0");
    }

    @Test
    @DisplayName("Não deve sacar mais que o saldo, e o saldo não muda")
    void naoDeveSacarMaisQueOSaldo() {
        Conta conta = criarConta("100.00");

        assertThatThrownBy(() -> conta.sacar(new BigDecimal("100.01")))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Saldo insuficiente");

        assertThat(conta.getSaldo()).isEqualByComparingTo("100.00");
    }

    // ---------- encerrar ----------

    @Test
    @DisplayName("Deve encerrar conta com saldo zero")
    void deveEncerrarContaZerada() {
        Conta conta = criarConta("0");

        conta.encerrar();

        assertThat(conta.getStatus()).isEqualTo(StatusConta.ENCERRADA);
    }

    @Test
    @DisplayName("Não deve encerrar conta com saldo")
    void naoDeveEncerrarContaComSaldo() {
        Conta conta = criarConta("10.00");

        assertThatThrownBy(conta::encerrar)
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Só é possível encerrar uma conta com saldo zero");

        assertThat(conta.getStatus()).isEqualTo(StatusConta.ATIVA);
    }

    @Test
    @DisplayName("Não deve encerrar conta já encerrada")
    void naoDeveEncerrarDuasVezes() {
        Conta conta = criarConta("0");
        conta.encerrar();

        assertThatThrownBy(conta::encerrar)
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Conta já encerrada");
    }

    @Test
    @DisplayName("Conta encerrada não pode receber depósito")
    void contaEncerradaNaoMovimenta() {
        Conta conta = criarConta("0");
        conta.encerrar();

        assertThatThrownBy(() -> conta.depositar(new BigDecimal("10.00")))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Conta encerrada não pode movimentar valores");
    }
}