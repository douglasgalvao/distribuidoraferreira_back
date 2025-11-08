package com.distribuidoraferreira.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponse {

    private Long id;
    private String nome;
    private String telefone;
    private String cpf;
    private String email;
    private LocalDateTime dataCadastro;
    private Boolean ativo;
    private String observacoes;
}
