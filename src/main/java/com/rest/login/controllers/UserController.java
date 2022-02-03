package com.rest.login.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rest.login.models.User;
import com.rest.login.repository.UserRepository;
import com.rest.login.security.services.UserDetailsServiceImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/data")
public class UserController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	private UserDetailsServiceImpl userDetailsService;


	@GetMapping("/users/{username}")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public EntityModel<UserDetails> getUserByUsername(@PathVariable String username) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		EntityModel<UserDetails> resource = EntityModel.of(userDetails);
		/*Creating link to endpoint .getAllUsers() - /users
		WebMvcLinkBuilder linkTo = 
				linkTo(methodOn(this.getClass()).getAllUsers());
		//Adding link with all-users label to resource
		resource.add(linkTo.withRel("all-users"));
		*/
		return resource;
	}
	
	@GetMapping("/users")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public List<User> retriveAllUsers() {  
		return userRepository.findAll();  
	}  

}