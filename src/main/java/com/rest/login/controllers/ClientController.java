package com.rest.login.controllers;

import com.rest.login.dto.ClientDTO;
import com.rest.login.payload.request.AddClientRequest;
import com.rest.login.payload.response.MessageResponse;
import com.rest.login.repository.ClientRepository;
import com.rest.login.repository.UserRepository;
import com.rest.login.security.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;
import static com.rest.login.enums.EResponses.CLIENT_NOT_FOUND;

import static com.rest.login.enums.EResponses.CLIENT_DELETED;

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
    public List<ClientDTO> retriveAllClients() {
        return clientService.getAllClientsDTO();
    }

    @GetMapping("/clients/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ClientDTO retrieveAnyClientById(@PathVariable Long id) {
        return new ClientDTO(clientRepository.getById(id));
    }

    @GetMapping("/users/{id}/clients")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasUserId(authentication,#id)")
    public List<ClientDTO> retrieveAllClientsByUserId(@PathVariable Long id) {
        return clientService.getAllUsersClientsDTOsByUserId(id);
    }

    @GetMapping("/users/{userId}/clients/{clientId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasUserId(authentication,#userId)")
    public ClientDTO retrieveOwnedClientById(@PathVariable Long userId, @PathVariable Long clientId) {
        return clientService.getClientDTOByUserIdAndClientId(userId, clientId);
    }

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