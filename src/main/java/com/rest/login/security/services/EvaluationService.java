package com.rest.login.security.services;

import com.rest.login.dto.EvaluationDTO;
import com.rest.login.models.*;
import com.rest.login.payload.request.AddEvaluationRequest;
import com.rest.login.payload.response.MessageResponse;
import com.rest.login.repository.EvaluationRepository;
import com.sun.xml.bind.v2.TODO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
                .map(EvaluationDTO::new)
                .collect(Collectors.toList());
    }

    public EvaluationDTO getEvaluationDTOByClientIdAndEvalId(Long clientId, Long evaluationId) throws NoSuchElementException {
        List<EvaluationDTO> list = getAllEvaluationsDTO();
        return list.stream().filter(evaluation -> Objects.equals(evaluation.getClientId(), clientId) && Objects.equals(evaluation.getId(), evaluationId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Error: Evaluation not found!"));
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

    public ResponseEntity<MessageResponse> createEvaluation(Long clientId, AddEvaluationRequest addEvaluationRequest) {
        Client client;
        try {
            client = clientService.getClientById(clientId);
        } catch (EntityNotFoundException | NoSuchElementException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(CLIENT_NOT_FOUND.getMessage()));
        }
        //TODO: přesunout rozhodování ohledně tvorby evaluation do evaluationService (kod níže)
        Evaluation evaluation;
        if (addEvaluationRequest == null) {
            evaluation = createBasicEvaluation(client);
        } else {
            evaluation = createBasicEvaluationWithDescription(client, addEvaluationRequest);
        }
        return ResponseEntity.ok().body(new MessageResponse(EVALUATION_ADDED.getMessage(), new EvaluationDTO(evaluation)));
    }

    public ResponseEntity<MessageResponse> getAllClientsEvaluations(Client client) {
        //TODO: Doplnit přístup jen pro klienty daného usera. Přidat userID a pracovat s ním.
        if (client == null) {
            return ResponseEntity.badRequest().body(new MessageResponse(CLIENT_NOT_FOUND.getMessage()));
        }
        Long dbClientId = client.getId();
        List<EvaluationDTO> evaluations = getAllClientEvaluations(dbClientId);

        if (evaluations.isEmpty()) {
            return ResponseEntity.ok(new MessageResponse(NO_EVALUATIONS_FOR_CLIENT.getMessage()));
        }
        return ResponseEntity.ok().body(createMessageResponseWithEvaluationDTOs(LISTING_EVALUATIONS.getMessage(), evaluations));
    }
}
