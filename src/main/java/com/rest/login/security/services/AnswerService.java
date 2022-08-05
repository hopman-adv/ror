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
        if(answers.size() <= 0) {
            throw new NoSuchElementException(ANSWER_NOT_FOUND.getMessage());
        }
        return answers;
    }

    public List<AnswerDto> getAllAnswersDTOsFromBoard(Long userId, Long clientId, Long evalId, Long boardId) {
        Long authorizedBoardId = boardService.getBoardById(userId, clientId, evalId, boardId).getId();

        return getAllAnswersByBoardId(authorizedBoardId).stream()
                .map(this::answerToAnswerDTO)
                .collect(Collectors.toList());
    }

    private AnswerDto answerToAnswerDTO(Answer answer) {
        return new AnswerDto(answer);
    }
}
