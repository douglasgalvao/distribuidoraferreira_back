package com.distribuidoraferreira.backend.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComandaClienteRequest {

    @NotNull(message = "ID da comanda é obrigatório")
    private Long comandaId;

    @NotNull(message = "ID do cliente é obrigatório")
    private Long clienteId;

    @DecimalMin(value = "0.00", message = "Percentual de divisão deve ser no mínimo 0")
    @DecimalMax(value = "100.00", message = "Percentual de divisão deve ser no máximo 100")
    private BigDecimal percentualDivisao;

    private Boolean principal;
}
