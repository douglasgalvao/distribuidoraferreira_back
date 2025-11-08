package com.distribuidoraferreira.backend.mapper;

import com.distribuidoraferreira.backend.dto.request.MesaRequest;
import com.distribuidoraferreira.backend.dto.response.MesaResponse;
import com.distribuidoraferreira.backend.models.Mesa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
    componentModel = "spring",
    uses = {ComandaMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface MesaMapper {

    @Mapping(target = "comandaAtual", expression = "java(getComandaAtual(mesa))")
    MesaResponse toResponse(Mesa mesa);

    List<MesaResponse> toResponseList(List<Mesa> mesas);

    Mesa toEntity(MesaRequest request);

    void updateEntityFromRequest(MesaRequest request, @MappingTarget Mesa mesa);

    default com.distribuidoraferreira.backend.dto.response.ComandaResponse getComandaAtual(Mesa mesa) {
        if (mesa.getComandas() == null || mesa.getComandas().isEmpty()) {
            return null;
        }
        
        // Retorna a primeira comanda ABERTA
        return mesa.getComandas().stream()
            .filter(c -> c.getStatus() == com.distribuidoraferreira.backend.enums.StatusComanda.ABERTA)
            .findFirst()
            .map(c -> {
                com.distribuidoraferreira.backend.dto.response.ComandaResponse response = 
                    new com.distribuidoraferreira.backend.dto.response.ComandaResponse();
                response.setId(c.getId());
                response.setCodigo(c.getCodigo());
                response.setTipo(c.getTipo());
                response.setStatus(c.getStatus());
                return response;
            })
            .orElse(null);
    }
}
