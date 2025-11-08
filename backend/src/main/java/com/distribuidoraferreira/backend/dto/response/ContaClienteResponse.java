package com.distribuidoraferreira.backend.dto.response;

import com.distribuidoraferreira.backend.enums.StatusConta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContaClienteResponse {

    private Long id;
    private Long clienteId;
    private String clienteNome;
    private String clienteTelefone;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataFechamento;
    private BigDecimal saldoDevedor;
    private BigDecimal totalConsumido;
    private BigDecimal limiteCredito;
    private StatusConta status;
    private String observacoes;
    
    // Informações calculadas
    private BigDecimal creditoDisponivel; // limiteCredito - saldoDevedor
    private Boolean podeComprar;
}
