package com.rest.login.payload.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class AddClientRequest {

    @NotBlank(message = "Name is missing.")
    //@Pattern(regexp = "^[a-žA-Ž0-9-_!? ]{1,20}")
    private String name;

    @Size(max = 50)
    @Email(message = "Formatting doesn't look as email!")
    private String email;

    private String description;

    public static AddClientRequest createFullRequest(String name, String email, String description) {
        AddClientRequest addClientRequest = new AddClientRequest();
        addClientRequest.setName(name);
        addClientRequest.setEmail(email);
        addClientRequest.setDescription(description);
        return addClientRequest;
    }

    public static AddClientRequest createNameEmailRequest(String name, String email) {
        AddClientRequest addClientRequest = new AddClientRequest();
        addClientRequest.setName(name);
        addClientRequest.setEmail(email);
        return addClientRequest;
    }

    public static AddClientRequest createNameDescriptionRequest(String name, String description) {
        AddClientRequest addClientRequest = new AddClientRequest();
        addClientRequest.setName(name);
        addClientRequest.setDescription(description);
        return addClientRequest;
    }

    public static AddClientRequest createNameRequest(String name) {
        AddClientRequest addClientRequest = new AddClientRequest();
        addClientRequest.setName(name);
        return addClientRequest;
    }

    public static AddClientRequest createMissingNameRequest(String email, String description) {
        AddClientRequest addClientRequest = new AddClientRequest();
        addClientRequest.setEmail(email);
        addClientRequest.setDescription(description);
        return addClientRequest;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
