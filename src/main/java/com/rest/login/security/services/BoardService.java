package com.rest.login.security.services;

import com.rest.login.dto.ClientDTO;
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

import static com.rest.login.models.Client.*;

@Service
public class BoardService {

	@Autowired
	private BoardRepository boardRepository;

	public List<Board> getAllBoardsByEvaluationId(Long evalId) {
		List<Board> boards = boardRepository.findByEvaluation_id(evalId);

		return boards;
	}

	public Board getBoardById(Long boardId) {
		 return boardRepository.findById(boardId).get();
	}

}
