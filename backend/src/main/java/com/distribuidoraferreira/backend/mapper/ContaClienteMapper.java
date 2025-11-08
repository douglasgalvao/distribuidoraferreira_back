package com.distribuidoraferreira.backend.mapper;

import com.distribuidoraferreira.backend.dto.request.ContaClienteRequest;
import com.distribuidoraferreira.backend.dto.response.ContaClienteResponse;
import com.distribuidoraferreira.backend.models.ContaCliente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.math.BigDecimal;
import java.util.List;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ContaClienteMapper {

    @Mapping(target = "clienteId", source = "cliente.id")
    @Mapping(target = "clienteNome", source = "cliente.nome")
    @Mapping(target = "clienteTelefone", source = "cliente.telefone")
    @Mapping(target = "creditoDisponivel", expression = "java(calcularCreditoDisponivel(contaCliente))")
    @Mapping(target = "podeComprar", expression = "java(contaCliente.podeComprar())")
    ContaClienteResponse toResponse(ContaCliente contaCliente);

    List<ContaClienteResponse> toResponseList(List<ContaCliente> contasClientes);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "dataAbertura", ignore = true)
    @Mapping(target = "dataFechamento", ignore = true)
    @Mapping(target = "saldoDevedor", ignore = true)
    @Mapping(target = "totalConsumido", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "pagamentos", ignore = true)
    ContaCliente toEntity(ContaClienteRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "dataAbertura", ignore = true)
    @Mapping(target = "dataFechamento", ignore = true)
    @Mapping(target = "saldoDevedor", ignore = true)
    @Mapping(target = "totalConsumido", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "pagamentos", ignore = true)
    void updateEntityFromRequest(ContaClienteRequest request, @MappingTarget ContaCliente contaCliente);

    default BigDecimal calcularCreditoDisponivel(ContaCliente contaCliente) {
        if (contaCliente.getLimiteCredito() == null) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal saldo = contaCliente.getSaldoDevedor() != null ? 
            contaCliente.getSaldoDevedor() : BigDecimal.ZERO;
        
        return contaCliente.getLimiteCredito().subtract(saldo);
    }
}
