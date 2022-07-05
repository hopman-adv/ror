package com.rest.login.controllers;

import com.rest.login.dto.ClientDTO;
import com.rest.login.models.Client;
import com.rest.login.models.User;
import com.rest.login.payload.request.AddClientRequest;
import com.rest.login.payload.response.MessageResponse;
import com.rest.login.repository.ClientRepository;
import com.rest.login.repository.UserRepository;
import com.rest.login.security.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;

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

    @GetMapping("/client/{id}")
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

    @PostMapping("/users/{id}/add-client")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasUserId(authentication,#id)")
    public ClientDTO createClient(@Valid @RequestBody AddClientRequest addClientRequest, @PathVariable Long id) {
        User user = userRepository.findById(id).get();
        Client client = clientService.createClientDependingOnPayload(addClientRequest, user);
        clientRepository.save(client);
        return new ClientDTO(client);
    }

    @PostMapping("/users/{userId}/delete-client/{clientId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasUserId(authentication,#userId)")
    public ResponseEntity deleteClient(@PathVariable Long userId, @PathVariable Long clientId) {
        try {
            clientService.getClientDTOByUserIdAndClientId(userId, clientId);
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Client not found in database!"));
        }
        clientRepository.deleteById(clientId);
        return ResponseEntity.ok(new MessageResponse("Client deleted!"));
    }

    @PutMapping("/users/{userId}/edit-client/{clientId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasUserId(authentication,#userId)")
    public ResponseEntity editClient(@PathVariable Long userId, @PathVariable Long clientId, @Valid @RequestBody AddClientRequest addClientRequest) {
        Client client;
        //TODO: Možná přesunout a zmergovat s createClientDependingOnPayload v ClientService
        try {
            client = clientRepository.getById(clientId);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Client not found in database!"));
        }
        if (addClientRequest.getName() != null) {
            client.setName(addClientRequest.getName());
        }
        if (addClientRequest.getEmail() != null) {
            client.setEmail(addClientRequest.getEmail());
        }
        if (addClientRequest.getDescription() != null) {
            client.setDescription(addClientRequest.getDescription());
        }

        clientRepository.save(client);

        return ResponseEntity.ok(new MessageResponse("Client updated.", clientService.getClientDTOByUserIdAndClientId(userId, clientId)));
    }

}