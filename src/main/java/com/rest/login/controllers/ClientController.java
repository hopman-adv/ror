package com.rest.login.controllers;

import com.rest.login.dto.ClientDTO;
import com.rest.login.models.Board;
import com.rest.login.models.Client;
import com.rest.login.payload.request.AddClientRequest;
import com.rest.login.payload.response.MessageResponse;
import com.rest.login.repository.ClientRepository;
import com.rest.login.repository.UserRepository;
import com.rest.login.security.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;

import static com.rest.login.enums.EResponses.*;
import static com.rest.login.payload.response.MessageResponse.createMessageResponseWithClientDTOsList;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/data")
public class ClientController {

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ClientService clientService;

    @GetMapping("/clients")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> retriveAllClients() {
        return ResponseEntity.ok().body(createMessageResponseWithClientDTOsList(clientService.getAllClientsDTO()));
    }

    @GetMapping("/clients/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> retrieveAnyClientById(@PathVariable Long id) {
        Client client;
        try {
            client = clientService.getClientById(id);
        } catch (EntityNotFoundException | NoSuchElementException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(CLIENT_NOT_FOUND.getMessage()));
        }
        return ResponseEntity.ok().body(new MessageResponse(CLIENT_FOUND.getMessage(), clientService.getClientDTO(client)));
    }

    @GetMapping("/users/{id}/clients")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasUserId(authentication,#id)")
    public ResponseEntity<MessageResponse> retrieveAllClientsByUserId(@PathVariable Long id) {
        List<ClientDTO> list = clientService.getAllUsersClientsDTOsByUserId(id);
        if(list.isEmpty()) {
            return ResponseEntity.ok().body(new MessageResponse(CLIENTS_NOT_FOUND.getMessage()));
        }
        return ResponseEntity.ok().body(
                createMessageResponseWithClientDTOsList(list));
    }

    @GetMapping("/users/{userId}/clients/{clientId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasUserId(authentication,#userId)")
    public ResponseEntity<MessageResponse> retrieveOwnedClientById(@PathVariable Long userId, @PathVariable Long clientId) {
        ClientDTO clientDTO;
        try {
            clientDTO = clientService.getClientDTOByUserIdAndClientId(userId, clientId);
        }catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(CLIENT_NOT_FOUND.getMessage()));
        }
        return ResponseEntity.ok().body(new MessageResponse(CLIENT_FOUND.getMessage(), clientDTO));
    }
    //TODO: zde pokračovat
    @PostMapping("/users/{id}/clients")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasUserId(authentication,#id)")
    public ClientDTO createClient(@Valid @RequestBody AddClientRequest addClientRequest, @PathVariable Long id) {
        return clientService.createClient(id, addClientRequest);
    }

    @DeleteMapping("/users/{userId}/clients/{clientId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasUserId(authentication,#userId)")
    public ResponseEntity<MessageResponse> deleteClient(@PathVariable Long userId, @PathVariable Long clientId) {
        try {
            clientService.getClientDTOByUserIdAndClientId(userId, clientId);
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(CLIENT_NOT_FOUND.getMessage()));
        }
        clientRepository.deleteById(clientId);
        return ResponseEntity.ok(new MessageResponse(CLIENT_DELETED.getMessage()));
    }

    @PutMapping("/users/{userId}/clients/{clientId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasUserId(authentication,#userId)")
    public ResponseEntity<MessageResponse> editClient(@PathVariable Long userId, @PathVariable Long clientId, @Valid @RequestBody AddClientRequest addClientRequest) {
        return clientService.editAndSaveClient(clientId, addClientRequest);
    }
}