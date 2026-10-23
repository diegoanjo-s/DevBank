package com.devbank.repository;

import com.devbank.entity.Transacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    Page<Transacao> findByContaIdAndDataHoraBetweenOrderByDataHoraDesc(
            Long contaId, LocalDateTime inicio, LocalDateTime fim, Pageable pageable);
}