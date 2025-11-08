package com.distribuidoraferreira.backend.controllers;

import com.distribuidoraferreira.backend.dto.request.MesaRequest;
import com.distribuidoraferreira.backend.dto.response.MesaResponse;
import com.distribuidoraferreira.backend.enums.StatusMesa;
import com.distribuidoraferreira.backend.services.implementations.MesaServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/mesas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MesaController {

    private final MesaServiceImpl mesaService;

    @PostMapping
    public ResponseEntity<MesaResponse> criar(@Valid @RequestBody MesaRequest request) {
        log.info("POST /api/mesas - Criando nova mesa");
        MesaResponse response = mesaService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MesaResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody MesaRequest request) {
        log.info("PUT /api/mesas/{} - Atualizando mesa", id);
        MesaResponse response = mesaService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MesaResponse> buscarPorId(@PathVariable Long id) {
        log.info("GET /api/mesas/{} - Buscando mesa", id);
        MesaResponse response = mesaService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<MesaResponse>> listarTodas(
            @RequestParam(required = false, defaultValue = "false") Boolean apenasAtivas) {
        log.info("GET /api/mesas - Listando mesas (ativas: {})", apenasAtivas);
        List<MesaResponse> response = apenasAtivas ? 
            mesaService.listarAtivas() : mesaService.listarTodas();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/numero/{numero}")
    public ResponseEntity<MesaResponse> buscarPorNumero(@PathVariable String numero) {
        log.info("GET /api/mesas/numero/{} - Buscando por número", numero);
        MesaResponse response = mesaService.buscarPorNumero(numero);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<MesaResponse>> listarPorStatus(@PathVariable StatusMesa status) {
        log.info("GET /api/mesas/status/{} - Listando por status", status);
        List<MesaResponse> response = mesaService.listarPorStatus(status);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/ocupar")
    public ResponseEntity<MesaResponse> ocupar(@PathVariable Long id) {
        log.info("PATCH /api/mesas/{}/ocupar - Ocupando mesa", id);
        MesaResponse response = mesaService.ocuparMesa(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/liberar")
    public ResponseEntity<MesaResponse> liberar(@PathVariable Long id) {
        log.info("PATCH /api/mesas/{}/liberar - Liberando mesa", id);
        MesaResponse response = mesaService.liberarMesa(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/reservar")
    public ResponseEntity<MesaResponse> reservar(@PathVariable Long id) {
        log.info("PATCH /api/mesas/{}/reservar - Reservando mesa", id);
        MesaResponse response = mesaService.reservarMesa(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<Void> ativar(@PathVariable Long id) {
        log.info("PATCH /api/mesas/{}/ativar - Ativando mesa", id);
        mesaService.ativar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        log.info("PATCH /api/mesas/{}/inativar - Inativando mesa", id);
        mesaService.inativar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        log.info("DELETE /api/mesas/{} - Deletando mesa", id);
        mesaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
