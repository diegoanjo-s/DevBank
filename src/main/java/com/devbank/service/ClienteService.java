package com.devbank.service;

import com.devbank.dto.ClienteRequest;
import com.devbank.dto.ClienteResponse;
import com.devbank.dto.ClienteUpdateRequest;
import com.devbank.entity.Cliente;
import com.devbank.exception.ConflitoException;
import com.devbank.exception.RecursoNaoEncontradoException;
import com.devbank.repository.ClienteRepository;
import com.devbank.repository.ContaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ContaRepository contaRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClienteAutenticado clienteAutenticado;

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
        cliente.setSenha(passwordEncoder.encode(request.senha()));

        Cliente salvo = clienteRepository.save(cliente);
        return ClienteResponse.from(salvo);
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscarPorId(Long id) {
        clienteAutenticado.validarDono(id);
        return ClienteResponse.from(buscarClientePorId(id));
    }

    private Cliente buscarClientePorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado"));
    }

    @Transactional
    public ClienteResponse atualizar(Long id, ClienteUpdateRequest upRequest) {
        clienteAutenticado.validarDono(id);
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
        clienteAutenticado.validarDono(id);
        Cliente cliente = buscarClientePorId(id);

        if (contaRepository.existsByClienteId(id)) {
            throw new ConflitoException("Cliente possui contas e não pode ser removido");
        }
        clienteRepository.delete(cliente);
    }

    private String somenteDigitos(String valor) {
        return valor.replaceAll("\\D", "");
    }
}