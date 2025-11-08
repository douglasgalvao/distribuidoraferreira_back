package com.distribuidoraferreira.backend.models;

import com.distribuidoraferreira.backend.enums.StatusMesa;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "mesas")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mesa_id")
    private Long id;

    @Column(name = "numero", unique = true, nullable = false)
    private Integer numero;

    @Column(name = "capacidade")
    private Integer capacidade;

    @Column(name = "localizacao")
    private String localizacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusMesa status;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    // Relacionamentos
    @OneToMany(mappedBy = "mesa", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Comanda> comandas;

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = StatusMesa.LIVRE;
        }
        if (this.ativo == null) {
            this.ativo = true;
        }
    }

    public void ocupar() {
        this.status = StatusMesa.OCUPADA;
    }

    public void liberar() {
        this.status = StatusMesa.LIVRE;
    }

    public void reservar() {
        this.status = StatusMesa.RESERVADA;
    }

    public boolean isDisponivel() {
        return this.status == StatusMesa.LIVRE;
    }
}
