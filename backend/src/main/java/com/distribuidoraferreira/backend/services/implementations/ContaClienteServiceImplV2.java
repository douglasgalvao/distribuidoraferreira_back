package com.distribuidoraferreira.backend.services.implementations;

import com.distribuidoraferreira.backend.dto.request.ContaClienteRequest;
import com.distribuidoraferreira.backend.dto.response.ContaClienteResponse;
import com.distribuidoraferreira.backend.enums.StatusConta;
import com.distribuidoraferreira.backend.mapper.ContaClienteMapper;
import com.distribuidoraferreira.backend.models.Cliente;
import com.distribuidoraferreira.backend.models.ContaCliente;
import com.distribuidoraferreira.backend.repositories.ClienteRepository;
import com.distribuidoraferreira.backend.repositories.ContaClienteRepository;
import com.distribuidoraferreira.backend.services.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContaClienteServiceImplV2 {

    private final ContaClienteRepository contaClienteRepository;
    private final ClienteRepository clienteRepository;
    private final ContaClienteMapper contaClienteMapper;

    @Transactional
    public ContaClienteResponse criar(ContaClienteRequest request) {
        log.info("Criando nova conta para cliente ID: {}", request.getClienteId());

        Cliente cliente = clienteRepository.findById(request.getClienteId())
            .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        if (!cliente.getAtivo()) {
            throw new IllegalStateException("Cliente está inativo");
        }

        // Verificar se cliente já possui conta ativa
        List<ContaCliente> contasAtivas = contaClienteRepository.findByClienteIdAndStatus(
            request.getClienteId(), StatusConta.ATIVA);

        if (!contasAtivas.isEmpty()) {
            throw new IllegalStateException("Cliente já possui uma conta ativa");
        }

        ContaCliente contaCliente = contaClienteMapper.toEntity(request);
        contaCliente.setCliente(cliente);
        contaCliente.setDataAbertura(LocalDateTime.now());
        contaCliente.setSaldoDevedor(BigDecimal.ZERO);
        contaCliente.setTotalConsumido(BigDecimal.ZERO);
        contaCliente.setStatus(StatusConta.ATIVA);

        ContaCliente saved = contaClienteRepository.save(contaCliente);
        log.info("Conta criada com sucesso. ID: {}", saved.getId());

        return contaClienteMapper.toResponse(saved);
    }

    @Transactional
    public ContaClienteResponse atualizar(Long id, ContaClienteRequest request) {
        log.info("Atualizando conta ID: {}", id);

        ContaCliente contaCliente = contaClienteRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada"));

        contaClienteMapper.updateEntityFromRequest(request, contaCliente);
        ContaCliente updated = contaClienteRepository.save(contaCliente);

        log.info("Conta atualizada com sucesso. ID: {}", id);
        return contaClienteMapper.toResponse(updated);
    }

    @Transactional(readOnly = true)
    public ContaClienteResponse buscarPorId(Long id) {
        log.debug("Buscando conta por ID: {}", id);

        ContaCliente contaCliente = contaClienteRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada"));

        return contaClienteMapper.toResponse(contaCliente);
    }

    @Transactional(readOnly = true)
    public List<ContaClienteResponse> listarTodas() {
        log.debug("Listando todas as contas");
        List<ContaCliente> contas = contaClienteRepository.findAll();
        return contaClienteMapper.toResponseList(contas);
    }

    @Transactional(readOnly = true)
    public List<ContaClienteResponse> listarPorCliente(Long clienteId) {
        log.debug("Listando contas do cliente: {}", clienteId);

        if (!clienteRepository.existsById(clienteId)) {
            throw new ResourceNotFoundException("Cliente não encontrado");
        }

        List<ContaCliente> contas = contaClienteRepository.findByClienteId(clienteId);
        return contaClienteMapper.toResponseList(contas);
    }

    @Transactional(readOnly = true)
    public List<ContaClienteResponse> listarAtivas() {
        log.debug("Listando contas ativas");
        List<ContaCliente> contas = contaClienteRepository.findByStatus(StatusConta.ATIVA);
        return contaClienteMapper.toResponseList(contas);
    }

    @Transactional
    public ContaClienteResponse adicionarDebito(Long id, BigDecimal valor) {
        log.info("Adicionando débito de {} à conta {}", valor, id);

        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor do débito deve ser maior que zero");
        }

        ContaCliente contaCliente = contaClienteRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada"));

        if (!contaCliente.podeComprar()) {
            throw new IllegalStateException("Conta não pode realizar compras no momento");
        }

        contaCliente.adicionarDebito(valor);
        contaCliente.verificarStatus();

        ContaCliente updated = contaClienteRepository.save(contaCliente);
        log.info("Débito adicionado. Novo saldo: {}", updated.getSaldoDevedor());

        return contaClienteMapper.toResponse(updated);
    }

    @Transactional
    public ContaClienteResponse realizarPagamento(Long id, BigDecimal valor) {
        log.info("Realizando pagamento de {} na conta {}", valor, id);

        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor do pagamento deve ser maior que zero");
        }

        ContaCliente contaCliente = contaClienteRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada"));

        if (valor.compareTo(contaCliente.getSaldoDevedor()) > 0) {
            log.warn("Valor do pagamento ({}) é maior que o saldo devedor ({})", 
                valor, contaCliente.getSaldoDevedor());
        }

        contaCliente.realizarPagamento(valor);
        contaCliente.verificarStatus();

        ContaCliente updated = contaClienteRepository.save(contaCliente);
        log.info("Pagamento realizado. Novo saldo: {}", updated.getSaldoDevedor());

        return contaClienteMapper.toResponse(updated);
    }

    @Transactional
    public ContaClienteResponse bloquear(Long id) {
        log.info("Bloqueando conta ID: {}", id);

        ContaCliente contaCliente = contaClienteRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada"));

        if (contaCliente.getStatus() == StatusConta.QUITADA) {
            throw new IllegalStateException("Conta quitada não pode ser bloqueada");
        }

        contaCliente.setStatus(StatusConta.BLOQUEADA);
        ContaCliente updated = contaClienteRepository.save(contaCliente);

        log.info("Conta bloqueada com sucesso. ID: {}", id);
        return contaClienteMapper.toResponse(updated);
    }

    @Transactional
    public ContaClienteResponse desbloquear(Long id) {
        log.info("Desbloqueando conta ID: {}", id);

        ContaCliente contaCliente = contaClienteRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada"));

        if (contaCliente.getStatus() != StatusConta.BLOQUEADA) {
            throw new IllegalStateException("Conta não está bloqueada");
        }

        contaCliente.setStatus(StatusConta.ATIVA);
        ContaCliente updated = contaClienteRepository.save(contaCliente);

        log.info("Conta desbloqueada com sucesso. ID: {}", id);
        return contaClienteMapper.toResponse(updated);
    }

    @Transactional
    public ContaClienteResponse suspender(Long id) {
        log.info("Suspendendo conta ID: {}", id);

        ContaCliente contaCliente = contaClienteRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada"));

        if (contaCliente.getStatus() == StatusConta.QUITADA) {
            throw new IllegalStateException("Conta quitada não pode ser suspensa");
        }

        contaCliente.setStatus(StatusConta.SUSPENSA);
        ContaCliente updated = contaClienteRepository.save(contaCliente);

        log.info("Conta suspensa com sucesso. ID: {}", id);
        return contaClienteMapper.toResponse(updated);
    }

    @Transactional
    public ContaClienteResponse reativar(Long id) {
        log.info("Reativando conta ID: {}", id);

        ContaCliente contaCliente = contaClienteRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada"));

        if (contaCliente.getStatus() != StatusConta.SUSPENSA) {
            throw new IllegalStateException("Conta não está suspensa");
        }

        contaCliente.setStatus(StatusConta.ATIVA);
        ContaCliente updated = contaClienteRepository.save(contaCliente);

        log.info("Conta reativada com sucesso. ID: {}", id);
        return contaClienteMapper.toResponse(updated);
    }

    @Transactional
    public ContaClienteResponse fechar(Long id) {
        log.info("Fechando conta ID: {}", id);

        ContaCliente contaCliente = contaClienteRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada"));

        if (contaCliente.getSaldoDevedor().compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException("Conta possui saldo devedor e não pode ser fechada");
        }

        contaCliente.setStatus(StatusConta.QUITADA);
        contaCliente.setDataFechamento(LocalDateTime.now());

        ContaCliente updated = contaClienteRepository.save(contaCliente);
        log.info("Conta fechada com sucesso. ID: {}", id);

        return contaClienteMapper.toResponse(updated);
    }

    @Transactional
    public void deletar(Long id) {
        log.info("Deletando conta ID: {}", id);

        ContaCliente contaCliente = contaClienteRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada"));

        if (contaCliente.getSaldoDevedor().compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException("Conta possui saldo devedor e não pode ser deletada");
        }

        if (contaCliente.getPagamentos() != null && !contaCliente.getPagamentos().isEmpty()) {
            throw new IllegalStateException("Conta possui pagamentos registrados e não pode ser deletada");
        }

        contaClienteRepository.delete(contaCliente);
        log.info("Conta deletada com sucesso. ID: {}", id);
    }
}
