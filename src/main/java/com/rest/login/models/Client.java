package com.rest.login.models;

import com.rest.login.payload.request.AddClientRequest;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Pattern(regexp = "^[a-žA-Ž0-9-_!? ']{1,20}") //Validation for Czech and English names
    private String name;

    @Size(max = 50)
    @Email
    private String email;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Size(max = 1500)
    private String description;

    @OneToMany(cascade = CascadeType.ALL,
            orphanRemoval = true,
            mappedBy = "client")
    private List<Evaluation> evaluations;

    public Client() {
    }

    public Client(User user) {
        this.user = user;
    }

    public Client(@NotBlank @Size(max = 20) String name, @NotBlank @Size(max = 50) @Email String email, @NotBlank User user, String description, List<Evaluation> evaluations) {
        this.user = user;
        this.name = name;
        this.email = email;
        this.description = description;
        this.evaluations = evaluations;
    }

    public static Client createFullClient(String name, String email, String description, User user) {
        Client client = new Client(user);
        client.setName(name);
        client.setEmail(email);
        client.setDescription(description);
        return client;
    }

    public static Client createNameEmailClient(String name, String email, User user) {
        Client client = new Client(user);
        client.setName(name);
        client.setEmail(email);
        return client;
    }

    public static Client createNameDescriptionClient(String name, String description, User user) {
        Client client = new Client(user);
        client.setName(name);
        client.setDescription(description);
        return client;
    }

    public static Client createNameClient(String name, User user) {
        Client client = new Client(user);
        client.setName(name);
        return client;
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

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Evaluation> getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(List<Evaluation> evaluations) {
        this.evaluations = evaluations;
    }
}
