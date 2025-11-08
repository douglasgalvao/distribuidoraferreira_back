package com.distribuidoraferreira.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComandaClienteResponse {

    private Long id;
    private Long comandaId;
    private String comandaCodigo;
    private Long clienteId;
    private String clienteNome;
    private String clienteTelefone;
    private BigDecimal percentualDivisao;
    private Boolean principal;
    private LocalDateTime dataVinculo;
    private BigDecimal totalConsumido; // Total das vendas deste cliente nesta comanda
}
