package com.rest.login.security.services;

import com.rest.login.dto.AnswerDto;
import com.rest.login.models.Answer;
import com.rest.login.models.Board;
import com.rest.login.payload.response.MessageResponse;
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

public ResponseEntity<MessageResponse> getAllAnswersFromBoard(Long boardId, Long userId) {
	Board board = null;
	try {
		board = boardService.getBoardById(boardId);
	} catch (EntityNotFoundException | NoSuchElementException e) {
		return ResponseEntity.badRequest().body(new MessageResponse(BOARD_NOT_FOUND.getMessage()));
	}
	//Kontrola přístupu ke klientovi!
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
