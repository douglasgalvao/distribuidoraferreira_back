package com.distribuidoraferreira.backend.repositories;

import com.distribuidoraferreira.backend.models.ComandaCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComandaClienteRepository extends JpaRepository<ComandaCliente, Long> {
    List<ComandaCliente> findByComandaId(Long comandaId);
    List<ComandaCliente> findByClienteId(Long clienteId);
    List<ComandaCliente> findByComandaIdAndPrincipalTrue(Long comandaId);
}
