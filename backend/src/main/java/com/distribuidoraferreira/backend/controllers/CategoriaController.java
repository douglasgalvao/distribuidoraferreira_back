package com.distribuidoraferreira.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.distribuidoraferreira.backend.dtos.CategoriaRequest;
import com.distribuidoraferreira.backend.dtos.CategoriaResponse;
import com.distribuidoraferreira.backend.dtos.GenericResponse;
import com.distribuidoraferreira.backend.services.interfaces.CategoriaService;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    @Autowired
    CategoriaService categoriaService;

    // 1. Considere alterar a visibilidade de "categoriaService" para private para garantir encapsulamento.
    // @Autowired
    // private CategoriaService categoriaService;

    // 2. O método addCategoria segue bem o padrão MVC, recebendo um DTO e delegando a lógica para a camada de serviço.
    // Sugestão: Adicionar valores de retorno para melhor feedback ao usuário (por exemplo, o ID do recurso criado ou o próprio objeto).
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addCategoria(@RequestBody CategoriaRequest categoriaRequest) {
        categoriaService.addCategoria(categoriaRequest);
    }

    // 3. O uso de ResponseEntity aqui é bom para flexibilidade ao retornar diferentes status HTTP.
    // No entanto, considere mover o HttpStatus.OK para o próprio ResponseEntity (por exemplo, ResponseEntity.ok()) para maior consistência.
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GenericResponse<List<CategoriaResponse>>> getCategorias() {
        return ResponseEntity.ok().body(categoriaService.getCategorias());
    }

    // 4. getCategoriaById: O uso de ResponseEntity<?> é amplo e pode não oferecer segurança de tipo suficiente.
    // Sugestão: Substitua por ResponseEntity<CategoriaResponse> ou um wrapper como GenericResponse para melhor legibilidade e controle de tipo.
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getCategoriaById(@PathVariable Long id) {
        return ResponseEntity.ok().body(categoriaService.getCategoriaById(id));
    }

    // 5. getCategoriaByNome: Novamente, o uso de `<?>` reduz a segurança de tipo.
    // Sugestão: Use ResponseEntity<CategoriaResponse> para tornar as expectativas de tipo explícitas.
    @GetMapping("/nome/{nome}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getCategoriaByNome(@PathVariable String nome) {
        return ResponseEntity.ok().body(categoriaService.getCategoriaByNome(nome));
    }

    // 6. deleteCategoriaById: O método atualmente retorna um status OK, o que é funcionalmente correto.
    // Para consistência semântica com práticas REST, considere usar HttpStatus.NO_CONTENT (204) para exclusões sem corpo de resposta.
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> deleteCategoriaById(@PathVariable Long id) {
        categoriaService.deleteCategoriaById(id);
        // return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
/*
 * Observações:
 *  - O controller está bem alinhado com a arquitetura MVC, processando as requisições HTTP e delegando a lógica para o CategoriaService.
 *  - A estrutura atual, com pastas separadas para controllers, dtos e services.interfaces, segue as convenções típicas. No entanto, 
 * considere usar apenas services em vez de services.interfaces para simplicidade e clareza, a menos que haja um motivo específico para
 * separar as interfaces.
 * 
 * Sugestões:
 *  - Considere tornar a dependência categoriaService privada.
 *  - Substituir ResponseEntity<?> por tipos específicos (por exemplo, ResponseEntity<CategoriaResponse>) para melhorar a segurança de tipo
 *  - 
 */