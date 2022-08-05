package com.rest.login.controllers;

import com.rest.login.dto.AnswerDto;
import com.rest.login.dto.BoardDto;
import com.rest.login.dto.EvaluationDTO;
import com.rest.login.models.Answer;
import com.rest.login.models.Board;
import com.rest.login.models.Client;
import com.rest.login.models.Evaluation;
import com.rest.login.payload.request.AddEvaluationRequest;
import com.rest.login.payload.request.UpdateAnswerRequest;
import com.rest.login.payload.response.MessageResponse;
import com.rest.login.repository.BoardRepository;
import com.rest.login.security.services.AnswerService;
import com.rest.login.security.services.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    AnswerService answerService;

    @GetMapping("/users/{userId}/clients/{clientId}/evaluations/{evalId}/boards/{boardId}/answers")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasUserId(authentication,#userId)")
    public ResponseEntity<MessageResponse> retrieveAnswersByBoardId(@PathVariable Long userId, @PathVariable Long clientId, @PathVariable Long evalId, @PathVariable Long boardId) {
        List<AnswerDto> answerDtos = answerService.getAllAnswersDTOsFromBoard(userId, clientId, evalId, boardId);

        return ResponseEntity.ok(createMessageResponseWithAnswerDTOs(answerDtos));
    }

    @PostMapping("/users/{userId}/clients/{clientId}/evaluations/{evalId}/boards/{boardId}/answers")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasUserId(authentication,#userId)")
    public ResponseEntity<MessageResponse> createAnswer(@PathVariable Long userId, @PathVariable Long clientId, @PathVariable Long evalId, @PathVariable Long boardId) {
        AnswerDto answerDto = answerService.createAnswer(userId, clientId, evalId, boardId);

        return ResponseEntity.ok(new MessageResponse(ANSWER_CREATED.getMessage(), answerDto));
    }

    @PutMapping("/users/{userId}/clients/{clientId}/evaluations/{evalId}/boards/{boardId}/answers/{answerId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasUserId(authentication,#userId)")
    public ResponseEntity<MessageResponse> editAnswer(
            @PathVariable Long userId,
            @PathVariable Long clientId,
            @PathVariable Long evalId,
            @PathVariable Long boardId,
            @PathVariable Long answerId,
            @Valid @RequestBody UpdateAnswerRequest updateAnswerRequest) {
        AnswerDto answerDto = answerService.editAnswer(userId, clientId, evalId, boardId, answerId, updateAnswerRequest.getText());

        return ResponseEntity.ok(new MessageResponse(ANSWER_UPDATED.getMessage(), answerDto));
    }

    @DeleteMapping("/users/{userId}/clients/{clientId}/evaluations/{evalId}/boards/{boardId}/answers/{answerId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasUserId(authentication,#userId)")
    public ResponseEntity<MessageResponse> deleteAnswer(
            @PathVariable Long userId,
            @PathVariable Long clientId,
            @PathVariable Long evalId,
            @PathVariable Long boardId,
            @PathVariable Long answerId) {
        answerService.deleteAnswer(userId, clientId, evalId, boardId, answerId);

        return ResponseEntity.ok(new MessageResponse(ANSWER_DELETED.getMessage()));
    }


}