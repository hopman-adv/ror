package com.rest.login.dto;

import com.rest.login.models.Board;

import java.io.Serializable;
import java.util.Objects;

public class AnswerDto implements Serializable {
    private final Long id;
    private final String answer_text;
    private final Long boardId;

    public AnswerDto(Long id, String answer_text, Board board) {
        this.id = id;
        this.answer_text = answer_text;
        this.boardId = board.getId();
    }

    public Long getId() {
        return id;
    }

    public String getAnswer_text() {
        return answer_text;
    }

    public Long getBoardId() {
        return boardId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnswerDto entity = (AnswerDto) o;
        return Objects.equals(this.id, entity.id) &&
                Objects.equals(this.answer_text, entity.answer_text) &&
                Objects.equals(this.boardId, entity.boardId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, answer_text, boardId);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "answer_text = " + answer_text + ", " +
                "boardId = " + boardId + ")";
    }
}
