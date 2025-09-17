package com.distribuidoraferreira.backend.services.implementations;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import com.distribuidoraferreira.backend.dtos.CategoriaRequest;
import com.distribuidoraferreira.backend.dtos.CategoriaResponse;
import com.distribuidoraferreira.backend.dtos.GenericResponse;
import com.distribuidoraferreira.backend.dtos.BasicResponse;
import com.distribuidoraferreira.backend.mappers.CategoriaMapper;
import com.distribuidoraferreira.backend.models.Categoria;
import com.distribuidoraferreira.backend.repositories.CategoriaRepository;
import com.distribuidoraferreira.backend.services.interfaces.CategoriaService;

import jakarta.transaction.Transactional;

@Service
@AllArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    @Override
    public void addCategoria(CategoriaRequest categoriaRequest) {
        // Mapeia o objeto CategoriaRequest para o modelo Categoria
        Categoria categoria = categoriaMapper.categoriaRequestToCategoria(categoriaRequest);
        
        // Define o nome da categoria em letras maiúsculas para padronização
        categoria.setNome(categoria.getNome().toUpperCase());
        
        // Salva a nova categoria no repositório
        categoriaRepository.save(categoria);
    }

    @Override
    @Transactional
    public void deleteCategoriaById(Long id) {
        // Remove a categoria por ID. O uso de @Transactional assegura que a transação
        // seja concluída com sucesso ou revertida em caso de erro.
        categoriaRepository.deleteCategoriaById(id);
    }

    @Override
    public GenericResponse<CategoriaResponse> getCategoriaById(Long id) {
        Optional<Categoria> categoria = categoriaRepository.findById(id);
        
        // Verifica se a categoria existe antes de processar o mapeamento
        if (categoria.isEmpty()) {
            return new BasicResponse<>(null, 404);
        }

        // Mapeia o objeto Categoria para CategoriaResponse
        CategoriaResponse response = categoriaMapper.categoriaToCategoriaResponse(categoria.get());

        // Suprime warnings de tipos genéricos com anotações
        @SuppressWarnings({ "rawtypes", "unchecked" })
        GenericResponse<CategoriaResponse> genericResponse = new BasicResponse(response, 200);

        return genericResponse;
    }

    @Override
    public GenericResponse<List<CategoriaResponse>> getCategorias() {
        // Busca todas as categorias do repositório
        List<Categoria> categorias = categoriaRepository.findAll();

        // Mapeia a lista de objetos Categoria para uma lista de CategoriaResponse
        List<CategoriaResponse> categoriasResponse = categoriaMapper.categoriasToCategoriaResponses(categorias);

        return new BasicResponse<List<CategoriaResponse>>(categoriasResponse, 200);
    }

    @Override
    public GenericResponse<CategoriaResponse> getCategoriaByNome(String nome) {
        // Busca a categoria pelo nome, convertendo-o para letras maiúsculas
        Optional<Categoria> categoria = categoriaRepository.findCategoriaByNome(nome.toUpperCase());
        
        // Verifica se a categoria existe antes de processar o mapeamento
        if (categoria.isEmpty()) {
            return new BasicResponse<>(null, 404);
        }
        
        CategoriaResponse response = categoriaMapper.categoriaToCategoriaResponse(categoria.get());
        
        // Suprime warnings de tipos genéricos com anotações
        @SuppressWarnings({ "rawtypes", "unchecked" })
        GenericResponse<CategoriaResponse> genericResponse = new BasicResponse(response, 200);
        
        return genericResponse;
    }

}
/*
 - Verificações de Existência: Em getCategoriaById e getCategoriaByNome, foram adicionados comentários para destacar a verificação de Optional,
   que é necessária para evitar exceções ao acessar elementos não presentes.
 - Padronização de Nome: No método addCategoria, o nome da categoria é definido como maiúsculo para padronização, com um comentário 
   explicativo.
 - Anotações @SuppressWarnings: Foram adicionados comentários sobre o uso de @SuppressWarnings em alguns métodos, explicando 
que a anotação reduz os warnings de tipos genéricos.
 */