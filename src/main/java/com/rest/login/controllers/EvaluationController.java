package com.rest.login.controllers;

import com.rest.login.dto.EvaluationDTO;
import com.rest.login.models.Client;
import com.rest.login.models.EStatus;
import com.rest.login.models.Evaluation;
import com.rest.login.payload.request.AddEvaluationRequest;
import com.rest.login.payload.response.MessageResponse;
import com.rest.login.repository.ClientRepository;
import com.rest.login.repository.EvaluationRepository;
import com.rest.login.repository.UserRepository;
import com.rest.login.security.services.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/data")
public class EvaluationController {

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EvaluationRepository evaluationRepository;

    @Autowired
    EvaluationService evaluationService;

    @GetMapping("/evaluations")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public List<EvaluationDTO> retriveAllEvaluations() {
        return evaluationService.getAllEvaluationsDTO();
    }

    @GetMapping("/evaluation/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public Evaluation retrieveAnyEvaluationById(@PathVariable Long id) {
        return evaluationRepository.getById(id);
    }

    @GetMapping("/users/{userId}/clients/{clientId}/evaluations")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasUserId(authentication,#userId)")
    public ResponseEntity<?> retrieveAllEvaluationsByClientId(@PathVariable Long userId, @PathVariable Long clientId) {
        Client client = getClientById(clientId);

        if (client == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Client was not found."));
        }
        Long dbClientId = client.getId();
        List<EvaluationDTO> evaluations = evaluationService.getAllClientEvaluations(dbClientId);

        if (evaluations.isEmpty()) {
            return ResponseEntity.ok(new MessageResponse("Error: Client does not have any evaluations created!"));
        }
        return ResponseEntity.ok().body(new MessageResponse("Listing client's evaluations!", evaluations));
    }

    @GetMapping("/users/{userId}/clients/{clientId}/evaluations/{evalId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasUserId(authentication,#userId)")
    public EvaluationDTO retrieveEvaluationByClientId(@PathVariable Long userId, @PathVariable Long clientId, @PathVariable Long evalId) {
        return evaluationService.getEvaluationDTOByClientIdAndEvalId(clientId, evalId);
    }

    @PostMapping(path = "/users/{id}/clients/{clientId}/add-evaluation", consumes = "application/json")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasUserId(authentication,#id)")
    public ResponseEntity<MessageResponse> createEvaluation(@Valid @RequestBody AddEvaluationRequest addEvaluationRequest, @PathVariable Long id, @PathVariable Long clientId) {
        Client client = getClientById(clientId);

        if (client == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Client was not found."));
        } else {
            Evaluation evaluation = evaluationService.createBasicEvaluation(client);
            evaluation.setDescription(addEvaluationRequest.getDescription());
            evaluationRepository.save(evaluation);

            return ResponseEntity.ok().body(new MessageResponse("Evaluation added.", new EvaluationDTO(evaluation)));
        }
    }

    @PostMapping(path = "/users/{id}/clients/{clientId}/add-evaluation")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasUserId(authentication,#id)")
    public ResponseEntity<MessageResponse> createEvaluation(@PathVariable Long id, @PathVariable Long clientId) {
        Client client = getClientById(clientId);

        if (client == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Client was not found."));
        } else {
            Evaluation evaluation = evaluationService.createBasicEvaluation(client);
            evaluationRepository.save(evaluation);

            return ResponseEntity.ok().body(new MessageResponse("Evaluation added.", new EvaluationDTO(evaluation)));
        }
    }

    private Client getClientById(Long clientId) {
        Client client = null;
        try {
            client = clientRepository.findById(clientId).get();
        } catch (EntityNotFoundException | NoSuchElementException e) {
            return null;
        }
        return client;
    }
}