package com.distribuidoraferreira.backend.models;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import com.distribuidoraferreira.backend.enums.StatusVenda;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vendas")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Venda {

    @Id
    @Column(name = "venda_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_hora", nullable = false)
    private Date dataHora;

    @Column(name = "metodo_pagamento", nullable = true)
    private String metodoPagamento;

    @Column(name = "total_venda", nullable = false)
    private double totalVenda;

    @Column(name = "total_pago", nullable = true)
    private Double totalPago;

    @Column(name = "total_pago_pix", nullable = true)
    private Double totalPagoPix;

    @Column(name = "total_pago_debito", nullable = true)
    private Double totalPagoDebito;

    @Column(name = "total_pago_credito", nullable = true)
    private Double totalPagoCredito;

    @Column(name = "total_pago_dinheiro", nullable = true)
    private Double totalPagoDinheiro;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<MovimentacaoEstoque> movimentacoesEstoque;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comanda_id")
    private Comanda comanda;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comanda_cliente_id")
    private ComandaCliente comandaCliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caixa_id", nullable = false)
    private Caixa caixa;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_venda", nullable = false)
    private StatusVenda status;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Pagamento> pagamentos;

    @PrePersist
    public void prePersist() {
        if (this.dataHora == null) {
            this.dataHora = Date.from(Instant.now()); // Valor padrão
        }

        if (this.totalVenda == 0) {
            this.totalVenda = calcularValorTotal();
        }
    }

    private double calcularValorTotal() {
        double total = 0;
        if (movimentacoesEstoque == null) {
            return total;
        }
        for (MovimentacaoEstoque movimentacao : movimentacoesEstoque) {
            total = total + movimentacao.calcularValorTotalMovimentacao();
        }

        return total;
    }
}
