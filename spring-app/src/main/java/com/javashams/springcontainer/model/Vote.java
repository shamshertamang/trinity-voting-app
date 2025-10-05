package com.javashams.springcontainer.model;


import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "votes")
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "voter_email", nullable = false, unique = true)
    private String voterEmail;

    @Column(name = "candidate_name", nullable = false)
    private String candidateName;

    @Column(name = "vote_time", nullable = false)
    private LocalDateTime voteTime;

    // Default constructor
    public Vote() {
        this.voteTime = LocalDateTime.now();
    }

    // Constructor with parameters
    public Vote(String voterEmail, String candidateName) {
        this.voterEmail = voterEmail;
        this.candidateName = candidateName;
        this.voteTime = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public LocalDateTime getVoteTime() {
        return voteTime;
    }

    public void setVoteTime(LocalDateTime voteTime) {
        this.voteTime = voteTime;
    }
}