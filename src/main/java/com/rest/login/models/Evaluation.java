package com.rest.login.models;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "evaluation_records")
public class Evaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(20) default 'NEW'")
    private EStatus evaluation_status = EStatus.NEW;

    @Size(max = 1500)
    private String description_info;

    @Size(max = 1500)
    private String result;

    @ManyToOne(fetch = FetchType.LAZY)
    private Client client;

    @OneToMany(mappedBy = "evaluation",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Board> boards;

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

    public String getDescription_info() {
        return description_info;
    }

    public void setDescription_info(String description_info) {
        this.description_info = description_info;
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

    public List<Board> getBoards() {
        return boards;
    }

    public void setBoards(List<Board> boards) {
        this.boards = boards;
    }
}
