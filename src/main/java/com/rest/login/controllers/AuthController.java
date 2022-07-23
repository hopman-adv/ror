package com.rest.login.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rest.login.models.ERole;
import com.rest.login.models.RefreshToken;
import com.rest.login.models.Role;
import com.rest.login.models.User;
import com.rest.login.payload.request.LogOutRequest;
import com.rest.login.payload.request.LoginRequest;
import com.rest.login.payload.request.SignUpRequest;
import com.rest.login.payload.request.TokenRefreshRequest;
import com.rest.login.payload.response.JwtResponse;
import com.rest.login.payload.response.MessageResponse;
import com.rest.login.payload.response.TokenRefreshResponse;
import com.rest.login.payload.response.UserInfoResponse;
import com.rest.login.repository.RoleRepository;
import com.rest.login.repository.UserRepository;
import com.rest.login.security.jwt.JwtUtils;
import com.rest.login.security.jwt.exception.TokenRefreshException;
import com.rest.login.security.services.RefreshTokenService;
import com.rest.login.security.services.UserDetailsImpl;

import static com.rest.login.enums.EResponses.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	RefreshTokenService refreshTokenService;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

		String jwt = jwtUtils.generateJwtToken(userDetails);

		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());

		RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

		return ResponseEntity.ok(new JwtResponse(jwt, refreshToken.getToken(), userDetails.getId(),
				userDetails.getUsername(), userDetails.getEmail(), roles));
	}

	@PostMapping("/refreshtoken")
	public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
		String requestRefreshToken = request.getRefreshToken();

		return refreshTokenService.findByToken(requestRefreshToken)
				.map(refreshTokenService::verifyExpiration)
				.map(RefreshToken::getUser)
				.map(user -> {
					String token = jwtUtils.generateTokenFromUsername(user.getUsername());
					return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
				})
				.orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
						"Refresh token is not in database!"));
	}


	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse(USERNAME_TAKEN.getMessage()));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity.badRequest().body(new MessageResponse(EMAIL_ALREADY_USED.getMessage()));
		}

		// Create new user's account
		User user = new User(signUpRequest.getUsername(),
				signUpRequest.getEmail(),
				encoder.encode(signUpRequest.getPassword()));
		//Beru Role jako string do setu
		Set<String> strRoles = signUpRequest.getRole();
		Set<Role> roles = new HashSet<>();
		//Chybí li role nastaví se role USER
		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND.getMessage()));
			roles.add(userRole);
		} else {
			//pro každý string role najdu roli v enum a přidám ji do Listu rolí
			strRoles.forEach(role -> {
				switch (role) {
				case "admin":
					throw new RuntimeException(ADMIN_ROLE_NOT_REGISTREABLE.getMessage());
				case "mod":
					Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
					.orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND.getMessage()));
					roles.add(modRole);

					break;
				default:
					Role userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND.getMessage()));
					roles.add(userRole);
				}
			});
		}
		//přiřadím list rolí k danému uživateli
		user.setRoles(roles);
		//Uložím uživatele do DB
		userRepository.save(user);
		//Vrátím OK hlášku
		return ResponseEntity.ok(new MessageResponse(USER_REGISTERED.getMessage()));
	}

	  @PostMapping("/logout")
	  public ResponseEntity<?> logoutUser(@Valid @RequestBody LogOutRequest logOutRequest) {
	    refreshTokenService.deleteByUserId(logOutRequest.getUserId());
	    return ResponseEntity.ok(new MessageResponse(LOGOUT_SUCCESSFUL.getMessage()));
	  }

}
