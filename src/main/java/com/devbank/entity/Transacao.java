package com.devbank.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transacoes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conta_id", nullable = false)
    private Conta conta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoTransacao tipo;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataHora;

    public Transacao(Conta conta, TipoTransacao tipo, BigDecimal valor) {
        this.conta = conta;
        this.tipo = tipo;
        this.valor = valor;
    }
}