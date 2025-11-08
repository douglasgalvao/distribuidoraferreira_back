package com.distribuidoraferreira.backend.repositories;

import com.distribuidoraferreira.backend.enums.StatusComanda;
import com.distribuidoraferreira.backend.enums.TipoComanda;
import com.distribuidoraferreira.backend.models.Comanda;
import com.distribuidoraferreira.backend.models.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ComandaRepository extends JpaRepository<Comanda, Long> {
    // Métodos legados (manter por compatibilidade)
    List<Comanda> findComandaByStatus(String status);
    List<Comanda> findComandaByIdCaixa(Long idCaixa);
    List<Comanda> findComandaByIdCliente(Long idCliente);
    
    // Novos métodos para nova arquitetura
    Optional<Comanda> findByCodigo(String codigo);
    List<Comanda> findByStatus(StatusComanda status);
    List<Comanda> findByTipo(TipoComanda tipo);
    List<Comanda> findByMesa(Mesa mesa);
    List<Comanda> findByMesaId(Long mesaId);
}