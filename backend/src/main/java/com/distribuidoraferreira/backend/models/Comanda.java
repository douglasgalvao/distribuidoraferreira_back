package com.distribuidoraferreira.backend.models;

import com.distribuidoraferreira.backend.enums.StatusComanda;
import com.distribuidoraferreira.backend.enums.TipoComanda;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "comandas")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Comanda {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comanda_id")
    private Long id;

    @Column(name = "codigo", unique = true, nullable = false)
    private String codigo;

    @Column(name = "data_abertura", nullable = false)
    private Date dataAbertura;

    @Column(name = "data_fechamento")
    private Date dataFechamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusComanda status;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoComanda tipo;

    @Column(name = "observacoes", length = 500)
    private String observacoes;

    // Relacionamentos
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mesa_id")
    private Mesa mesa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caixa_id", nullable = false)
    private Caixa caixa;

    @OneToMany(mappedBy = "comanda", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ComandaCliente> comandasClientes;

    @OneToMany(mappedBy = "comanda", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Venda> vendas;

    @PrePersist
    public void prePersist() {
        if (this.dataAbertura == null) {
            this.dataAbertura = Date.from(Instant.now());
        }
        if (this.status == null) {
            this.status = StatusComanda.ABERTA;
        }
        if (this.codigo == null) {
            this.codigo = gerarCodigo();
        }
    }

    private String gerarCodigo() {
        // Gera código único: CMD-YYYYMMDD-XXXX
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "CMD-" + timestamp.substring(timestamp.length() - 8) + "-" + uuid;
    }

    public void fechar() {
        this.status = StatusComanda.FECHADA;
        this.dataFechamento = Date.from(Instant.now());
        if (this.mesa != null) {
            this.mesa.liberar();
        }
    }

    public void cancelar() {
        this.status = StatusComanda.CANCELADA;
        this.dataFechamento = Date.from(Instant.now());
        if (this.mesa != null) {
            this.mesa.liberar();
        }
    }

    public void adicionarCliente(ComandaCliente comandaCliente) {
        this.comandasClientes.add(comandaCliente);
        comandaCliente.setComanda(this);
    }

    public void adicionarVenda(Venda venda) {
        this.vendas.add(venda);
        venda.setComanda(this);
    }
}
