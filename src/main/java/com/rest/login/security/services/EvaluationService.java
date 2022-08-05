package com.rest.login.security.services;

import com.rest.login.dto.EvaluationDTO;
import com.rest.login.models.*;
import com.rest.login.payload.request.AddEvaluationRequest;
import com.rest.login.payload.response.MessageResponse;
import com.rest.login.repository.EvaluationRepository;
import com.sun.xml.bind.v2.TODO;
import javafx.scene.web.WebView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rest.login.enums.EResponses.*;
import static com.rest.login.payload.response.MessageResponse.createMessageResponseWithEvaluationDTOs;

@Service
public class EvaluationService {

    @Autowired
    private EvaluationRepository evaluationRepository;

    @Autowired
    private ClientService clientService;

    public List<EvaluationDTO> getAllEvaluationsDTO() {
        return evaluationRepository.findAll()
                .stream()
                .map(this::evaluationToDTO)
                .collect(Collectors.toList());
    }

    public EvaluationDTO getEvaluationDTOByClientIdAndEvalId(Long userId, Long clientId, Long evaluationId) throws NoSuchElementException {
        Long authorizedClientId = clientService.getClientById(clientId, userId).getId();
        List<EvaluationDTO> list = getAllEvaluationsDTO();

        return list.stream().filter(
                        evaluation -> Objects.equals(evaluation.getClientId(), authorizedClientId)
                                && Objects.equals(evaluation.getId(), evaluationId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(EVALUATION_NOT_FOUND.getMessage()));
    }

    private Evaluation createBasicEvaluation(Client client) {
        Evaluation evaluation = new Evaluation();
        evaluation.setClient(client);

        List<Board> list = createBoards(evaluation);
        evaluation.setBoards(list);

        return evaluationRepository.save(evaluation);
    }

    private Evaluation createBasicEvaluationWithDescription(Client client, AddEvaluationRequest addEvaluationRequest) {
        Evaluation evaluation = new Evaluation();
        evaluation.setClient(client);
        evaluation.setDescription_info(addEvaluationRequest.getDescription());
        List<Board> list = createBoards(evaluation);
        evaluation.setBoards(list);

        return evaluationRepository.save(evaluation);
    }


    private List<EvaluationDTO> getAllClientEvaluations(Long clientId) {
        return getAllEvaluationsDTO().stream()
                .filter(evaluationDTO -> Objects.equals(evaluationDTO.getClientId(), clientId))
                .collect(Collectors.toList());
    }

    private List<Board> createBoards(Evaluation evaluation) {
        List<Board> boards = Stream.generate(() -> new Board(evaluation))
                .limit(10)
                .collect(Collectors.toList());

        creatingAnswersForBoardList(boards);

        return boards;
    }

    private List<Board> createBoards() {
        List<Board> boards = Stream.generate(Board::new)
                .limit(10)
                .collect(Collectors.toList());

        creatingAnswersForBoardList(boards);

        return boards;
    }

    private void creatingAnswersForBoardList(List<Board> boards) {
        boards.forEach(board -> {
            List<Answer> ans = new ArrayList<>(List.of(new Answer(board)));
            board.setAnswers(ans);
        });
    }

    public Evaluation createEvaluation(Client client, Long userId, AddEvaluationRequest addEvaluationRequest) {
        Evaluation evaluation;
        if (addEvaluationRequest == null) {
            evaluation = createBasicEvaluation(client);
        } else {
            evaluation = createBasicEvaluationWithDescription(client, addEvaluationRequest);
        }
        return evaluation;
    }

    public List<EvaluationDTO> getAllClientsEvaluationDTOs(Client client) {
        return getAllEvaluationsDTO().stream()
                .filter(evaluationDTO -> Objects.equals(evaluationDTO.getClientId(), client.getId()))
                .collect(Collectors.toList());

    }

    private EvaluationDTO evaluationToDTO(Evaluation evaluation) {
        return new EvaluationDTO(evaluation);
    }

    public EvaluationDTO getEvaluationById(Long id) {
        return evaluationToDTO(evaluationRepository.getById(id));
    }

    public EvaluationDTO editEvaluation(Client client, Long evalId, AddEvaluationRequest addEvaluationRequest)
            throws DataAccessResourceFailureException {
        Evaluation evaluation = evaluationRepository.getById(evalId);

        if (!Objects.equals(evaluation.getClient().getId(), client.getId())) {
            throw new DataAccessResourceFailureException(WRONG_CLIENT_NUMBER.getMessage());
        }
        evaluation.setDescription_info(addEvaluationRequest.getDescription());
        Evaluation savedEvaluation = evaluationRepository.save(evaluation);

        return evaluationToDTO(savedEvaluation);
    }

    public void deleteEvaluation(Client client, Long evalId)
            throws DataAccessResourceFailureException {
        Evaluation evaluation = evaluationRepository.getById(evalId);

        if (!Objects.equals(evaluation.getClient().getId(), client.getId())) {
            throw new DataAccessResourceFailureException(WRONG_CLIENT_NUMBER.getMessage());
        }
        evaluationRepository.deleteById(evaluation.getId());

    }

}
