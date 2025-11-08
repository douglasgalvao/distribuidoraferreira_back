package com.distribuidoraferreira.backend.controllers;

import com.distribuidoraferreira.backend.dto.request.ContaClienteRequest;
import com.distribuidoraferreira.backend.dto.response.ContaClienteResponse;
import com.distribuidoraferreira.backend.services.implementations.ContaClienteServiceImplV2;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v2/contas-cliente")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ContaClienteControllerV2 {

    private final ContaClienteServiceImplV2 contaClienteService;

    @PostMapping
    public ResponseEntity<ContaClienteResponse> criar(@Valid @RequestBody ContaClienteRequest request) {
        log.info("POST /api/v2/contas-cliente - Criando nova conta");
        ContaClienteResponse response = contaClienteService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContaClienteResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ContaClienteRequest request) {
        log.info("PUT /api/v2/contas-cliente/{} - Atualizando conta", id);
        ContaClienteResponse response = contaClienteService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContaClienteResponse> buscarPorId(@PathVariable Long id) {
        log.info("GET /api/v2/contas-cliente/{} - Buscando conta", id);
        ContaClienteResponse response = contaClienteService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ContaClienteResponse>> listarTodas(
            @RequestParam(required = false, defaultValue = "false") Boolean apenasAtivas) {
        log.info("GET /api/v2/contas-cliente - Listando contas (ativas: {})", apenasAtivas);
        List<ContaClienteResponse> response = apenasAtivas ? 
            contaClienteService.listarAtivas() : contaClienteService.listarTodas();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<ContaClienteResponse>> listarPorCliente(@PathVariable Long clienteId) {
        log.info("GET /api/v2/contas-cliente/cliente/{} - Listando por cliente", clienteId);
        List<ContaClienteResponse> response = contaClienteService.listarPorCliente(clienteId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/debito")
    public ResponseEntity<ContaClienteResponse> adicionarDebito(
            @PathVariable Long id,
            @RequestParam BigDecimal valor) {
        log.info("POST /api/v2/contas-cliente/{}/debito - Adicionando débito de {}", id, valor);
        ContaClienteResponse response = contaClienteService.adicionarDebito(id, valor);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/pagamento")
    public ResponseEntity<ContaClienteResponse> realizarPagamento(
            @PathVariable Long id,
            @RequestParam BigDecimal valor) {
        log.info("POST /api/v2/contas-cliente/{}/pagamento - Realizando pagamento de {}", id, valor);
        ContaClienteResponse response = contaClienteService.realizarPagamento(id, valor);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/bloquear")
    public ResponseEntity<ContaClienteResponse> bloquear(@PathVariable Long id) {
        log.info("PATCH /api/v2/contas-cliente/{}/bloquear - Bloqueando conta", id);
        ContaClienteResponse response = contaClienteService.bloquear(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/desbloquear")
    public ResponseEntity<ContaClienteResponse> desbloquear(@PathVariable Long id) {
        log.info("PATCH /api/v2/contas-cliente/{}/desbloquear - Desbloqueando conta", id);
        ContaClienteResponse response = contaClienteService.desbloquear(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/suspender")
    public ResponseEntity<ContaClienteResponse> suspender(@PathVariable Long id) {
        log.info("PATCH /api/v2/contas-cliente/{}/suspender - Suspendendo conta", id);
        ContaClienteResponse response = contaClienteService.suspender(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/reativar")
    public ResponseEntity<ContaClienteResponse> reativar(@PathVariable Long id) {
        log.info("PATCH /api/v2/contas-cliente/{}/reativar - Reativando conta", id);
        ContaClienteResponse response = contaClienteService.reativar(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/fechar")
    public ResponseEntity<ContaClienteResponse> fechar(@PathVariable Long id) {
        log.info("PATCH /api/v2/contas-cliente/{}/fechar - Fechando conta", id);
        ContaClienteResponse response = contaClienteService.fechar(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        log.info("DELETE /api/v2/contas-cliente/{} - Deletando conta", id);
        contaClienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
