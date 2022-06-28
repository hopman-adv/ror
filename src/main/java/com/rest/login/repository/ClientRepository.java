package com.rest.login.repository;

import com.rest.login.models.Client;
import com.rest.login.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long>{
	Optional<Client> findByUsername(String username);

	List<Client> findAll();
	
	Boolean existsByUsername(String username);
	
	Boolean existsByEmail(String email);
}
