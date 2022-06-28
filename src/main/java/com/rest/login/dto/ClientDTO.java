package com.rest.login.dto;

import com.rest.login.models.Client;
import com.rest.login.models.User;

public class ClientDTO {

    private Long id;
    private String username;
    private String email;
    private Long userId;
    private User user;

    public ClientDTO(Client client) {
        this.id = client.getId();
        this.username = client.getUsername();
        this.email = client.getEmail();
        this.userId = client.getUser().getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
