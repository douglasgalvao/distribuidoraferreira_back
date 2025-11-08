package com.distribuidoraferreira.backend.services.implementations;

import com.distribuidoraferreira.backend.dto.request.ClienteRequest;
import com.distribuidoraferreira.backend.dto.response.ClienteResponse;
import com.distribuidoraferreira.backend.mapper.ClienteMapper;
import com.distribuidoraferreira.backend.models.Cliente;
import com.distribuidoraferreira.backend.repositories.ClienteRepository;
import com.distribuidoraferreira.backend.services.exceptions.ResourceNotFoundException;
import com.distribuidoraferreira.backend.services.interfaces.ClienteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    @Override
    @Transactional
    public ClienteResponse criar(ClienteRequest request) {
        log.info("Criando novo cliente: {}", request.getNome());

        // Validar CPF duplicado se fornecido
        if (request.getCpf() != null && !request.getCpf().isBlank()) {
            clienteRepository.findByCpf(request.getCpf())
                .ifPresent(c -> {
                    throw new IllegalArgumentException("CPF já cadastrado");
                });
        }

        // Validar telefone duplicado
        clienteRepository.findByTelefone(request.getTelefone())
            .ifPresent(c -> {
                throw new IllegalArgumentException("Telefone já cadastrado");
            });

        Cliente cliente = clienteMapper.toEntity(request);
        cliente.setDataCadastro(LocalDateTime.now());
        cliente.setAtivo(request.getAtivo() != null ? request.getAtivo() : true);

        Cliente saved = clienteRepository.save(cliente);
        log.info("Cliente criado com sucesso. ID: {}", saved.getId());

        return clienteMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ClienteResponse atualizar(Long id, ClienteRequest request) {
        log.info("Atualizando cliente ID: {}", id);

        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        // Validar CPF duplicado se alterado
        if (request.getCpf() != null && !request.getCpf().equals(cliente.getCpf())) {
            clienteRepository.findByCpf(request.getCpf())
                .ifPresent(c -> {
                    throw new IllegalArgumentException("CPF já cadastrado para outro cliente");
                });
        }

        // Validar telefone duplicado se alterado
        if (!request.getTelefone().equals(cliente.getTelefone())) {
            clienteRepository.findByTelefone(request.getTelefone())
                .ifPresent(c -> {
                    throw new IllegalArgumentException("Telefone já cadastrado para outro cliente");
                });
        }

        clienteMapper.updateEntityFromRequest(request, cliente);
        Cliente updated = clienteRepository.save(cliente);

        log.info("Cliente atualizado com sucesso. ID: {}", id);
        return clienteMapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponse buscarPorId(Long id) {
        log.debug("Buscando cliente por ID: {}", id);

        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        return clienteMapper.toResponse(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponse> listarTodos() {
        log.debug("Listando todos os clientes");
        List<Cliente> clientes = clienteRepository.findAll();
        return clienteMapper.toResponseList(clientes);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponse> listarAtivos() {
        log.debug("Listando clientes ativos");
        List<Cliente> clientes = clienteRepository.findByAtivoTrue();
        return clienteMapper.toResponseList(clientes);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponse buscarPorCpf(String cpf) {
        log.debug("Buscando cliente por CPF: {}", cpf);

        Cliente cliente = clienteRepository.findByCpf(cpf)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com CPF: " + cpf));

        return clienteMapper.toResponse(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponse buscarPorTelefone(String telefone) {
        log.debug("Buscando cliente por telefone: {}", telefone);

        Cliente cliente = clienteRepository.findByTelefone(telefone)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com telefone: " + telefone));

        return clienteMapper.toResponse(cliente);
    }

    @Override
    @Transactional
    public void inativar(Long id) {
        log.info("Inativando cliente ID: {}", id);

        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        cliente.setAtivo(false);
        clienteRepository.save(cliente);

        log.info("Cliente inativado com sucesso. ID: {}", id);
    }

    @Override
    @Transactional
    public void ativar(Long id) {
        log.info("Ativando cliente ID: {}", id);

        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        cliente.setAtivo(true);
        clienteRepository.save(cliente);

        log.info("Cliente ativado com sucesso. ID: {}", id);
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        log.info("Deletando cliente ID: {}", id);

        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        // Verificar se há comandas ou contas vinculadas
        if (!cliente.getComandasCliente().isEmpty()) {
            throw new IllegalStateException("Cliente possui comandas vinculadas e não pode ser deletado");
        }

        if (!cliente.getContas().isEmpty()) {
            throw new IllegalStateException("Cliente possui contas vinculadas e não pode ser deletado");
        }

        clienteRepository.delete(cliente);
        log.info("Cliente deletado com sucesso. ID: {}", id);
    }
}
