package com.rest.login.payload.request;

import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UpdateRequest {

    @NotBlank
	@Pattern(regexp = "^[a-zA-Z0-9]{3,20}")
    private String username;
 
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;
        
    
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	
}
