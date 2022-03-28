package com.rest.login.security.expression;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.rest.login.security.services.UserDetailsImpl;

@Component("userSecurity")
public class UserSecurity {
	public boolean hasUserName(Authentication authentication, String username) {
		Boolean auth;
		if(authentication instanceof AnonymousAuthenticationToken) {
			auth = false;
		}else {
			UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
			String loggedUsername = userDetails.getUsername();
			auth = loggedUsername.equals(username);
		}
		return auth;
	}
	
	public boolean hasUserId(Authentication authentication, Long id) {
		Boolean auth;
		if(authentication instanceof AnonymousAuthenticationToken) {
			auth = false;
		}else {
			UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
			Long loggedId = userDetails.getId();
			auth = loggedId == id;
		}
		return auth;
	}

}