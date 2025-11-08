package com.distribuidoraferreira.backend.dto.request;

import com.distribuidoraferreira.backend.enums.StatusMesa;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MesaRequest {

    @NotBlank(message = "Número da mesa é obrigatório")
    @Size(max = 10, message = "Número da mesa deve ter no máximo 10 caracteres")
    private String numero;

    @NotNull(message = "Capacidade é obrigatória")
    @Min(value = 1, message = "Capacidade deve ser no mínimo 1")
    private Integer capacidade;

    @Size(max = 100, message = "Localização deve ter no máximo 100 caracteres")
    private String localizacao;

    private StatusMesa status;

    private Boolean ativo;
}
