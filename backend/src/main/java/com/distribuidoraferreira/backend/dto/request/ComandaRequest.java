package com.distribuidoraferreira.backend.dto.request;

import com.distribuidoraferreira.backend.enums.TipoComanda;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComandaRequest {

    @NotNull(message = "Tipo de comanda é obrigatório")
    private TipoComanda tipo;

    private Long mesaId; // Obrigatório apenas para tipo MESA

    @Size(max = 500, message = "Observações devem ter no máximo 500 caracteres")
    private String observacoes;

    private List<ComandaClienteRequest> clientes; // Lista de clientes para vincular
}
