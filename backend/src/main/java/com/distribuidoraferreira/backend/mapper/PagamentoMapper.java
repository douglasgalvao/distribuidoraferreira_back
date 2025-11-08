package com.distribuidoraferreira.backend.mapper;

import com.distribuidoraferreira.backend.dto.request.PagamentoRequest;
import com.distribuidoraferreira.backend.dto.response.PagamentoResponse;
import com.distribuidoraferreira.backend.models.Pagamento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PagamentoMapper {

    @Mapping(target = "contaClienteId", source = "contaCliente.id")
    @Mapping(target = "nomeCliente", source = "contaCliente.cliente.nome")
    @Mapping(target = "vendaId", source = "venda.id")
    PagamentoResponse toResponse(Pagamento pagamento);

    List<PagamentoResponse> toResponseList(List<Pagamento> pagamentos);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataPagamento", ignore = true)
    @Mapping(target = "contaCliente", ignore = true)
    @Mapping(target = "venda", ignore = true)
    Pagamento toEntity(PagamentoRequest request);
}
