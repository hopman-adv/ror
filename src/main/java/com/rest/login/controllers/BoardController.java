package com.rest.login.controllers;

import com.rest.login.dto.AnswerDto;
import com.rest.login.dto.BoardDto;
import com.rest.login.dto.EvaluationDTO;
import com.rest.login.models.Answer;
import com.rest.login.models.Board;
import com.rest.login.models.Client;
import com.rest.login.models.Evaluation;
import com.rest.login.payload.response.MessageResponse;
import com.rest.login.repository.BoardRepository;
import com.rest.login.repository.EvaluationRepository;
import com.rest.login.security.services.BoardService;
import com.rest.login.security.services.ClientService;
import com.rest.login.security.services.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.rest.login.enums.EResponses.*;
import static com.rest.login.payload.response.MessageResponse.createMessageResponseWithAnswerDTOs;
import static com.rest.login.payload.response.MessageResponse.createMessageResponseWithBoardsList;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/data")
public class BoardController {

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    EvaluationRepository evaluationRepository;

    @Autowired
    BoardService boardService;

    @Autowired
    ClientService clientService;

    @Autowired
    EvaluationService evaluationService;

    @GetMapping("/users/{userId}/clients/{clientId}/evaluations/{evalId}/boards")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasUserId(authentication,#userId)")
    public ResponseEntity<MessageResponse> retrieveBoardsByEvaluationId
            (@PathVariable Long userId, @PathVariable Long clientId, @PathVariable Long evalId) {
        List<BoardDto> boardsDTOs = boardService.getAllBoardsDTOs(userId, clientId, evalId);

        return ResponseEntity.ok(createMessageResponseWithBoardsList(boardsDTOs));
    }
}