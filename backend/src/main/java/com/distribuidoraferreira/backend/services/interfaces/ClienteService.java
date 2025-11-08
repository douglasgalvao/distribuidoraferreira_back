package com.distribuidoraferreira.backend.services.interfaces;

import com.distribuidoraferreira.backend.dto.request.ClienteRequest;
import com.distribuidoraferreira.backend.dto.response.ClienteResponse;

import java.util.List;

public interface ClienteService {

    ClienteResponse criar(ClienteRequest request);

    ClienteResponse atualizar(Long id, ClienteRequest request);

    ClienteResponse buscarPorId(Long id);

    List<ClienteResponse> listarTodos();

    List<ClienteResponse> listarAtivos();

    ClienteResponse buscarPorCpf(String cpf);

    ClienteResponse buscarPorTelefone(String telefone);

    void inativar(Long id);

    void ativar(Long id);

    void deletar(Long id);
}
