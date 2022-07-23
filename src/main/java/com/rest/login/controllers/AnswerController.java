package com.rest.login.controllers;

import com.rest.login.dto.AnswerDto;
import com.rest.login.dto.BoardDto;
import com.rest.login.dto.EvaluationDTO;
import com.rest.login.models.Answer;
import com.rest.login.models.Board;
import com.rest.login.models.Client;
import com.rest.login.models.Evaluation;
import com.rest.login.payload.request.AddEvaluationRequest;
import com.rest.login.payload.response.MessageResponse;
import com.rest.login.repository.BoardRepository;
import com.rest.login.security.services.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.rest.login.enums.EResponses.*;
import static com.rest.login.payload.response.MessageResponse.createMessageResponseWithAnswerDTOs;
import static com.rest.login.payload.response.MessageResponse.createMessageResponseWithEvaluationDTOs;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/data")
public class AnswerController {

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    BoardService boardService;

    @GetMapping("/users/{userId}/boards/{boardId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasUserId(authentication,#userId)")
    public ResponseEntity<MessageResponse> retrieveAnswersByBoardId(@PathVariable Long userId, @PathVariable Long boardId) {
        Board board = null;
        try {
            board = boardService.getBoardById(boardId);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(BOARD_NOT_FOUND.getMessage()));
        }
        if (!Objects.equals(board.getEvaluation().getClient().getUser().getId(), userId)) {
            return ResponseEntity.badRequest().body(new MessageResponse(CLIENT_OWNED_BY_DIFFERENT_USER.getMessage()));
        }
        List<Answer> answers = board.getAnswers();
        List<AnswerDto> listDtos = answers.stream()
                .map(answer -> new AnswerDto(answer.getId(), answer.getAnswer_text(), answer.getBoard()))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(createMessageResponseWithAnswerDTOs(LISTING_ANSWERS_FROM_BOARD.getMessage(), listDtos));
    }
}