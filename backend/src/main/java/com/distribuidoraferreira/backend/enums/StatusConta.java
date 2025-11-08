package com.distribuidoraferreira.backend.enums;

import lombok.Getter;

@Getter
public enum StatusConta {
    ATIVA("ATIVA"),
    SUSPENSA("SUSPENSA"),
    BLOQUEADA("BLOQUEADA"),
    QUITADA("QUITADA");

    private final String status;

    StatusConta(String status) {
        this.status = status;
    }
}
