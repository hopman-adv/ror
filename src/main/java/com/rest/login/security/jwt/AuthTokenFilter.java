package com.rest.login.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.rest.login.security.services.UserDetailsServiceImpl;


public class AuthTokenFilter extends OncePerRequestFilter{

	  @Autowired
	  private JwtUtils jwtUtils;

	  @Autowired
	  private UserDetailsServiceImpl userDetailsService;

	  private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);
	
	  @Override
	  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
	      throws ServletException, IOException {
	    try {
	      String jwt = parseJwt(request);
	      if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
	    	  //Jeli token validní, vytáhneme z něj username
	        String username = jwtUtils.getUsernameFromJwtToken(jwt);
	        //Získáme user details
	        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
	        //Vytvoříme Authentication object z user details
	        UsernamePasswordAuthenticationToken authentication = 
	            new UsernamePasswordAuthenticationToken(userDetails,
	                                                    null,
	                                                    userDetails.getAuthorities());
	        
	        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	        //Nastavíme user details do security context pomocí setAuthentication
	        SecurityContextHolder.getContext().setAuthentication(authentication);
	        /*Nyní kdykoliv je třeba získat User Details, lze použít:
	        	UserDetails userDetails =
	        	(UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	        // userDetails.getUsername()
	        // userDetails.getPassword()
	        // userDetails.getAuthorities()
	        */
	      }
	    } catch (Exception e) {
	      logger.error("Cannot set user authentication: {}", e);
	    }

	    filterChain.doFilter(request, response);
	  }
	  
	  //bereme JWT token z cookies
	  private String parseJwt(HttpServletRequest request) {
		    String headerAuth = request.getHeader("Authorization");

		    if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
		      return headerAuth.substring(7, headerAuth.length());
		    }
		    return null;
	  }	  
}
