package com.rest.login.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import com.rest.login.models.RefreshToken;
import com.rest.login.models.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>{

	@Override
	Optional<RefreshToken> findById(Long id);

	Optional<RefreshToken>findByToken(String token);

	@Modifying
	int deleteByUser(User user);

}
