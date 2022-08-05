package com.rest.login.security.services;

import com.rest.login.dto.AnswerDto;
import com.rest.login.dto.EvaluationDTO;
import com.rest.login.models.Answer;
import com.rest.login.models.Board;
import com.rest.login.payload.response.MessageResponse;
import com.rest.login.repository.AnswerRepository;
import com.rest.login.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.rest.login.enums.EResponses.*;
import static com.rest.login.payload.response.MessageResponse.createMessageResponseWithAnswerDTOs;

@Service
public class AnswerService {

    @Autowired
    private BoardService boardService;

    @Autowired
    ClientService clientService;

    @Autowired
    EvaluationService evaluationService;

    @Autowired
    AnswerRepository answerRepository;

    private List<Answer> getAllAnswersByBoardId(Long boardId) {
        List<Answer> answers = answerRepository.findByBoard_id(boardId);
        if (answers.size() <= 0) {
            throw new NoSuchElementException(ANSWER_NOT_FOUND.getMessage());
        }
        return answers;
    }

    public List<AnswerDto> getAllAnswersDTOsFromBoard(Long userId, Long clientId, Long evalId, Long boardId) {
        Long authorizedBoardId = boardService.getAuthorizedBoardId(userId, clientId, evalId, boardId);

        return getAllAnswersByBoardId(authorizedBoardId).stream()
                .map(this::answerToAnswerDTO)
                .collect(Collectors.toList());
    }

    public AnswerDto createAnswer(Long userId, Long clientId, Long evalId, Long boardId) {
        Board board = boardService.getBoardById(userId, clientId, evalId, boardId);
        Answer answer = new Answer();
        answer.setBoard(board);

        return new AnswerDto(answerRepository.save(answer));
    }

    public AnswerDto editAnswer(Long userId, Long clientId, Long evalId, Long boardId, Long answerId, String text) {
        Answer answer = getAnswerById(userId, clientId, evalId, boardId, answerId);
        answer.setAnswer_text(text);

        return new AnswerDto(answerRepository.save(answer));
    }

    public void deleteAnswer(Long userId, Long clientId, Long evalId, Long boardId, Long answerId) {
        Answer answer = getAnswerById(userId, clientId, evalId, boardId, answerId);

        answerRepository.delete(answer);
    }

    private AnswerDto answerToAnswerDTO(Answer answer) {
        return new AnswerDto(answer);
    }

    private Answer getAnswerById(Long userId, Long clientId, Long evalId, Long boardId, Long answerId) {
        Long authorizedBoardId = boardService.getAuthorizedBoardId(userId, clientId, evalId, boardId);

        return getAllAnswersByBoardId(authorizedBoardId).stream()
                .filter(answer -> Objects.equals(answer.getId(), answerId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(ANSWER_NOT_FOUND.getMessage()));
    }
}