package com.rest.login.controllers;

import com.rest.login.dto.EvaluationDTO;
import com.rest.login.models.Board;
import com.rest.login.models.Client;
import com.rest.login.models.Evaluation;
import com.rest.login.payload.request.AddEvaluationRequest;
import com.rest.login.payload.response.MessageResponse;
import com.rest.login.repository.ClientRepository;
import com.rest.login.repository.EvaluationRepository;
import com.rest.login.repository.UserRepository;
import com.rest.login.security.services.ClientService;
import com.rest.login.security.services.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Entity;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;

import static com.rest.login.enums.EResponses.*;
import static com.rest.login.payload.response.MessageResponse.createMessageResponseWithEvaluationDTOs;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/data")
public class EvaluationController {

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ClientService clientService;

    @Autowired
    EvaluationRepository evaluationRepository;

    @Autowired
    EvaluationService evaluationService;

    @GetMapping("/evaluations")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> retriveAllEvaluations() {
        return ResponseEntity.ok().body(createMessageResponseWithEvaluationDTOs(evaluationService.getAllEvaluationsDTO()));
    }

    @GetMapping("/evaluations/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> retrieveAnyEvaluationById(@PathVariable Long id) {
        return ResponseEntity.ok().body(new MessageResponse(EVALUATION_FOUND.getMessage(), evaluationService.getEvaluationById(id)));
    }

    //Zde jsme
    @GetMapping("/users/{userId}/clients/{clientId}/evaluations")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasUserId(authentication,#userId)")
    public ResponseEntity<MessageResponse> retrieveAllEvaluationsByClientId(@PathVariable Long userId, @PathVariable Long clientId) {
        Client client;
        try {
            client = clientService.getClientById(clientId, userId);
        } catch (EntityNotFoundException | NoSuchElementException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(CLIENT_NOT_FOUND.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse(UNAUTHORIZED_ACCESS.getMessage()));
        }
        List<EvaluationDTO> evaluations = evaluationService.getAllClientsEvaluationDTOs(client);
        if (evaluations.isEmpty()) {
            return ResponseEntity.ok(new MessageResponse(NO_EVALUATIONS_FOR_CLIENT.getMessage()));
        }
        return ResponseEntity.ok().body(createMessageResponseWithEvaluationDTOs(evaluations));
    }

    @GetMapping("/users/{userId}/clients/{clientId}/evaluations/{evalId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasUserId(authentication,#userId)")
    public EvaluationDTO retrieveEvaluationByClientId(@PathVariable Long userId, @PathVariable Long clientId, @PathVariable Long evalId) {
        return evaluationService.getEvaluationDTOByClientIdAndEvalId(clientId, evalId);
    }

    @PostMapping(path = "/users/{id}/clients/{clientId}/evaluations", consumes = "application/json")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasUserId(authentication,#id)")
    public ResponseEntity<MessageResponse> createEvaluation(@Valid @RequestBody AddEvaluationRequest addEvaluationRequest, @PathVariable Long id, @PathVariable Long clientId) {
        Client client;
        try {
            client = clientService.getClientById(clientId, id);
        } catch (EntityNotFoundException | NoSuchElementException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(CLIENT_NOT_FOUND.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(UNAUTHORIZED_ACCESS.getMessage()));
        }

        EvaluationDTO evaluationDTO = new EvaluationDTO(evaluationService.createEvaluation(client, id, addEvaluationRequest));
        return ResponseEntity.ok(new MessageResponse(EVALUATION_ADDED.getMessage(), evaluationDTO));
    }

    @PostMapping(path = "/users/{id}/clients/{clientId}/evaluations")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasUserId(authentication,#id)")
    public ResponseEntity<MessageResponse> createEvaluation(@PathVariable Long id, @PathVariable Long clientId) {
        Client client;
        try {
            client = clientService.getClientById(clientId, id);
        } catch (EntityNotFoundException | NoSuchElementException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(CLIENT_NOT_FOUND.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(UNAUTHORIZED_ACCESS.getMessage()));
        }

        EvaluationDTO evaluationDTO = new EvaluationDTO(evaluationService.createEvaluation(client, id, null));
        return ResponseEntity.ok(new MessageResponse(EVALUATION_ADDED.getMessage(), evaluationDTO));
    }
}