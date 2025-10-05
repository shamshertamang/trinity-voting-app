package com.javashams.springcontainer.repository;

import com.javashams.springcontainer.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    boolean existsByVoterEmail(String voterEmail);
    Optional<Vote> findByVoterEmail(String voterEmail);
    long deleteByVoterEmail(String voterEmail);
}
