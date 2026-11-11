package com.devbank.repository;

import com.devbank.entity.Conta;
import com.devbank.entity.StatusConta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContaRepository extends JpaRepository<Conta, Long> {
    boolean existsByNumero(String numero);

    boolean existsByClienteIdAndStatus(Long clienteId, StatusConta status);

    boolean existsByClienteId(Long clienteId);

    List<Conta> findByClienteIdOrderByDataAberturaDesc(Long clienteId);

}
