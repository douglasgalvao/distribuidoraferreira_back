package com.distribuidoraferreira.backend.mapper;

import com.distribuidoraferreira.backend.dto.request.ComandaClienteRequest;
import com.distribuidoraferreira.backend.dto.response.ComandaClienteResponse;
import com.distribuidoraferreira.backend.models.ComandaCliente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.math.BigDecimal;
import java.util.List;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ComandaClienteMapper {

    @Mapping(target = "comandaId", source = "comanda.id")
    @Mapping(target = "comandaCodigo", source = "comanda.codigo")
    @Mapping(target = "clienteId", source = "cliente.id")
    @Mapping(target = "clienteNome", source = "cliente.nome")
    @Mapping(target = "clienteTelefone", source = "cliente.telefone")
    @Mapping(target = "totalConsumido", expression = "java(calcularTotalConsumido(comandaCliente))")
    ComandaClienteResponse toResponse(ComandaCliente comandaCliente);

    List<ComandaClienteResponse> toResponseList(List<ComandaCliente> comandasClientes);

    default BigDecimal calcularTotalConsumido(ComandaCliente comandaCliente) {
        if (comandaCliente.getVendas() == null || comandaCliente.getVendas().isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        return comandaCliente.getVendas().stream()
            .map(venda -> venda.getValorTotal() != null ? venda.getValorTotal() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
