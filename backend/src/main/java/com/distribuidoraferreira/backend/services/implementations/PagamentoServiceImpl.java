package com.distribuidoraferreira.backend.services.implementations;

import com.distribuidoraferreira.backend.dto.request.PagamentoRequest;
import com.distribuidoraferreira.backend.dto.response.PagamentoResponse;
import com.distribuidoraferreira.backend.mapper.PagamentoMapper;
import com.distribuidoraferreira.backend.models.ContaCliente;
import com.distribuidoraferreira.backend.models.Pagamento;
import com.distribuidoraferreira.backend.models.Venda;
import com.distribuidoraferreira.backend.repositories.ContaClienteRepository;
import com.distribuidoraferreira.backend.repositories.PagamentoRepository;
import com.distribuidoraferreira.backend.repositories.VendaRepository;
import com.distribuidoraferreira.backend.services.exceptions.ResourceNotFoundException;
import com.distribuidoraferreira.backend.services.interfaces.PagamentoService;
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
public class PagamentoServiceImpl implements PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final ContaClienteRepository contaClienteRepository;
    private final VendaRepository vendaRepository;
    private final PagamentoMapper pagamentoMapper;

    @Override
    @Transactional
    public PagamentoResponse registrar(PagamentoRequest request) {
        log.info("Registrando pagamento de {} para conta {}", 
            request.getValor(), request.getContaClienteId());

        // Buscar conta cliente
        ContaCliente contaCliente = contaClienteRepository.findById(request.getContaClienteId())
            .orElseThrow(() -> new ResourceNotFoundException("Conta cliente não encontrada"));

        if (!contaCliente.podeComprar()) {
            throw new IllegalStateException("Conta cliente está bloqueada ou suspensa");
        }

        // Buscar venda se especificada
        Venda venda = null;
        if (request.getVendaId() != null) {
            venda = vendaRepository.findById(request.getVendaId())
                .orElseThrow(() -> new ResourceNotFoundException("Venda não encontrada"));
        }

        // Criar pagamento
        Pagamento pagamento = pagamentoMapper.toEntity(request);
        pagamento.setContaCliente(contaCliente);
        pagamento.setVenda(venda);
        pagamento.setDataPagamento(LocalDateTime.now());

        Pagamento saved = pagamentoRepository.save(pagamento);
        log.info("Pagamento registrado. ID: {}", saved.getId());

        // Atualizar saldo da conta
        contaCliente.realizarPagamento(request.getValor());
        contaClienteRepository.save(contaCliente);

        log.info("Saldo devedor atualizado. Novo saldo: {}", contaCliente.getSaldoDevedor());

        return pagamentoMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PagamentoResponse buscarPorId(Long id) {
        log.debug("Buscando pagamento por ID: {}", id);

        Pagamento pagamento = pagamentoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado"));

        return pagamentoMapper.toResponse(pagamento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagamentoResponse> listarPorContaCliente(Long contaClienteId) {
        log.debug("Listando pagamentos da conta: {}", contaClienteId);

        if (!contaClienteRepository.existsById(contaClienteId)) {
            throw new ResourceNotFoundException("Conta cliente não encontrada");
        }

        List<Pagamento> pagamentos = pagamentoRepository.findByContaClienteId(contaClienteId);
        return pagamentoMapper.toResponseList(pagamentos);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagamentoResponse> listarPorVenda(Long vendaId) {
        log.debug("Listando pagamentos da venda: {}", vendaId);

        if (!vendaRepository.existsById(vendaId)) {
            throw new ResourceNotFoundException("Venda não encontrada");
        }

        List<Pagamento> pagamentos = pagamentoRepository.findByVendaId(vendaId);
        return pagamentoMapper.toResponseList(pagamentos);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagamentoResponse> listarTodos() {
        log.debug("Listando todos os pagamentos");
        List<Pagamento> pagamentos = pagamentoRepository.findAll();
        return pagamentoMapper.toResponseList(pagamentos);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calcularTotalPagoPorConta(Long contaClienteId) {
        log.debug("Calculando total pago pela conta: {}", contaClienteId);

        if (!contaClienteRepository.existsById(contaClienteId)) {
            throw new ResourceNotFoundException("Conta cliente não encontrada");
        }

        List<Pagamento> pagamentos = pagamentoRepository.findByContaClienteId(contaClienteId);
        
        return pagamentos.stream()
            .map(Pagamento::getValor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        log.info("Deletando pagamento ID: {}", id);

        Pagamento pagamento = pagamentoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado"));

        // Reverter o pagamento na conta
        ContaCliente contaCliente = pagamento.getContaCliente();
        contaCliente.adicionarDebito(pagamento.getValor());
        contaClienteRepository.save(contaCliente);

        pagamentoRepository.delete(pagamento);
        log.info("Pagamento deletado e saldo revertido. ID: {}", id);
    }
}
