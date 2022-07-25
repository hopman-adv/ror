package com.rest.login.security.services;

import com.rest.login.dto.ClientDTO;
import com.rest.login.models.Client;
import com.rest.login.models.RefreshToken;
import com.rest.login.models.User;
import com.rest.login.payload.request.AddClientRequest;
import com.rest.login.payload.request.AddEvaluationRequest;
import com.rest.login.payload.response.MessageResponse;
import com.rest.login.repository.ClientRepository;
import com.rest.login.repository.RefreshTokenRepository;
import com.rest.login.repository.UserRepository;
import com.rest.login.security.jwt.exception.TokenRefreshException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.rest.login.enums.EResponses.*;
import static com.rest.login.models.Client.*;
import static com.rest.login.models.Client.createFullClient;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private UserRepository userRepository;

    private Client createClientDependingOnPayload(AddClientRequest addClientRequest, User user) {
        if (addClientRequest.getName() == null) {
            throw new UnsupportedOperationException(MISSING_NAME_IN_BODY.getMessage());
        }
        if (addClientRequest.getDescription() == null && addClientRequest.getEmail() == null) {
            return createNameClient(addClientRequest.getName(), user);
        }
        if (addClientRequest.getDescription() != null && addClientRequest.getEmail() == null) {
            return createNameDescriptionClient(addClientRequest.getName(), addClientRequest.getDescription(), user);
        }
        if (addClientRequest.getDescription() == null && addClientRequest.getEmail() != null) {
            return createNameEmailClient(addClientRequest.getName(), addClientRequest.getEmail(), user);
        }
        return createFullClient(addClientRequest.getName(), addClientRequest.getEmail(), addClientRequest.getDescription(), user);
    }

    public ClientDTO createClient(Long userId, AddClientRequest addClientRequest) {
        User user = userRepository.findById(userId).get();
        Client client = createClientDependingOnPayload(addClientRequest, user);
        clientRepository.save(client);
        return new ClientDTO(client);
    }

    public List<ClientDTO> getAllClientsDTO() {
        return clientRepository.findAll()
                .stream()
                .map(ClientDTO::new)
                .collect(Collectors.toList());
    }

    public List<ClientDTO> getAllUsersClientsDTOsByUserId(Long id) {
        List<ClientDTO> list = getAllClientsDTO();
        return list.stream().filter(client -> Objects.equals(client.getUserId(), id)).collect(Collectors.toList());
    }

    public ClientDTO getClientDTOByUserIdAndClientId(Long userId, Long clientId) throws NoSuchElementException {
        List<ClientDTO> list = getAllClientsDTO();
        return list.stream()
                .filter(client -> Objects.equals(client.getUserId(), userId)
                        && Objects.equals(client.getId(), clientId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(CLIENT_NOT_FOUND.getMessage()));
    }

    public Client getClientById(Long clientId) {
        Client client = null;
        try {
            client = clientRepository.findById(clientId).get();
        } catch (EntityNotFoundException | NoSuchElementException e) {
            return null;
        }
        return client;
    }

    public ResponseEntity<MessageResponse> editAndSaveClient(Long clientId, AddClientRequest addClientRequest) {
        Client client = getClientById(clientId);

        if (client == null) {
            return ResponseEntity.badRequest().body(new MessageResponse(CLIENT_NOT_FOUND.getMessage()));
        }
        Long userId = client.getUser().getId();

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

        return ResponseEntity.ok(new MessageResponse(CLIENT_UPDATED.getMessage(), getClientDTOByUserIdAndClientId(userId, clientId)));
    }
}
