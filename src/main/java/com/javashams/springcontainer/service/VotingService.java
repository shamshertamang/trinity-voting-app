package com.javashams.springcontainer.service;

import com.javashams.springcontainer.model.Vote;
import com.javashams.springcontainer.model.Candidate;
import com.javashams.springcontainer.repository.VoteRepository;
import com.javashams.springcontainer.repository.CandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class VotingService {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    // Email validation pattern for Trinity College emails
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^.]+@trincoll\\.edu$");

    public boolean isValidTrinityEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        // Check if email matches pattern and has exactly one dot
        long dotCount = email.chars().filter(ch -> ch == '.').count();
        return EMAIL_PATTERN.matcher(email).matches() && dotCount == 1;
    }

    public boolean hasAlreadyVoted(String email) {
        return voteRepository.existsByVoterEmail(email);
    }

    @Transactional
    public String submitVote(String voterEmail, String candidateName) {
        // Validate email
        if (!isValidTrinityEmail(voterEmail)) {
            return "Invalid Trinity College email format.";
        }

        // Check if user has already voted
        if (hasAlreadyVoted(voterEmail)) {
            return "You have already submitted a vote.";
        }

        // Validate candidate name
        if (candidateName == null || candidateName.trim().isEmpty()) {
            return "Candidate name cannot be empty.";
        }

        candidateName = candidateName.trim();

        try {
            // Find or create candidate
            Candidate candidate = candidateRepository.findByName(candidateName);
            if (candidate == null) {
                candidate = new Candidate(candidateName);
                candidateRepository.save(candidate);
            }

            // Increment vote count
            candidate.incrementVoteCount();
            candidateRepository.save(candidate);

            // Save the vote
            Vote vote = new Vote(voterEmail, candidateName);
            voteRepository.save(vote);

            return "SUCCESS";

        } catch (Exception e) {
            return "Error processing vote: " + e.getMessage();
        }
    }

    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAllByOrderByVoteCountDesc();
    }
}