package com.distribuidoraferreira.backend.dto.response;

import com.distribuidoraferreira.backend.enums.MetodoPagamento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagamentoResponse {

    private Long id;
    private Long contaClienteId;
    private String nomeCliente;
    private Long vendaId;
    private LocalDateTime dataPagamento;
    private BigDecimal valor;
    private MetodoPagamento metodoPagamento;
    private String observacoes;
}
