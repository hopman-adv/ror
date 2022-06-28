package com.rest.login.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rest.login.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	Optional<User> findByUsername(String username);

	@Override
	Optional<User> findById(Long id);

	List<User> findAll();
	
	Boolean existsByUsername(String username);
	
	Boolean existsByEmail(String email);


}
