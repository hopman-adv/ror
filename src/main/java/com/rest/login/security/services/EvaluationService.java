package com.rest.login.security.services;

import com.rest.login.dto.ClientDTO;
import com.rest.login.dto.EvaluationDTO;
import com.rest.login.models.Client;
import com.rest.login.models.EStatus;
import com.rest.login.models.Evaluation;
import com.rest.login.payload.request.AddEvaluationRequest;
import com.rest.login.repository.EvaluationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class EvaluationService {

    @Autowired
    EvaluationRepository evaluationRepository;

    public List<EvaluationDTO> getAllEvaluationsDTO() {
        return evaluationRepository.findAll()
                .stream()
                .map(EvaluationDTO::new)
                .collect(Collectors.toList());
    }

    public EvaluationDTO getEvaluationDTOByClientIdAndEvalId(Long clientId, Long evaluationId) throws NoSuchElementException {
        List<EvaluationDTO> list = getAllEvaluationsDTO();
        return list.stream().filter(evaluation -> Objects.equals(evaluation.getClientId(), clientId) && Objects.equals(evaluation.getId(), evaluationId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Error: Evaluation not found!"));
    }

    public Evaluation createBasicEvaluation(Client client) {
        Evaluation evaluation = new Evaluation();
        evaluation.setClient(client);
        evaluation.setEvaluationStatus(EStatus.NEW);
        return evaluation;
    }

    public List<EvaluationDTO> getAllClientEvaluations(Long clientId) {
        return getAllEvaluationsDTO().stream()
                .filter(evaluationDTO -> Objects.equals(evaluationDTO.getClientId(), clientId))
                .collect(Collectors.toList());
    }

}
