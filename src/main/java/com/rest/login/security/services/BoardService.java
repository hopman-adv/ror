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

	public Board getBoardById(Long boardId) {
		Board board;
		try {
			board = boardRepository.findById(boardId).get();
		} catch (EntityNotFoundException | NoSuchElementException e) {
			return null;
		}
		return board;
	}

}
