package com.distribuidoraferreira.backend.mapper;

import com.distribuidoraferreira.backend.dto.request.ComandaRequest;
import com.distribuidoraferreira.backend.dto.response.ComandaResponse;
import com.distribuidoraferreira.backend.models.Comanda;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.math.BigDecimal;
import java.util.List;

@Mapper(
    componentModel = "spring",
    uses = {ComandaClienteMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ComandaMapper {

    @Mapping(target = "mesaId", source = "mesa.id")
    @Mapping(target = "mesaNumero", source = "mesa.numero")
    @Mapping(target = "clientes", source = "comandasClientes")
    @Mapping(target = "totalVendas", expression = "java(calcularTotalVendas(comanda))")
    @Mapping(target = "quantidadeItens", expression = "java(calcularQuantidadeItens(comanda))")
    ComandaResponse toResponse(Comanda comanda);

    List<ComandaResponse> toResponseList(List<Comanda> comandas);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "codigo", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "dataAbertura", ignore = true)
    @Mapping(target = "dataFechamento", ignore = true)
    @Mapping(target = "mesa", ignore = true)
    @Mapping(target = "comandasClientes", ignore = true)
    Comanda toEntity(ComandaRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "codigo", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "dataAbertura", ignore = true)
    @Mapping(target = "dataFechamento", ignore = true)
    @Mapping(target = "mesa", ignore = true)
    @Mapping(target = "comandasClientes", ignore = true)
    void updateEntityFromRequest(ComandaRequest request, @MappingTarget Comanda comanda);

    default BigDecimal calcularTotalVendas(Comanda comanda) {
        if (comanda.getComandasClientes() == null) {
            return BigDecimal.ZERO;
        }
        
        return comanda.getComandasClientes().stream()
            .flatMap(cc -> cc.getVendas() != null ? cc.getVendas().stream() : java.util.stream.Stream.empty())
            .map(venda -> venda.getValorTotal() != null ? venda.getValorTotal() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    default Integer calcularQuantidadeItens(Comanda comanda) {
        if (comanda.getComandasClientes() == null) {
            return 0;
        }
        
        return comanda.getComandasClientes().stream()
            .flatMap(cc -> cc.getVendas() != null ? cc.getVendas().stream() : java.util.stream.Stream.empty())
            .mapToInt(venda -> venda.getItens() != null ? venda.getItens().size() : 0)
            .sum();
    }
}
