package com.distribuidoraferreira.backend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Date;

@Entity
@Table(name = "pagamentos")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pagamento_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_cliente_id")
    private ContaCliente contaCliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venda_id")
    private Venda venda;

    @Column(name = "data_pagamento", nullable = false)
    private Date dataPagamento;

    @Column(name = "valor", nullable = false)
    private Double valor;

    @Column(name = "metodo_pagamento", nullable = false)
    private String metodoPagamento;

    @Column(name = "observacoes", length = 500)
    private String observacoes;

    @PrePersist
    public void prePersist() {
        if (this.dataPagamento == null) {
            this.dataPagamento = Date.from(Instant.now());
        }
    }
}
