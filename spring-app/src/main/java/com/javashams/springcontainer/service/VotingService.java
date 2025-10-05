package com.javashams.springcontainer.service;

import com.javashams.springcontainer.model.Candidate;
import com.javashams.springcontainer.model.Vote;
import com.javashams.springcontainer.repository.CandidateRepository;
import com.javashams.springcontainer.repository.VoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class VotingService {

    private final VoteRepository voteRepository;
    private final CandidateRepository candidateRepository;

    public VotingService(VoteRepository voteRepository, CandidateRepository candidateRepository) {
        this.voteRepository = voteRepository;
        this.candidateRepository = candidateRepository;
    }

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^.]+@trincoll\\.edu$");

    public boolean isValidTrinityEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        long dotCount = email.chars().filter(ch -> ch == '.').count();
        return EMAIL_PATTERN.matcher(email).matches() && dotCount == 1;
    }

    public boolean hasAlreadyVoted(String email) {
        return voteRepository.existsByVoterEmail(email);
    }

    @Transactional
    public String submitVote(String voterEmail, String candidateName) {
        if (!isValidTrinityEmail(voterEmail)) return "Invalid Trinity College email format. Please use username@trincoll.edu";
        if (hasAlreadyVoted(voterEmail)) return "You have already submitted a vote.";
        if (candidateName == null || candidateName.trim().isEmpty()) return "Candidate name cannot be empty.";

        Candidate candidate = candidateRepository.findByName(candidateName.trim());
        if (candidate == null) candidate = candidateRepository.save(new Candidate(candidateName.trim()));

        candidate.incrementVoteCount();
        candidateRepository.save(candidate);

        Vote vote = new Vote(voterEmail.trim(), candidate.getName());
        voteRepository.save(vote);
        return "SUCCESS";
    }

    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAllByOrderByVoteCountDesc();
    }

    public List<Vote> getAllVotes() {
        return voteRepository.findAll();
    }

    public Optional<Vote> getVoteByEmail(String voterEmail) {
        return voteRepository.findByVoterEmail(voterEmail);
    }

    @Transactional
    public String updateVote(String voterEmail, String newCandidateName) {
        if (!isValidTrinityEmail(voterEmail)) return "Invalid Trinity College email format.";
        if (newCandidateName == null || newCandidateName.trim().isEmpty()) return "Candidate name cannot be empty.";

        Vote vote = voteRepository.findByVoterEmail(voterEmail)
                .orElse(null);
        if (vote == null) return "No existing vote found for this email.";

        String oldCandidateName = vote.getCandidateName();
        if (oldCandidateName.equalsIgnoreCase(newCandidateName.trim())) return "No change: candidate is the same.";

        // decrement old
        Candidate oldC = candidateRepository.findByName(oldCandidateName);
        if (oldC != null && oldC.getVoteCount() > 0) {
            oldC.setVoteCount(oldC.getVoteCount() - 1);
            candidateRepository.save(oldC);
        }

        // increment new (create if needed)
        Candidate newC = candidateRepository.findByName(newCandidateName.trim());
        if (newC == null) newC = candidateRepository.save(new Candidate(newCandidateName.trim()));
        newC.setVoteCount(newC.getVoteCount() + 1);
        candidateRepository.save(newC);

        // update vote
        vote.setCandidateName(newC.getName());
        vote.setVoteTime(LocalDateTime.now());
        voteRepository.save(vote);

        return "SUCCESS";
    }

    @Transactional
    public String deleteVote(String voterEmail) {
        if (!isValidTrinityEmail(voterEmail)) return "Invalid Trinity College email format.";
        Vote vote = voteRepository.findByVoterEmail(voterEmail).orElse(null);
        if (vote == null) return "No existing vote found for this email.";

        Candidate c = candidateRepository.findByName(vote.getCandidateName());
        if (c != null && c.getVoteCount() > 0) {
            c.setVoteCount(c.getVoteCount() - 1);
            candidateRepository.save(c);
        }
        voteRepository.delete(vote);
        return "SUCCESS";
    }
}
