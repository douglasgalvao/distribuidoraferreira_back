package com.distribuidoraferreira.backend.services.interfaces;

import com.distribuidoraferreira.backend.dto.request.MesaRequest;
import com.distribuidoraferreira.backend.dto.response.MesaResponse;
import com.distribuidoraferreira.backend.enums.StatusMesa;

import java.util.List;

public interface MesaService {

    MesaResponse criar(MesaRequest request);

    MesaResponse atualizar(Long id, MesaRequest request);

    MesaResponse buscarPorId(Long id);

    List<MesaResponse> listarTodas();

    List<MesaResponse> listarAtivas();

    List<MesaResponse> listarPorStatus(StatusMesa status);

    MesaResponse buscarPorNumero(String numero);

    MesaResponse ocuparMesa(Long id);

    MesaResponse liberarMesa(Long id);

    MesaResponse reservarMesa(Long id);

    void inativar(Long id);

    void ativar(Long id);

    void deletar(Long id);
}
