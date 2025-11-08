package com.distribuidoraferreira.backend.services.interfaces;

import com.distribuidoraferreira.backend.dto.request.PagamentoRequest;
import com.distribuidoraferreira.backend.dto.response.PagamentoResponse;

import java.math.BigDecimal;
import java.util.List;

public interface PagamentoService {

    PagamentoResponse registrar(PagamentoRequest request);

    PagamentoResponse buscarPorId(Long id);

    List<PagamentoResponse> listarPorContaCliente(Long contaClienteId);

    List<PagamentoResponse> listarPorVenda(Long vendaId);

    List<PagamentoResponse> listarTodos();

    BigDecimal calcularTotalPagoPorConta(Long contaClienteId);

    void deletar(Long id);
}
