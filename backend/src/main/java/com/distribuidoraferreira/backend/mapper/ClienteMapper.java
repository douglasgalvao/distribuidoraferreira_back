package com.distribuidoraferreira.backend.mapper;

import com.distribuidoraferreira.backend.dto.request.ClienteRequest;
import com.distribuidoraferreira.backend.dto.response.ClienteResponse;
import com.distribuidoraferreira.backend.models.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ClienteMapper {

    ClienteResponse toResponse(Cliente cliente);

    List<ClienteResponse> toResponseList(List<Cliente> clientes);

    Cliente toEntity(ClienteRequest request);

    void updateEntityFromRequest(ClienteRequest request, @MappingTarget Cliente cliente);
}
