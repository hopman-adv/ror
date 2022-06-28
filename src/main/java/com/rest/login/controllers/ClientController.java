package com.rest.login.controllers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rest.login.dto.ClientDTO;
import com.rest.login.models.Client;
import com.rest.login.models.User;
import com.rest.login.payload.request.UpdateRequest;
import com.rest.login.payload.response.MessageResponse;
import com.rest.login.repository.ClientRepository;
import com.rest.login.repository.UserRepository;
import com.rest.login.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/data")
public class ClientController {

	@Autowired
	ClientRepository clientRepository;

	@Autowired
	UserRepository userRepository;


	@GetMapping("/clients")
	//@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public List<ClientDTO> retriveAllClients() {
		return getAllClientsDTO();
	}

	@GetMapping("/client/{id}")
	//@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public ClientDTO retrieveClientById(@PathVariable Long id) {
		return new ClientDTO(clientRepository.getById(id));
	}

	@GetMapping("/users/{id}/clients")
	//@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public List<ClientDTO> retrieveClientByUserId(@PathVariable Long id) {
		List<ClientDTO> list = getAllClientsDTO();
		return list.stream().filter(client -> Objects.equals(client.getUserId(), id)).collect(Collectors.toList());
	}

	private List<ClientDTO> getAllClientsDTO() {
		return clientRepository.findAll()
				.stream()
				.map(ClientDTO::new)
				.collect(Collectors.toList());
	}
}