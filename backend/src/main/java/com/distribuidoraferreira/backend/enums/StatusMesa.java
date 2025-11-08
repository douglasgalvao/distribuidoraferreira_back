package com.distribuidoraferreira.backend.enums;

import lombok.Getter;

@Getter
public enum StatusMesa {
    LIVRE("LIVRE"),
    OCUPADA("OCUPADA"),
    RESERVADA("RESERVADA"),
    MANUTENCAO("MANUTENCAO");

    private final String status;

    StatusMesa(String status) {
        this.status = status;
    }
}
