package com.rest.login.models;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "evaluation_records")
public class Evaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EStatus evaluation_status;

    @Size(max = 1500)
    private String description;

    @Size(max = 1500)
    private String result;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    private Client client;

    public Evaluation() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EStatus getEvaluationStatus() {
        return evaluation_status;
    }

    public void setEvaluationStatus(EStatus evaluation_status) {
        this.evaluation_status = evaluation_status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
