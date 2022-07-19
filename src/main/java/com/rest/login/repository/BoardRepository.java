package com.rest.login.repository;

import com.rest.login.models.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    public List<Board> findByEvaluation_id(Long id);
}
