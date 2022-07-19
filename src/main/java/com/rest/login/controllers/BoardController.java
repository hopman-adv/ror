package com.rest.login.controllers;

import com.rest.login.dto.AnswerDto;
import com.rest.login.models.Answer;
import com.rest.login.models.Board;
import com.rest.login.payload.response.MessageResponse;
import com.rest.login.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.rest.login.payload.response.MessageResponse.createMessageResponseWithAnswerDTOs;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/data")
public class BoardController {

    @Autowired
    BoardRepository boardRepository;

    //TODO: controller pro getAllBoards pomocí evaluation id
}