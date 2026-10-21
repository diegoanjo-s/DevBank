package com.devbank.repository;

import com.devbank.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByCpf(String cpf);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);
}
