package com.devbank.service;

import com.devbank.dto.ClienteRequest;
import com.devbank.dto.ClienteResponse;
import com.devbank.entity.Cliente;
import com.devbank.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional
    public ClienteResponse criar(ClienteRequest request) {
        String cpf = somenteDigitos(request.cpf());
        String email = request.email().trim().toLowerCase();

        if (clienteRepository.existsByCpf(cpf)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado");
        }
        if (clienteRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
        }
        Cliente cliente = new Cliente();
        cliente.setNome(request.nome().trim());
        cliente.setCpf(cpf);
        cliente.setEmail(email);

        Cliente salvo = clienteRepository.save(cliente);
        return ClienteResponse.from(salvo);
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscarPorId(Long id){
        return ClienteResponse.from(buscarClientePorId(id));
    }

    private Cliente buscarClientePorId(Long id){
        return clienteRepository.findById(id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }

    private String somenteDigitos(String valor) {
        return valor.replaceAll("\\D", "");
    }
}
