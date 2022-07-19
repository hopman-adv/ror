package com.rest.login.security.services;

import com.rest.login.dto.EvaluationDTO;
import com.rest.login.models.*;
import com.rest.login.payload.request.AddEvaluationRequest;
import com.rest.login.repository.BoardRepository;
import com.rest.login.repository.EvaluationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        List<Board> list = createBoards(evaluation);
        evaluation.setBoards(list);

        return evaluationRepository.save(evaluation);
    }

    public Evaluation createBasicEvaluationWithDescription(Client client, AddEvaluationRequest addEvaluationRequest) {
        Evaluation evaluation = new Evaluation();
        evaluation.setClient(client);
        evaluation.setDescription_info(addEvaluationRequest.getDescription());
        List<Board> list = createBoards(evaluation);
        evaluation.setBoards(list);

        return evaluationRepository.save(evaluation);
    }


    public List<EvaluationDTO> getAllClientEvaluations(Long clientId) {
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
}
