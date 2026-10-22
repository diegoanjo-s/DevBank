package com.devbank.service;

import com.devbank.dto.ClienteRequest;
import com.devbank.dto.ClienteResponse;
import com.devbank.dto.ClienteUpdateRequest;
import com.devbank.entity.Cliente;
import com.devbank.exception.ConflitoException;
import com.devbank.exception.RecursoNaoEncontradoException;
import com.devbank.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional
    public ClienteResponse criar(ClienteRequest request) {
        String cpf = somenteDigitos(request.cpf());
        String email = request.email().trim().toLowerCase();

        if (clienteRepository.existsByCpf(cpf)) {
            throw new ConflitoException("CPF já cadastrado");
        }
        if (clienteRepository.existsByEmail(email)) {
            throw new ConflitoException("E-mail já cadastrado");
        }
        Cliente cliente = new Cliente();
        cliente.setNome(request.nome().trim());
        cliente.setCpf(cpf);
        cliente.setEmail(email);

        Cliente salvo = clienteRepository.save(cliente);
        return ClienteResponse.from(salvo);
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscarPorId(Long id) {
        return ClienteResponse.from(buscarClientePorId(id));
    }

    private Cliente buscarClientePorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado"));
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        return clienteRepository.findAll()
                .stream()
                .map(ClienteResponse::from)
                .toList();
    }

    @Transactional
    public ClienteResponse atualizar(Long id, ClienteUpdateRequest upRequest) {
        Cliente cliente = buscarClientePorId(id);
        String email = upRequest.email().trim().toLowerCase();

        if (clienteRepository.existsByEmailAndIdNot(email, id)) {
            throw new ConflitoException("E-mail já cadastrado");
        }
        cliente.setNome(upRequest.nome().trim());
        cliente.setEmail(email);

        return ClienteResponse.from(cliente);
    }

    @Transactional
    public void deletar(Long id) {
        Cliente cliente = buscarClientePorId(id);
        clienteRepository.delete(cliente);
    }

    private String somenteDigitos(String valor) {
        return valor.replaceAll("\\D", "");
    }
}
