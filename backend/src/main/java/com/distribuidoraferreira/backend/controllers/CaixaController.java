package com.distribuidoraferreira.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.distribuidoraferreira.backend.dtos.CaixaRequest;
import com.distribuidoraferreira.backend.services.interfaces.CaixaService;

@RestController
@RequestMapping("/caixa")
public class CaixaController {

    @Autowired
    CaixaService caixaService;

    // 1. O método abrirCaixa está aderente ao padrão MVC, delegando a lógica para a camada de serviço.
    // Sugestão: Usar ResponseEntity<CaixaResponse> (ou outro DTO) no lugar de ResponseEntity<?> para maior clareza de tipo.
    @PostMapping("/abrir")
    public ResponseEntity<?> abrirCaixa(@RequestBody CaixaRequest caixaRequest) {
        return ResponseEntity.ok().body(caixaService.abrirCaixa(caixaRequest));
    }

    // 2. fecharCaixa também está aderente ao padrão MVC.
    // Sugestão: Definir ResponseEntity<CaixaResponse> para retornar uma resposta mais estruturada, facilitando a manipulação do retorno na view.
    @PostMapping("/fechar/{id}")
    public ResponseEntity<?> fecharCaixa(@PathVariable Long id) {
        return ResponseEntity.ok().body(caixaService.fecharCaixa(id));
    }

    // 3. O método getCaixaById usa `ResponseEntity<?>`, que pode comprometer a segurança de tipo.
    // Sugestão: Definir ResponseEntity<CaixaResponse> para tornar explícito o tipo retornado.
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getCaixaById(@PathVariable Long id) {
        return ResponseEntity.ok().body(caixaService.getCaixaById(id));
    }

    // 4. O método getCaixas retorna todas as instâncias. 
    // Novamente, substitua ResponseEntity<?> por ResponseEntity<List<CaixaResponse>> para mais clareza.
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getCaixas() {
        return ResponseEntity.ok().body(caixaService.getCaixas());
    }

    // 5. getCaixa retorna a instância de caixa aberto atual.
    // Usar ResponseEntity<CaixaResponse> em vez de `<?>` para especificidade e evitar verificação de tipo em runtime.
    @GetMapping("/aberto")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getCaixa() {
        return ResponseEntity.ok().body(caixaService.getCaixa());
    }

    // 6. updateCaixa realiza uma atualização e usa HttpStatus.OK. 
    // Para retorno sem corpo, considere HttpStatus.NO_CONTENT para RESTful mais semântico.
    // Também considere retornar um DTO de resposta com informações da atualização.
    @PutMapping("/{id}")
    public ResponseEntity<String> updateCaixa(@PathVariable("id") Long id,
            @RequestBody CaixaRequest caixaRequest) {
        caixaService.updateCaixa(id, caixaRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 7. verifyCaixaOpen retorna se o caixa está aberto, usando um tipo genérico.
    // Sugestão: Usar ResponseEntity<Boolean> ou um objeto específico para retornar status de abertura.
    @GetMapping("verificar")
    public ResponseEntity<?> verifyCaixaOpen() {
        return ResponseEntity.ok().body(caixaService.isCaixaAberto());
    }

    // 8. getDetalhesCaixa também usa ResponseEntity<?>.
    // Sugestão: Alterar para ResponseEntity<DetalhesCaixaResponse> para maior clareza de tipo e consistência.
    @GetMapping("/detalhes/{id}")
    public ResponseEntity<?> getDetalhesCaixa(@PathVariable Long id) {
        return ResponseEntity.ok().body(caixaService.getDetalhesCaixa(id));
    }

    // 9. getIDCaixaOpen busca o ID do caixa aberto.
    // Para clareza de retorno, use ResponseEntity<Long> em vez de <?>, tornando mais explícito o tipo esperado.
    @GetMapping("pegarID")
    public ResponseEntity<?> getIDCaixaOpen() {
        return ResponseEntity.ok().body(caixaService.getCaixaByID());
    }

}
/*
 * Observações:
 * O controller segue o padrão MVC, delegando lógica ao CaixaService e mantendo o foco em manipular requisições e respostas.
 * O uso de ResponseEntity<?> pode reduzir a segurança de tipo e dificultar a manutenção. Substituir por tipos específicos, 
 * como ResponseEntity<CaixaResponse> ou ResponseEntity<DetalhesCaixaResponse>, tornará o código mais robusto e fácil de entender.
 * Os métodos possuem nomes descritivos que facilitam o entendimento de suas funções.
 * 
 * Sugestão:
 * Organização das Dependências: Use private para o CaixaService para encapsulamento, mesmo com o uso de @Autowired
 * Para manter consistência RESTful, retorne HttpStatus.NO_CONTENT em respostas sem corpo (como em updateCaixa). Isso comunica m
 * elhor que a atualização foi bem-sucedida, sem necessidade de conteúdo.
 */