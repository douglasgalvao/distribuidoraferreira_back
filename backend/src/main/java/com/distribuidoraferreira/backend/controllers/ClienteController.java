package com.distribuidoraferreira.backend.controllers;

import com.distribuidoraferreira.backend.dto.request.ClienteRequest;
import com.distribuidoraferreira.backend.dto.response.ClienteResponse;
import com.distribuidoraferreira.backend.services.implementations.ClienteServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClienteController {

    private final ClienteServiceImpl clienteService;

    @PostMapping
    public ResponseEntity<ClienteResponse> criar(@Valid @RequestBody ClienteRequest request) {
        log.info("POST /api/clientes - Criando novo cliente");
        ClienteResponse response = clienteService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ClienteRequest request) {
        log.info("PUT /api/clientes/{} - Atualizando cliente", id);
        ClienteResponse response = clienteService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> buscarPorId(@PathVariable Long id) {
        log.info("GET /api/clientes/{} - Buscando cliente", id);
        ClienteResponse response = clienteService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponse>> listarTodos(
            @RequestParam(required = false, defaultValue = "false") Boolean apenasAtivos) {
        log.info("GET /api/clientes - Listando clientes (ativos: {})", apenasAtivos);
        List<ClienteResponse> response = apenasAtivos ? 
            clienteService.listarAtivos() : clienteService.listarTodos();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<ClienteResponse> buscarPorCpf(@PathVariable String cpf) {
        log.info("GET /api/clientes/cpf/{} - Buscando por CPF", cpf);
        ClienteResponse response = clienteService.buscarPorCpf(cpf);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/telefone/{telefone}")
    public ResponseEntity<ClienteResponse> buscarPorTelefone(@PathVariable String telefone) {
        log.info("GET /api/clientes/telefone/{} - Buscando por telefone", telefone);
        ClienteResponse response = clienteService.buscarPorTelefone(telefone);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<Void> ativar(@PathVariable Long id) {
        log.info("PATCH /api/clientes/{}/ativar - Ativando cliente", id);
        clienteService.ativar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        log.info("PATCH /api/clientes/{}/inativar - Inativando cliente", id);
        clienteService.inativar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        log.info("DELETE /api/clientes/{} - Deletando cliente", id);
        clienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
