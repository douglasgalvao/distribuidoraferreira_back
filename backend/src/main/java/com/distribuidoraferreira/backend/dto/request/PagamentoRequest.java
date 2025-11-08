package com.distribuidoraferreira.backend.dto.request;

import com.distribuidoraferreira.backend.enums.MetodoPagamento;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagamentoRequest {

    @NotNull(message = "ID da conta cliente é obrigatório")
    private Long contaClienteId;

    private Long vendaId; // Opcional: para quitar venda específica

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal valor;

    @NotNull(message = "Método de pagamento é obrigatório")
    private MetodoPagamento metodoPagamento;

    private String observacoes;
}
