package com.javashams.springcontainer.repository;

import com.javashams.springcontainer.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    boolean existsByVoterEmail(String voterEmail);
}
