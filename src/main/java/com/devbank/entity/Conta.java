package com.devbank.entity;

import com.devbank.exception.RegraNegocioException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "contas")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 8)
    private String numero;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusConta status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataAbertura;

    public Conta(Cliente cliente, String numero) {
        this.cliente = cliente;
        this.numero = numero;
        this.saldo = BigDecimal.ZERO;
        this.status = StatusConta.ATIVA;
    }

    public void encerrar() {
        if (status == StatusConta.ENCERRADA) {
            throw new RegraNegocioException("Conta já encerrada");
        }
        if (saldo.compareTo(BigDecimal.ZERO) != 0) {
            throw new RegraNegocioException("Só é possível encerrar uma conta com saldo zero");
        }
        status = StatusConta.ENCERRADA;
    }
}

