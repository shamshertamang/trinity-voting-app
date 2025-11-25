package com.javashams.springcontainer.controller;

import com.javashams.springcontainer.model.Candidate;
import com.javashams.springcontainer.model.Vote;
import com.javashams.springcontainer.service.VotingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;


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

    // configs and logs assignment part
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @RequestMapping(value = "/config", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> getConfig() {
        // Sorted for stable output (nice in UI/logs)
        Map<String, String> env = new TreeMap<>(System.getenv());
        try {
            String json = MAPPER.writeValueAsString(env);
            System.out.println(json); // <-- required: log the same JSON
        } catch (Exception e) {
            // Keep it simple; still return env
            System.out.println("{\"configLogError\":\"" + e.getMessage() + "\"}");
        }
        return env;
    }

    @RequestMapping(value = "/fib", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String generateFibonacci(@RequestParam("length") int length) {
        if (length <= 0) return "[]";
        List<Long> seq = new ArrayList<>(length);
        long a = 0, b = 1;
        for (int i = 0; i < length; i++) {
            seq.add(a);
            long next = a + b;
            a = b;
            b = next;
        }
        try {
            String json = MAPPER.writeValueAsString(seq);
            System.out.println(json);
            return json;
        } catch (Exception e) {
            System.out.println("[\"fibError: " + e.getMessage() + "\"]");
            return "[]";
        }
    }
}
