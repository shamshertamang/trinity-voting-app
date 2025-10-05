package com.javashams.springcontainer.controller;

import com.javashams.springcontainer.model.Candidate;
import com.javashams.springcontainer.model.Vote;
import com.javashams.springcontainer.service.VotingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class VoteController {

    private final VotingService votingService;

    public VoteController(VotingService votingService) {
        this.votingService = votingService;
    }

    @GetMapping("/candidates")
    public ResponseEntity<List<Candidate>> getAllCandidates() {
        return ResponseEntity.ok(votingService.getAllCandidates());
    }

    // RESULTS summary (sorted by vote_count desc) — handy for your UI
    @GetMapping("/results")
    public ResponseEntity<Map<String, Object>> getResults() {
        Map<String, Object> body = new HashMap<>();
        List<Candidate> list = votingService.getAllCandidates();
        int total = list.stream().mapToInt(Candidate::getVoteCount).sum();
        body.put("totalVotes", total);
        body.put("candidates", list);
        return ResponseEntity.ok(body);
    }

    // CREATE vote
    @PostMapping("/vote")
    public ResponseEntity<Map<String, Object>> submitVote(@RequestBody VoteRequest voteRequest) {
        String result = votingService.submitVote(voteRequest.getVoterEmail(), voteRequest.getCandidateName());
        return ok(result);
    }

    // READ all votes (for now, simple list)
    @GetMapping("/votes")
    public ResponseEntity<List<Vote>> getAllVotes() {
        return ResponseEntity.ok(votingService.getAllVotes());
    }

    // READ a single vote by email
    @GetMapping("/votes/{email}")
    public ResponseEntity<?> getVoteByEmail(@PathVariable("email") String email) {
        return votingService.getVoteByEmail(email)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "No vote found")));
    }

    // UPDATE vote (change candidate)
    @PutMapping("/vote")
    public ResponseEntity<Map<String, Object>> updateVote(@RequestBody VoteRequest voteRequest) {
        String result = votingService.updateVote(voteRequest.getVoterEmail(), voteRequest.getCandidateName());
        return ok(result);
    }

    // DELETE vote by email
    @DeleteMapping("/vote/{email}")
    public ResponseEntity<Map<String, Object>> deleteVote(@PathVariable("email") String email) {
        String result = votingService.deleteVote(email);
        return ok(result);
    }

    private ResponseEntity<Map<String, Object>> ok(String result) {
        boolean success = "SUCCESS".equals(result);
        return ResponseEntity.ok(Map.of(
                "success", success,
                "message", success ? "OK" : result
        ));
    }

    // Simple DTO shared by POST/PUT
    public static class VoteRequest {
        private String voterEmail;
        private String candidateName;
        public String getVoterEmail() { return voterEmail; }
        public void setVoterEmail(String voterEmail) { this.voterEmail = voterEmail; }
        public String getCandidateName() { return candidateName; }
        public void setCandidateName(String candidateName) { this.candidateName = candidateName; }
    }
}
