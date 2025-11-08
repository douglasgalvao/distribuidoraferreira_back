package com.distribuidoraferreira.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "comandas_clientes")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ComandaCliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comanda_cliente_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comanda_id", nullable = false)
    private Comanda comanda;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "percentual_divisao")
    private Double percentualDivisao;

    @Column(name = "principal", nullable = false)
    private Boolean principal = false;

    @Column(name = "data_vinculo", nullable = false)
    private Date dataVinculo;

    // Relacionamentos
    @OneToMany(mappedBy = "comandaCliente", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Venda> vendas;

    @PrePersist
    public void prePersist() {
        if (this.dataVinculo == null) {
            this.dataVinculo = Date.from(Instant.now());
        }
        if (this.principal == null) {
            this.principal = false;
        }
        if (this.percentualDivisao == null) {
            this.percentualDivisao = 100.0;
        }
    }
}
