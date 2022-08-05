package com.rest.login.security.services;

import com.rest.login.dto.BoardDto;
import com.rest.login.dto.ClientDTO;
import com.rest.login.dto.EvaluationDTO;
import com.rest.login.models.Board;
import com.rest.login.models.Client;
import com.rest.login.models.User;
import com.rest.login.payload.request.AddClientRequest;
import com.rest.login.repository.BoardRepository;
import com.rest.login.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.rest.login.enums.EResponses.*;
import static com.rest.login.models.Client.*;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    EvaluationService evaluationService;

    private List<Board> getAllBoardsByEvaluationId(Long evalId) throws NoSuchElementException {
        List<Board> boards = boardRepository.findByEvaluation_id(evalId);
        if (boards.size() <= 0) {
            throw new NoSuchElementException(BOARD_NOT_FOUND.getMessage());
        }
        return boards;
    }

    public List<BoardDto> getAllBoardsDTOs(Long userId, Long clientId, Long evalId) {
        Long authorizedEvaluationId = evaluationService.getEvaluationDTOByClientIdAndEvalId(userId, clientId, evalId).getId();

        return getAllBoardsByEvaluationId(authorizedEvaluationId).stream()
                .map(this::boardToDTO)
                .collect(Collectors.toList());
    }

    private BoardDto boardToDTO(Board board) {
        return new BoardDto(board);
    }

    public Board getBoardById(Long userId, Long clientId, Long evalId, Long boardId) {
        Long authorizedEvalId = evaluationService.getEvaluationDTOByClientIdAndEvalId(userId, clientId, evalId).getId();

        return getAllBoardsByEvaluationId(authorizedEvalId).stream()
                .filter(board -> Objects.equals(boardId, board.getId()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(BOARD_NOT_FOUND.getMessage()));
    }

    public Long getAuthorizedBoardId(Long userId, Long clientId, Long evalId, Long boardId) {
        return getBoardById(userId, clientId, evalId, boardId).getId();
    }
}
