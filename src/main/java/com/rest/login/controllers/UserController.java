package com.rest.login.controllers;

import static com.rest.login.enums.EResponses.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rest.login.dto.ClientDTO;
import com.rest.login.models.Client;
import com.rest.login.repository.ClientRepository;
import com.rest.login.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rest.login.models.User;
import com.rest.login.payload.request.SignUpRequest;
import com.rest.login.payload.request.UpdateRequest;
import com.rest.login.payload.response.MessageResponse;
import com.rest.login.repository.UserRepository;
import com.rest.login.security.services.UserDetailsServiceImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/data")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @GetMapping("/users/{username}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasUserName(authentication,#username)")
    public ResponseEntity<MessageResponse> getUserByUsername(@PathVariable String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return ResponseEntity.ok(new MessageResponse(USER_EXISTS.getMessage(), userDetails));
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> retriveAllUsers() {
        return ResponseEntity.ok().body(
                new MessageResponse(LISTING_ALL_USERS.getMessage(), userDetailsService.getAllUsers()));
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasUserId(authentication,#id)")
    public ResponseEntity<MessageResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateRequest updateRequest) {
        User user;
        try {
            user = userDetailsService.getUserById(id);
        } catch (EntityNotFoundException | NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(USER_NOT_FOUND.getMessage()));
        }

        User savedUser = userDetailsService.updateUser(user, updateRequest);
        UserDetails savedUserDetails = UserDetailsImpl.build(savedUser);
        return ResponseEntity.ok(new MessageResponse(USER_UPDATED.getMessage(), savedUserDetails));
    }
}