package com.rest.login.security.services;

import com.rest.login.models.RefreshToken;
import com.rest.login.models.User;
import com.rest.login.repository.ClientRepository;
import com.rest.login.repository.RefreshTokenRepository;
import com.rest.login.repository.UserRepository;
import com.rest.login.security.jwt.exception.TokenRefreshException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClientService {


	@Autowired
	private ClientRepository clientRepository;



}
