package com.distribuidoraferreira.backend.controllers;

import com.distribuidoraferreira.backend.dto.request.PagamentoRequest;
import com.distribuidoraferreira.backend.dto.response.PagamentoResponse;
import com.distribuidoraferreira.backend.services.implementations.PagamentoServiceImpl;
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
@RequestMapping("/api/pagamentos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PagamentoController {

    private final PagamentoServiceImpl pagamentoService;

    @PostMapping
    public ResponseEntity<PagamentoResponse> registrar(@Valid @RequestBody PagamentoRequest request) {
        log.info("POST /api/pagamentos - Registrando novo pagamento");
        PagamentoResponse response = pagamentoService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagamentoResponse> buscarPorId(@PathVariable Long id) {
        log.info("GET /api/pagamentos/{} - Buscando pagamento", id);
        PagamentoResponse response = pagamentoService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PagamentoResponse>> listarTodos() {
        log.info("GET /api/pagamentos - Listando todos os pagamentos");
        List<PagamentoResponse> response = pagamentoService.listarTodos();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/conta/{contaClienteId}")
    public ResponseEntity<List<PagamentoResponse>> listarPorContaCliente(@PathVariable Long contaClienteId) {
        log.info("GET /api/pagamentos/conta/{} - Listando por conta cliente", contaClienteId);
        List<PagamentoResponse> response = pagamentoService.listarPorContaCliente(contaClienteId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/venda/{vendaId}")
    public ResponseEntity<List<PagamentoResponse>> listarPorVenda(@PathVariable Long vendaId) {
        log.info("GET /api/pagamentos/venda/{} - Listando por venda", vendaId);
        List<PagamentoResponse> response = pagamentoService.listarPorVenda(vendaId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/conta/{contaClienteId}/total")
    public ResponseEntity<BigDecimal> calcularTotalPago(@PathVariable Long contaClienteId) {
        log.info("GET /api/pagamentos/conta/{}/total - Calculando total pago", contaClienteId);
        BigDecimal total = pagamentoService.calcularTotalPagoPorConta(contaClienteId);
        return ResponseEntity.ok(total);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        log.info("DELETE /api/pagamentos/{} - Deletando pagamento", id);
        pagamentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
