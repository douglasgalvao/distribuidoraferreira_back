package com.distribuidoraferreira.backend.models;

import com.distribuidoraferreira.backend.enums.StatusConta;
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
@Table(name = "contas_cliente")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ContaCliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conta_cliente_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "data_abertura", nullable = false)
    private Date dataAbertura;

    @Column(name = "data_fechamento")
    private Date dataFechamento;

    @Column(name = "saldo_devedor", nullable = false)
    private Double saldoDevedor = 0.0;

    @Column(name = "total_consumido", nullable = false)
    private Double totalConsumido = 0.0;

    @Column(name = "limite_credito")
    private Double limiteCredito;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusConta status;

    // Relacionamentos
    @OneToMany(mappedBy = "contaCliente", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Pagamento> pagamentos;

    @PrePersist
    public void prePersist() {
        if (this.dataAbertura == null) {
            this.dataAbertura = Date.from(Instant.now());
        }
        if (this.status == null) {
            this.status = StatusConta.ATIVA;
        }
        if (this.saldoDevedor == null) {
            this.saldoDevedor = 0.0;
        }
        if (this.totalConsumido == null) {
            this.totalConsumido = 0.0;
        }
    }

    public void adicionarDebito(Double valor) {
        this.saldoDevedor += valor;
        this.totalConsumido += valor;
        verificarStatus();
    }

    public void realizarPagamento(Double valor) {
        this.saldoDevedor -= valor;
        if (this.saldoDevedor < 0) {
            this.saldoDevedor = 0.0;
        }
        verificarStatus();
    }

    private void verificarStatus() {
        if (this.saldoDevedor == 0) {
            this.status = StatusConta.QUITADA;
        } else if (this.limiteCredito != null && this.saldoDevedor >= this.limiteCredito) {
            this.status = StatusConta.BLOQUEADA;
        } else {
            this.status = StatusConta.ATIVA;
        }
    }

    public void suspender() {
        this.status = StatusConta.SUSPENSA;
    }

    public void bloquear() {
        this.status = StatusConta.BLOQUEADA;
    }

    public void reativar() {
        this.status = StatusConta.ATIVA;
    }

    public boolean podeComprar() {
        if (this.status == StatusConta.BLOQUEADA || this.status == StatusConta.SUSPENSA) {
            return false;
        }
        if (this.limiteCredito != null && this.saldoDevedor >= this.limiteCredito) {
            return false;
        }
        return true;
    }
}
