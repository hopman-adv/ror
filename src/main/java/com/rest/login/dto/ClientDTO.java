package com.rest.login.dto;

import com.rest.login.models.Client;
import com.rest.login.models.User;

public class ClientDTO {

    private Long id;
    private String name;
    private String email;
    private Long userId;
    private String description;

    public ClientDTO(Client client) {
        this.id = client.getId();
        this.name = client.getName();
        this.email = client.getEmail();
        this.userId = client.getUser().getId();
        this.description = client.getDescription();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setUsername(String username) {
        this.name = name;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
