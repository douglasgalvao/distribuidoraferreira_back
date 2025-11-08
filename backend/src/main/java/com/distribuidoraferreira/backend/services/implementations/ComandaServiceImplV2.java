package com.distribuidoraferreira.backend.services.implementations;

import com.distribuidoraferreira.backend.dto.request.ComandaRequest;
import com.distribuidoraferreira.backend.dto.response.ComandaResponse;
import com.distribuidoraferreira.backend.enums.StatusComanda;
import com.distribuidoraferreira.backend.enums.StatusMesa;
import com.distribuidoraferreira.backend.enums.TipoComanda;
import com.distribuidoraferreira.backend.mapper.ComandaMapper;
import com.distribuidoraferreira.backend.models.Cliente;
import com.distribuidoraferreira.backend.models.Comanda;
import com.distribuidoraferreira.backend.models.ComandaCliente;
import com.distribuidoraferreira.backend.models.Mesa;
import com.distribuidoraferreira.backend.repositories.ClienteRepository;
import com.distribuidoraferreira.backend.repositories.ComandaClienteRepository;
import com.distribuidoraferreira.backend.repositories.ComandaRepository;
import com.distribuidoraferreira.backend.repositories.MesaRepository;
import com.distribuidoraferreira.backend.services.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComandaServiceImplV2 {

    private final ComandaRepository comandaRepository;
    private final ComandaClienteRepository comandaClienteRepository;
    private final ClienteRepository clienteRepository;
    private final MesaRepository mesaRepository;
    private final ComandaMapper comandaMapper;

    @Transactional
    public ComandaResponse criar(ComandaRequest request) {
        log.info("Criando nova comanda do tipo: {}", request.getTipo());

        // Validar mesa se tipo MESA
        Mesa mesa = null;
        if (request.getTipo() == TipoComanda.MESA) {
            if (request.getMesaId() == null) {
                throw new IllegalArgumentException("Mesa é obrigatória para comandas do tipo MESA");
            }

            mesa = mesaRepository.findById(request.getMesaId())
                .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada"));

            if (!mesa.isDisponivel()) {
                throw new IllegalStateException("Mesa não está disponível");
            }

            // Ocupar mesa
            mesa.ocupar();
            mesaRepository.save(mesa);
        }

        // Criar comanda
        Comanda comanda = comandaMapper.toEntity(request);
        comanda.setCodigo(gerarCodigoUnico());
        comanda.setStatus(StatusComanda.ABERTA);
        comanda.setDataAbertura(LocalDateTime.now());
        comanda.setMesa(mesa);

        Comanda savedComanda = comandaRepository.save(comanda);
        log.info("Comanda criada com código: {}", savedComanda.getCodigo());

        // Adicionar clientes se fornecidos
        if (request.getClientes() != null && !request.getClientes().isEmpty()) {
            validarPercentuaisDivisao(request);

            for (var clienteReq : request.getClientes()) {
                Cliente cliente = clienteRepository.findById(clienteReq.getClienteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

                ComandaCliente comandaCliente = new ComandaCliente();
                comandaCliente.setComanda(savedComanda);
                comandaCliente.setCliente(cliente);
                comandaCliente.setPercentualDivisao(clienteReq.getPercentualDivisao() != null ? 
                    clienteReq.getPercentualDivisao() : BigDecimal.ZERO);
                comandaCliente.setPrincipal(clienteReq.getPrincipal() != null ? 
                    clienteReq.getPrincipal() : false);
                comandaCliente.setDataVinculo(LocalDateTime.now());

                comandaClienteRepository.save(comandaCliente);
            }

            // Recarregar comanda com clientes
            savedComanda = comandaRepository.findById(savedComanda.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Comanda não encontrada"));
        }

        return comandaMapper.toResponse(savedComanda);
    }

    @Transactional
    public ComandaResponse adicionarCliente(Long comandaId, Long clienteId, Boolean principal) {
        log.info("Adicionando cliente {} à comanda {}", clienteId, comandaId);

        Comanda comanda = comandaRepository.findById(comandaId)
            .orElseThrow(() -> new ResourceNotFoundException("Comanda não encontrada"));

        if (comanda.getStatus() != StatusComanda.ABERTA) {
            throw new IllegalStateException("Comanda não está aberta");
        }

        Cliente cliente = clienteRepository.findById(clienteId)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        // Verificar se cliente já está na comanda
        boolean clienteJaVinculado = comanda.getComandasClientes().stream()
            .anyMatch(cc -> cc.getCliente().getId().equals(clienteId));

        if (clienteJaVinculado) {
            throw new IllegalStateException("Cliente já está vinculado a esta comanda");
        }

        // Se é principal, remover flag de outros
        if (Boolean.TRUE.equals(principal)) {
            comanda.getComandasClientes().forEach(cc -> {
                cc.setPrincipal(false);
                comandaClienteRepository.save(cc);
            });
        }

        ComandaCliente comandaCliente = new ComandaCliente();
        comandaCliente.setComanda(comanda);
        comandaCliente.setCliente(cliente);
        comandaCliente.setPercentualDivisao(BigDecimal.ZERO);
        comandaCliente.setPrincipal(principal != null ? principal : false);
        comandaCliente.setDataVinculo(LocalDateTime.now());

        comandaClienteRepository.save(comandaCliente);
        log.info("Cliente adicionado à comanda com sucesso");

        // Recarregar comanda
        Comanda updated = comandaRepository.findById(comandaId)
            .orElseThrow(() -> new ResourceNotFoundException("Comanda não encontrada"));

        return comandaMapper.toResponse(updated);
    }

    @Transactional
    public ComandaResponse removerCliente(Long comandaId, Long clienteId) {
        log.info("Removendo cliente {} da comanda {}", clienteId, comandaId);

        Comanda comanda = comandaRepository.findById(comandaId)
            .orElseThrow(() -> new ResourceNotFoundException("Comanda não encontrada"));

        if (comanda.getStatus() != StatusComanda.ABERTA) {
            throw new IllegalStateException("Comanda não está aberta");
        }

        ComandaCliente comandaCliente = comanda.getComandasClientes().stream()
            .filter(cc -> cc.getCliente().getId().equals(clienteId))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Cliente não vinculado a esta comanda"));

        // Verificar se há vendas vinculadas
        if (comandaCliente.getVendas() != null && !comandaCliente.getVendas().isEmpty()) {
            throw new IllegalStateException("Cliente possui vendas vinculadas e não pode ser removido");
        }

        comandaClienteRepository.delete(comandaCliente);
        log.info("Cliente removido da comanda com sucesso");

        // Recarregar comanda
        Comanda updated = comandaRepository.findById(comandaId)
            .orElseThrow(() -> new ResourceNotFoundException("Comanda não encontrada"));

        return comandaMapper.toResponse(updated);
    }

    @Transactional
    public ComandaResponse fecharComanda(Long id) {
        log.info("Fechando comanda ID: {}", id);

        Comanda comanda = comandaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Comanda não encontrada"));

        if (comanda.getStatus() == StatusComanda.FECHADA) {
            throw new IllegalStateException("Comanda já está fechada");
        }

        if (comanda.getStatus() == StatusComanda.CANCELADA) {
            throw new IllegalStateException("Comanda cancelada não pode ser fechada");
        }

        comanda.fechar();

        // Liberar mesa se houver
        if (comanda.getMesa() != null) {
            Mesa mesa = comanda.getMesa();
            mesa.liberar();
            mesaRepository.save(mesa);
        }

        Comanda updated = comandaRepository.save(comanda);
        log.info("Comanda fechada com sucesso");

        return comandaMapper.toResponse(updated);
    }

    @Transactional
    public ComandaResponse cancelarComanda(Long id) {
        log.info("Cancelando comanda ID: {}", id);

        Comanda comanda = comandaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Comanda não encontrada"));

        if (comanda.getStatus() == StatusComanda.FECHADA) {
            throw new IllegalStateException("Comanda já está fechada e não pode ser cancelada");
        }

        if (comanda.getStatus() == StatusComanda.CANCELADA) {
            throw new IllegalStateException("Comanda já está cancelada");
        }

        // Verificar se há vendas
        boolean temVendas = comanda.getComandasClientes().stream()
            .anyMatch(cc -> cc.getVendas() != null && !cc.getVendas().isEmpty());

        if (temVendas) {
            throw new IllegalStateException("Comanda possui vendas e não pode ser cancelada. Feche a comanda.");
        }

        comanda.cancelar();

        // Liberar mesa se houver
        if (comanda.getMesa() != null) {
            Mesa mesa = comanda.getMesa();
            mesa.liberar();
            mesaRepository.save(mesa);
        }

        Comanda updated = comandaRepository.save(comanda);
        log.info("Comanda cancelada com sucesso");

        return comandaMapper.toResponse(updated);
    }

    @Transactional(readOnly = true)
    public ComandaResponse buscarPorId(Long id) {
        log.debug("Buscando comanda por ID: {}", id);

        Comanda comanda = comandaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Comanda não encontrada"));

        return comandaMapper.toResponse(comanda);
    }

    @Transactional(readOnly = true)
    public ComandaResponse buscarPorCodigo(String codigo) {
        log.debug("Buscando comanda por código: {}", codigo);

        Comanda comanda = comandaRepository.findByCodigo(codigo)
            .orElseThrow(() -> new ResourceNotFoundException("Comanda não encontrada com código: " + codigo));

        return comandaMapper.toResponse(comanda);
    }

    @Transactional(readOnly = true)
    public List<ComandaResponse> listarTodas() {
        log.debug("Listando todas as comandas");
        List<Comanda> comandas = comandaRepository.findAll();
        return comandaMapper.toResponseList(comandas);
    }

    @Transactional(readOnly = true)
    public List<ComandaResponse> listarPorStatus(StatusComanda status) {
        log.debug("Listando comandas por status: {}", status);
        List<Comanda> comandas = comandaRepository.findByStatus(status);
        return comandaMapper.toResponseList(comandas);
    }

    @Transactional(readOnly = true)
    public List<ComandaResponse> listarPorTipo(TipoComanda tipo) {
        log.debug("Listando comandas por tipo: {}", tipo);
        List<Comanda> comandas = comandaRepository.findByTipo(tipo);
        return comandaMapper.toResponseList(comandas);
    }

    @Transactional(readOnly = true)
    public List<ComandaResponse> listarPorMesa(Long mesaId) {
        log.debug("Listando comandas por mesa: {}", mesaId);

        Mesa mesa = mesaRepository.findById(mesaId)
            .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada"));

        List<Comanda> comandas = comandaRepository.findByMesa(mesa);
        return comandaMapper.toResponseList(comandas);
    }

    @Transactional
    public void deletar(Long id) {
        log.info("Deletando comanda ID: {}", id);

        Comanda comanda = comandaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Comanda não encontrada"));

        if (comanda.getStatus() == StatusComanda.ABERTA) {
            throw new IllegalStateException("Não é possível deletar comanda aberta. Cancele primeiro.");
        }

        // Verificar se há vendas
        boolean temVendas = comanda.getComandasClientes().stream()
            .anyMatch(cc -> cc.getVendas() != null && !cc.getVendas().isEmpty());

        if (temVendas) {
            throw new IllegalStateException("Comanda possui vendas e não pode ser deletada");
        }

        comandaRepository.delete(comanda);
        log.info("Comanda deletada com sucesso. ID: {}", id);
    }

    // Métodos auxiliares

    private String gerarCodigoUnico() {
        String codigo;
        do {
            codigo = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (comandaRepository.findByCodigo(codigo).isPresent());
        return codigo;
    }

    private void validarPercentuaisDivisao(ComandaRequest request) {
        if (request.getClientes() == null || request.getClientes().isEmpty()) {
            return;
        }

        BigDecimal somaPercentuais = request.getClientes().stream()
            .map(c -> c.getPercentualDivisao() != null ? c.getPercentualDivisao() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Se há percentuais definidos, a soma deve ser 100
        if (somaPercentuais.compareTo(BigDecimal.ZERO) > 0 && 
            somaPercentuais.compareTo(new BigDecimal("100")) != 0) {
            throw new IllegalArgumentException("Soma dos percentuais de divisão deve ser 100%");
        }

        // Verificar se há apenas um cliente principal
        long principaisCount = request.getClientes().stream()
            .filter(c -> Boolean.TRUE.equals(c.getPrincipal()))
            .count();

        if (principaisCount > 1) {
            throw new IllegalArgumentException("Apenas um cliente pode ser marcado como principal");
        }
    }
}
