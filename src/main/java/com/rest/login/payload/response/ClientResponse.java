package com.rest.login.payload.response;

public class ClientResponse {

    private String id;
    private String username;
    private String email;
    private Long userId;

    public ClientResponse(String id, String username, String email, Long userId) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
