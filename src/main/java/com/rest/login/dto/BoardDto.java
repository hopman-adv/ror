package com.rest.login.dto;

import com.rest.login.models.Board;
import com.rest.login.models.EBoardStatus;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

public class BoardDto implements Serializable {
    private final Long id;
    private final EBoardStatus board_status;
    @Size(max = 1500)
    private final String comments;
    private final Long evaluationId;

    public BoardDto(Long id, EBoardStatus board_status, String comments, Long evaluationId) {
        this.id = id;
        this.board_status = board_status;
        this.comments = comments;
        this.evaluationId = evaluationId;
    }

    public BoardDto(Board board) {
        this.id = board.getId();
        this.board_status = board.getBoard_status();
        this.comments = board.getComments();
        this.evaluationId = board.getEvaluation().getId();
    }


    public Long getId() {
        return id;
    }

    public EBoardStatus getBoard_status() {
        return board_status;
    }

    public String getComments() {
        return comments;
    }

    public Long getEvaluationId() {
        return evaluationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoardDto entity = (BoardDto) o;
        return Objects.equals(this.id, entity.id) &&
                Objects.equals(this.board_status, entity.board_status) &&
                Objects.equals(this.comments, entity.comments) &&
                Objects.equals(this.evaluationId, entity.evaluationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, board_status, comments, evaluationId);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "board_status = " + board_status + ", " +
                "comments = " + comments + ", " +
                "evaluationId = " + evaluationId + ")";
    }
}
