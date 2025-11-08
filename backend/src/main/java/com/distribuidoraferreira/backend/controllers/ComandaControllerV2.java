package com.distribuidoraferreira.backend.controllers;

import com.distribuidoraferreira.backend.dto.request.ComandaRequest;
import com.distribuidoraferreira.backend.dto.response.ComandaResponse;
import com.distribuidoraferreira.backend.enums.StatusComanda;
import com.distribuidoraferreira.backend.enums.TipoComanda;
import com.distribuidoraferreira.backend.services.implementations.ComandaServiceImplV2;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v2/comandas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ComandaControllerV2 {

    private final ComandaServiceImplV2 comandaService;

    @PostMapping
    public ResponseEntity<ComandaResponse> criar(@Valid @RequestBody ComandaRequest request) {
        log.info("POST /api/v2/comandas - Criando nova comanda");
        ComandaResponse response = comandaService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComandaResponse> buscarPorId(@PathVariable Long id) {
        log.info("GET /api/v2/comandas/{} - Buscando comanda", id);
        ComandaResponse response = comandaService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<ComandaResponse> buscarPorCodigo(@PathVariable String codigo) {
        log.info("GET /api/v2/comandas/codigo/{} - Buscando por código", codigo);
        ComandaResponse response = comandaService.buscarPorCodigo(codigo);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ComandaResponse>> listarTodas() {
        log.info("GET /api/v2/comandas - Listando todas as comandas");
        List<ComandaResponse> response = comandaService.listarTodas();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ComandaResponse>> listarPorStatus(@PathVariable StatusComanda status) {
        log.info("GET /api/v2/comandas/status/{} - Listando por status", status);
        List<ComandaResponse> response = comandaService.listarPorStatus(status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<ComandaResponse>> listarPorTipo(@PathVariable TipoComanda tipo) {
        log.info("GET /api/v2/comandas/tipo/{} - Listando por tipo", tipo);
        List<ComandaResponse> response = comandaService.listarPorTipo(tipo);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/mesa/{mesaId}")
    public ResponseEntity<List<ComandaResponse>> listarPorMesa(@PathVariable Long mesaId) {
        log.info("GET /api/v2/comandas/mesa/{} - Listando por mesa", mesaId);
        List<ComandaResponse> response = comandaService.listarPorMesa(mesaId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/clientes/{clienteId}")
    public ResponseEntity<ComandaResponse> adicionarCliente(
            @PathVariable Long id,
            @PathVariable Long clienteId,
            @RequestParam(required = false, defaultValue = "false") Boolean principal) {
        log.info("POST /api/v2/comandas/{}/clientes/{} - Adicionando cliente", id, clienteId);
        ComandaResponse response = comandaService.adicionarCliente(id, clienteId, principal);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/clientes/{clienteId}")
    public ResponseEntity<ComandaResponse> removerCliente(
            @PathVariable Long id,
            @PathVariable Long clienteId) {
        log.info("DELETE /api/v2/comandas/{}/clientes/{} - Removendo cliente", id, clienteId);
        ComandaResponse response = comandaService.removerCliente(id, clienteId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/fechar")
    public ResponseEntity<ComandaResponse> fechar(@PathVariable Long id) {
        log.info("PATCH /api/v2/comandas/{}/fechar - Fechando comanda", id);
        ComandaResponse response = comandaService.fecharComanda(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ComandaResponse> cancelar(@PathVariable Long id) {
        log.info("PATCH /api/v2/comandas/{}/cancelar - Cancelando comanda", id);
        ComandaResponse response = comandaService.cancelarComanda(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        log.info("DELETE /api/v2/comandas/{} - Deletando comanda", id);
        comandaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
