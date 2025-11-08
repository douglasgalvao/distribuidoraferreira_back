package com.distribuidoraferreira.backend.repositories;

import com.distribuidoraferreira.backend.enums.StatusMesa;
import com.distribuidoraferreira.backend.models.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MesaRepository extends JpaRepository<Mesa, Long> {
    Optional<Mesa> findByNumero(String numero);
    List<Mesa> findByStatus(StatusMesa status);
    List<Mesa> findByAtivoTrue();
}
