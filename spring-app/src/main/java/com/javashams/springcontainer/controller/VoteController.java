package com.javashams.springcontainer.controller;

import com.javashams.springcontainer.model.Candidate;
import com.javashams.springcontainer.service.VotingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class VoteController {

    @Autowired
    private VotingService votingService;

    @GetMapping("/candidates")
    public ResponseEntity<List<Candidate>> getAllCandidates() {
        List<Candidate> candidates = votingService.getAllCandidates();
        return ResponseEntity.ok(candidates);
    }

    @PostMapping("/vote")
    public ResponseEntity<Map<String, Object>> submitVote(@RequestBody VoteRequest voteRequest) {
        Map<String, Object> response = new HashMap<>();

        try {
            String result = votingService.submitVote(voteRequest.getVoterEmail(), voteRequest.getCandidateName());

            if ("SUCCESS".equals(result)) {
                response.put("success", true);
                response.put("message", "Vote submitted successfully!");
            } else {
                response.put("success", false);
                response.put("message", result);
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "An error occurred while processing your vote.");
        }

        return ResponseEntity.ok(response);
    }

    // Inner class for request body
    public static class VoteRequest {
        private String voterEmail;
        private String candidateName;

        // Default constructor
        public VoteRequest() {}

        // Getters and Setters
        public String getVoterEmail() {
            return voterEmail;
        }

        public void setVoterEmail(String voterEmail) {
            this.voterEmail = voterEmail;
        }

        public String getCandidateName() {
            return candidateName;
        }

        public void setCandidateName(String candidateName) {
            this.candidateName = candidateName;
        }
    }
}