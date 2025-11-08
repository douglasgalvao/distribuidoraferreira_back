package com.distribuidoraferreira.backend.repositories;

import com.distribuidoraferreira.backend.models.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    List<Pagamento> findByContaClienteId(Long contaClienteId);
    List<Pagamento> findByVendaId(Long vendaId);
}
