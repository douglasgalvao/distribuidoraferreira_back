package com.distribuidoraferreira.backend.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContaClienteRequest {

    @NotNull(message = "ID do cliente é obrigatório")
    private Long clienteId;

    @DecimalMin(value = "0.00", message = "Limite de crédito não pode ser negativo")
    private BigDecimal limiteCredito;

    private String observacoes;
}
