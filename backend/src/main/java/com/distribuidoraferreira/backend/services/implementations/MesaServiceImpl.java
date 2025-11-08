package com.distribuidoraferreira.backend.services.implementations;

import com.distribuidoraferreira.backend.dto.request.MesaRequest;
import com.distribuidoraferreira.backend.dto.response.MesaResponse;
import com.distribuidoraferreira.backend.enums.StatusMesa;
import com.distribuidoraferreira.backend.mapper.MesaMapper;
import com.distribuidoraferreira.backend.models.Mesa;
import com.distribuidoraferreira.backend.repositories.MesaRepository;
import com.distribuidoraferreira.backend.services.exceptions.ResourceNotFoundException;
import com.distribuidoraferreira.backend.services.interfaces.MesaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MesaServiceImpl implements MesaService {

    private final MesaRepository mesaRepository;
    private final MesaMapper mesaMapper;

    @Override
    @Transactional
    public MesaResponse criar(MesaRequest request) {
        log.info("Criando nova mesa: {}", request.getNumero());

        // Validar número duplicado
        mesaRepository.findByNumero(request.getNumero())
            .ifPresent(m -> {
                throw new IllegalArgumentException("Número de mesa já cadastrado");
            });

        Mesa mesa = mesaMapper.toEntity(request);
        mesa.setStatus(request.getStatus() != null ? request.getStatus() : StatusMesa.LIVRE);
        mesa.setAtivo(request.getAtivo() != null ? request.getAtivo() : true);

        Mesa saved = mesaRepository.save(mesa);
        log.info("Mesa criada com sucesso. ID: {}", saved.getId());

        return mesaMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public MesaResponse atualizar(Long id, MesaRequest request) {
        log.info("Atualizando mesa ID: {}", id);

        Mesa mesa = mesaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada"));

        // Validar número duplicado se alterado
        if (!request.getNumero().equals(mesa.getNumero())) {
            mesaRepository.findByNumero(request.getNumero())
                .ifPresent(m -> {
                    throw new IllegalArgumentException("Número de mesa já cadastrado");
                });
        }

        mesaMapper.updateEntityFromRequest(request, mesa);
        Mesa updated = mesaRepository.save(mesa);

        log.info("Mesa atualizada com sucesso. ID: {}", id);
        return mesaMapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public MesaResponse buscarPorId(Long id) {
        log.debug("Buscando mesa por ID: {}", id);

        Mesa mesa = mesaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada"));

        return mesaMapper.toResponse(mesa);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MesaResponse> listarTodas() {
        log.debug("Listando todas as mesas");
        List<Mesa> mesas = mesaRepository.findAll();
        return mesaMapper.toResponseList(mesas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MesaResponse> listarAtivas() {
        log.debug("Listando mesas ativas");
        List<Mesa> mesas = mesaRepository.findByAtivoTrue();
        return mesaMapper.toResponseList(mesas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MesaResponse> listarPorStatus(StatusMesa status) {
        log.debug("Listando mesas por status: {}", status);
        List<Mesa> mesas = mesaRepository.findByStatus(status);
        return mesaMapper.toResponseList(mesas);
    }

    @Override
    @Transactional(readOnly = true)
    public MesaResponse buscarPorNumero(String numero) {
        log.debug("Buscando mesa por número: {}", numero);

        Mesa mesa = mesaRepository.findByNumero(numero)
            .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada com número: " + numero));

        return mesaMapper.toResponse(mesa);
    }

    @Override
    @Transactional
    public MesaResponse ocuparMesa(Long id) {
        log.info("Ocupando mesa ID: {}", id);

        Mesa mesa = mesaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada"));

        if (!mesa.isDisponivel()) {
            throw new IllegalStateException("Mesa não está disponível para ocupação");
        }

        mesa.ocupar();
        Mesa updated = mesaRepository.save(mesa);

        log.info("Mesa ocupada com sucesso. ID: {}", id);
        return mesaMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public MesaResponse liberarMesa(Long id) {
        log.info("Liberando mesa ID: {}", id);

        Mesa mesa = mesaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada"));

        mesa.liberar();
        Mesa updated = mesaRepository.save(mesa);

        log.info("Mesa liberada com sucesso. ID: {}", id);
        return mesaMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public MesaResponse reservarMesa(Long id) {
        log.info("Reservando mesa ID: {}", id);

        Mesa mesa = mesaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada"));

        if (!mesa.isDisponivel()) {
            throw new IllegalStateException("Mesa não está disponível para reserva");
        }

        mesa.reservar();
        Mesa updated = mesaRepository.save(mesa);

        log.info("Mesa reservada com sucesso. ID: {}", id);
        return mesaMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void inativar(Long id) {
        log.info("Inativando mesa ID: {}", id);

        Mesa mesa = mesaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada"));

        if (mesa.getStatus() == StatusMesa.OCUPADA) {
            throw new IllegalStateException("Não é possível inativar mesa ocupada");
        }

        mesa.setAtivo(false);
        mesaRepository.save(mesa);

        log.info("Mesa inativada com sucesso. ID: {}", id);
    }

    @Override
    @Transactional
    public void ativar(Long id) {
        log.info("Ativando mesa ID: {}", id);

        Mesa mesa = mesaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada"));

        mesa.setAtivo(true);
        mesaRepository.save(mesa);

        log.info("Mesa ativada com sucesso. ID: {}", id);
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        log.info("Deletando mesa ID: {}", id);

        Mesa mesa = mesaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada"));

        if (mesa.getStatus() == StatusMesa.OCUPADA) {
            throw new IllegalStateException("Não é possível deletar mesa ocupada");
        }

        if (!mesa.getComandas().isEmpty()) {
            throw new IllegalStateException("Mesa possui comandas vinculadas e não pode ser deletada");
        }

        mesaRepository.delete(mesa);
        log.info("Mesa deletada com sucesso. ID: {}", id);
    }
}
