package com.javashams.springcontainer.repository;

import com.javashams.springcontainer.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    Candidate findByName(String name);
    List<Candidate> findAllByOrderByVoteCountDesc();
}
