package com.rest.login.controllers;

import com.rest.login.dto.EvaluationDTO;
import com.rest.login.models.Client;
import com.rest.login.payload.request.AddEvaluationRequest;
import com.rest.login.payload.response.MessageResponse;
import com.rest.login.repository.ClientRepository;
import com.rest.login.repository.EvaluationRepository;
import com.rest.login.repository.UserRepository;
import com.rest.login.security.services.ClientService;
import com.rest.login.security.services.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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

    @GetMapping("/users/{userId}/clients/{clientId}/evaluations")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasUserId(authentication,#userId)")
    public ResponseEntity<MessageResponse> retrieveAllEvaluationsByClientId(@PathVariable Long userId, @PathVariable Long clientId) {
        Client client = clientService.getClientById(clientId, userId);
        List<EvaluationDTO> evaluations = evaluationService.getAllClientsEvaluationDTOs(client);
        if (evaluations.isEmpty()) {
            return ResponseEntity.ok(new MessageResponse(NO_EVALUATIONS_FOR_CLIENT.getMessage()));
        }
        return ResponseEntity.ok().body(createMessageResponseWithEvaluationDTOs(evaluations));
    }

    @GetMapping("/users/{userId}/clients/{clientId}/evaluations/{evalId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasUserId(authentication,#userId)")
    public ResponseEntity<MessageResponse> retrieveEvaluationByClientId(@PathVariable Long userId, @PathVariable Long clientId, @PathVariable Long evalId) {
        EvaluationDTO evaluationDTO = evaluationService.getEvaluationDTOByClientIdAndEvalId(userId, clientId, evalId);

        return ResponseEntity.ok(new MessageResponse(EVALUATION_FOUND.getMessage(), evaluationDTO));
    }

    @PostMapping(path = "/users/{id}/clients/{clientId}/evaluations", consumes = "application/json")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasUserId(authentication,#id)")
    public ResponseEntity<MessageResponse> createEvaluation(@Valid @RequestBody AddEvaluationRequest addEvaluationRequest, @PathVariable Long id, @PathVariable Long clientId) {
        Client client = clientService.getClientById(clientId, id);

        EvaluationDTO evaluationDTO = new EvaluationDTO(evaluationService.createEvaluation(client, addEvaluationRequest));
        return ResponseEntity.ok(new MessageResponse(EVALUATION_ADDED.getMessage(), evaluationDTO));
    }

    @PostMapping(path = "/users/{id}/clients/{clientId}/evaluations")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasUserId(authentication,#id)")
    public ResponseEntity<MessageResponse> createEvaluation(@PathVariable Long id, @PathVariable Long clientId) {
        Client client = clientService.getClientById(clientId, id);

        EvaluationDTO evaluationDTO = new EvaluationDTO(evaluationService.createEvaluation(client, null));
        return ResponseEntity.ok(new MessageResponse(EVALUATION_ADDED.getMessage(), evaluationDTO));
    }

    @PutMapping("/users/{userId}/clients/{clientId}/evaluations/{evalId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasUserId(authentication,#userId)")
    public ResponseEntity<MessageResponse> updateEvaluation(
            @PathVariable Long userId, @PathVariable Long clientId, @PathVariable Long evalId, @Valid @RequestBody AddEvaluationRequest addEvaluationRequest) {
        Client client = clientService.getClientById(clientId, userId);

        EvaluationDTO evaluationDTO = evaluationService.editEvaluation(client, evalId, addEvaluationRequest);
        return ResponseEntity.ok(new MessageResponse(EVALUATION_UPDATED.getMessage(), evaluationDTO));

    }

    @DeleteMapping("/users/{userId}/clients/{clientId}/evaluations/{evalId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasUserId(authentication,#userId)")
    public ResponseEntity<MessageResponse> deleteEvaluation(
            @PathVariable Long userId, @PathVariable Long clientId, @PathVariable Long evalId) {
        Client client = clientService.getClientById(clientId, userId);
        evaluationService.deleteEvaluation(client, evalId);

        return ResponseEntity.ok(new MessageResponse(EVALUATION_DELETED.getMessage()));
    }
}