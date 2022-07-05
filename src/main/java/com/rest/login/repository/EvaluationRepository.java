package com.rest.login.repository;

import com.rest.login.models.Client;
import com.rest.login.models.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long>{
	List<Evaluation> findAll();
}
