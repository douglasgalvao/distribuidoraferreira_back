package com.distribuidoraferreira.backend.enums;

import lombok.Getter;

@Getter
public enum TipoComanda {
    BALCAO("BALCAO"),
    MESA("MESA"),
    DELIVERY("DELIVERY");

    private final String tipo;

    TipoComanda(String tipo) {
        this.tipo = tipo;
    }
}
