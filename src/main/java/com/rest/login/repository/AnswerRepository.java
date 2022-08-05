package com.rest.login.repository;

import com.rest.login.models.Answer;
import com.rest.login.models.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    public List<Answer> findByBoard_id(Long id);
}