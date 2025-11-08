package com.distribuidoraferreira.backend.repositories;

import com.distribuidoraferreira.backend.enums.StatusConta;
import com.distribuidoraferreira.backend.models.ContaCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ContaClienteRepository extends JpaRepository<ContaCliente, Long> {
    // Métodos legados (manter por compatibilidade)
    Optional<ContaCliente> findByNomeCliente(String nomeCliente);
    Optional<ContaCliente> findByTelefone(String telefone);

    @Query("SELECT cc FROM ContaCliente cc WHERE cc.status = 'INADIMPLENTE'")
    List<ContaCliente> findAllInadimplentes();
    
    // Novos métodos para nova arquitetura
    List<ContaCliente> findByClienteId(Long clienteId);
    List<ContaCliente> findByStatus(StatusConta status);
    List<ContaCliente> findByClienteIdAndStatus(Long clienteId, StatusConta status);
}