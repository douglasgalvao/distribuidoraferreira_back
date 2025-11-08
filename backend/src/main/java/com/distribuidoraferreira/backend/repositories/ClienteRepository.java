package com.distribuidoraferreira.backend.repositories;

import com.distribuidoraferreira.backend.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByCpf(String cpf);
    Optional<Cliente> findByTelefone(String telefone);
    List<Cliente> findByAtivoTrue();
}
