package com.distribuidoraferreira.backend.dto.response;

import com.distribuidoraferreira.backend.enums.StatusMesa;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MesaResponse {

    private Long id;
    private String numero;
    private Integer capacidade;
    private String localizacao;
    private StatusMesa status;
    private Boolean ativo;
    private ComandaResponse comandaAtual; // Comanda ativa na mesa (se houver)
}
