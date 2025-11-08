package com.distribuidoraferreira.backend.dto.response;

import com.distribuidoraferreira.backend.enums.StatusComanda;
import com.distribuidoraferreira.backend.enums.TipoComanda;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComandaResponse {

    private Long id;
    private String codigo;
    private TipoComanda tipo;
    private StatusComanda status;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataFechamento;
    private String observacoes;
    
    // Mesa (se tipo MESA)
    private Long mesaId;
    private String mesaNumero;
    
    // Clientes vinculados
    private List<ComandaClienteResponse> clientes;
    
    // Totalizadores
    private BigDecimal totalVendas;
    private Integer quantidadeItens;
}
