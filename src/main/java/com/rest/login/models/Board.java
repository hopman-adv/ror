package com.rest.login.models;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "boards")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(20) default 'NEW'")
    private EBoardStatus board_status = EBoardStatus.NEW;

    @Size(max = 1500)
    private String comments;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "evaluation_id")
    private Evaluation evaluation;

    @OneToMany(mappedBy = "id", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Answer> answers;

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public Board() { }

    public Board(Evaluation evaluation) { this.evaluation = evaluation; }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EBoardStatus getBoard_status() {
        return board_status;
    }

    public void setBoard_status(EBoardStatus board_status) {
        this.board_status = board_status;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(Evaluation evaluation) {
        this.evaluation = evaluation;
    }
}
