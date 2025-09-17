package com.distribuidoraferreira.backend.services.implementations;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.distribuidoraferreira.backend.dtos.BasicResponse;
import com.distribuidoraferreira.backend.dtos.CaixaDetailsResponse;
import com.distribuidoraferreira.backend.dtos.CaixaRequest;
import com.distribuidoraferreira.backend.dtos.CaixaResponse;
import com.distribuidoraferreira.backend.dtos.GenericResponse;
import com.distribuidoraferreira.backend.enums.StatusCaixa;
import com.distribuidoraferreira.backend.enums.StatusComanda;
import com.distribuidoraferreira.backend.mappers.CaixaMapper;
import com.distribuidoraferreira.backend.models.Caixa;
import com.distribuidoraferreira.backend.models.Comanda;
import com.distribuidoraferreira.backend.models.DetalhesCaixa;
import com.distribuidoraferreira.backend.repositories.CaixaRepository;
import com.distribuidoraferreira.backend.repositories.ComandaRepository;
import com.distribuidoraferreira.backend.services.interfaces.CaixaService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CaixaServiceImpl implements CaixaService {

    private final CaixaRepository caixaRepository;
    private final ComandaRepository comandaRepository;
    private final CaixaMapper caixaMapper;

    @Override
    public GenericResponse<String> abrirCaixa(CaixaRequest caixaRequest) {

        // Verificar se já existe um caixa aberto para evitar duplicação de caixas abertos
        if (isCaixaAberto().getEntity()) {
            return new BasicResponse<>("Já existe um caixa aberto!", 400);
        }

        // Mapeia o objeto CaixaRequest para o modelo Caixa
        Caixa caixa = caixaMapper.caixaRequestToCaixa(caixaRequest);
        caixa.setStatus(StatusCaixa.ABERTO);

        // Salva o caixa no repositório
        caixaRepository.save(caixa);

        return new BasicResponse<>("Caixa criado e aberto com sucesso", 201);
    }

    @Override
    public GenericResponse<String> fecharCaixa(Long id) {
        Optional<Caixa> caixa = caixaRepository.findById(id);

        // Validar se existem comandas abertas associadas ao caixa antes de fechá-lo
        List<Comanda> comandas = comandaRepository.findComandaByIdCaixa(id);
        boolean existsComandaAberta = comandas.stream()
                .anyMatch(comanda -> comanda.getStatus() == StatusComanda.ABERTA);

        if (existsComandaAberta) {
            return new BasicResponse<>("Existem comandas abertas para este caixa", 400);
        }

        // Verifica se o caixa existe no repositório
        if (caixa.isEmpty()) {
            return new BasicResponse<>("Caixa inexistente", 404);
        }

        // Verifica se o caixa já está fechado para evitar fechamentos duplicados
        if (caixa.get().getStatus() == StatusCaixa.FECHADO) {
            return new BasicResponse<>("Caixa ja esta fechado", 400);
        }

        // Atualiza o status do caixa para fechado
        caixa.get().setStatus(StatusCaixa.FECHADO);

        // Salva o caixa atualizado no repositório
        caixaRepository.save(caixa.get());

        return new BasicResponse<>("Caixa fechado com sucesso", 200);
    }

    @Override
    public GenericResponse<CaixaResponse> getCaixaById(Long id) {
        Optional<Caixa> caixa = caixaRepository.findById(id);

        // Verifica se o caixa existe antes de tentar mapeá-lo para uma resposta
        if (caixa.isEmpty()) {
            return new BasicResponse<>(null, 404);
        }

        // Mapeia o objeto Caixa para CaixaResponse
        CaixaResponse response = caixaMapper.caixaToCaixaResponse(caixa.get());
        return new BasicResponse<>(response, 200);
    }

    @Override
    public GenericResponse<java.util.List<CaixaResponse>> getCaixas() {
        Optional<java.util.List<Caixa>> caixas = caixaRepository.getAllCaixasDesc();

        // Verifica se a lista de caixas está vazia antes de processá-la
        if (caixas.isEmpty()) {
            return new BasicResponse<>(null, 404);
        }

        // Calcula o valor total para cada caixa na lista
        for (Caixa caixa : caixas.get()) {
            caixa.setValorTotal(caixa.getFaturamentoDia() + caixa.getValorInicial());
        }

        // Mapeia a lista de objetos Caixa para uma lista de CaixaResponse
        List<CaixaResponse> response = caixaMapper.caixasToCaixasResponses(caixas.get());
        return new BasicResponse<>(response, 200);
    }

    @Override
    public GenericResponse<String> updateCaixa(Long id, CaixaRequest caixaRequest) {
        Optional<Caixa> caixa = caixaRepository.findById(id);

        // Valida se o caixa existe antes de tentar atualizá-lo
        if (caixa.isEmpty()) {
            return new BasicResponse<>("O caixa em questao nao existe", 404);
        }

        // Mapeia o objeto CaixaRequest para o modelo Caixa e atualiza o ID
        Caixa updatedCaixa = caixaMapper.caixaRequestToCaixa(caixaRequest);
        updatedCaixa.setId(id);

        // Salva o caixa atualizado no repositório
        caixaRepository.save(updatedCaixa);

        return new BasicResponse<>("Caixa atualizado com sucesso", 200);
    }

    @Override
    public GenericResponse<CaixaResponse> getCaixaByDataHora(Date dataHora) {
        Optional<Caixa> caixa = caixaRepository.findCaixaByStatusAberto();

        // Valida se há um caixa aberto antes de processá-lo
        if (caixa.isEmpty()) {
            return new BasicResponse<>(null, 404);
        }

        // Calcula o valor total do caixa e mapeia para a resposta
        caixa.get().setValorTotal(caixa.get().getFaturamentoDia() + caixa.get().getValorInicial());
        CaixaResponse response = caixaMapper.caixaToCaixaResponse(caixa.get());
        return new BasicResponse<>(response, 200);
    }

    @Override
    public GenericResponse<Boolean> isCaixaAberto() {
        Optional<Caixa> caixa = caixaRepository.findCaixaByStatusAberto();

        // Retorna verdadeiro se há um caixa com status ABERTO
        boolean response = (caixa.isPresent() && caixa.get().getStatus() == StatusCaixa.ABERTO);
        return new BasicResponse<>(response, 200);
    }

    @Override
    public GenericResponse<CaixaDetailsResponse> getDetalhesCaixa(Long id) {
        Optional<Caixa> caixa = caixaRepository.findById(id);

        // Valida a existência do caixa antes de obter os detalhes
        if (caixa.isEmpty()) {
            return new BasicResponse<>(null, 404);
        }

        // Calcula o total de vendas do caixa detalhadamente
        DetalhesCaixa detalhesCaixa = new DetalhesCaixa();
        caixa.get().getVendas().stream().forEach(action -> {
            detalhesCaixa.setDebito(detalhesCaixa.getDebito() +
                    action.getTotalPagoDebito());
            detalhesCaixa.setCredito(detalhesCaixa.getCredito() +
                    action.getTotalPagoCredito());
            detalhesCaixa.setPix(detalhesCaixa.getPix() + action.getTotalPagoPix());
            detalhesCaixa.setDinheiro(detalhesCaixa.getDinheiro() +
                    action.getTotalPagoDinheiro());
        });

        CaixaDetailsResponse response = caixaMapper.caixaToCaixaDetailsResponse(detalhesCaixa);

        return new BasicResponse<>(response, 200);
    }

    @Override
    public GenericResponse<CaixaResponse> getCaixa() {
        Optional<Caixa> caixa = caixaRepository.findCaixaByStatusAberto();

        // Verifica se o caixa está aberto antes de calculá-lo
        if (caixa.isEmpty()) {
            return new BasicResponse<>(null, 404);
        }
        caixa.get().setValorTotal(caixa.get().getFaturamentoDia() + caixa.get().getValorInicial());
        CaixaResponse response = caixaMapper.caixaToCaixaResponse(caixa.get());
        return new BasicResponse<>(response, 200);
    }

    @Override
    public GenericResponse<Long> getCaixaByID() {
        Optional<Caixa> caixa = caixaRepository.findCaixaByStatusAberto();

        boolean response = (caixa.isPresent() && caixa.get().getStatus() == StatusCaixa.ABERTO) || false;
        if (response) {
            long idResponse = caixa.get().getId();
            return new BasicResponse<>(idResponse, 200);
        }
        return new BasicResponse<>(null, 404);
    }
}
