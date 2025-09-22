package com.javashams.springcontainer.model;

import jakarta.persistence.*;

@Entity
@Table(name = "candidates")
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "vote_count", nullable = false)
    private Integer voteCount;

    // Default constructor
    public Candidate() {
        this.voteCount = 0;
    }

    // Constructor with name
    public Candidate(String name) {
        this.name = name;
        this.voteCount = 0;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    public void incrementVoteCount() {
        this.voteCount++;
    }
}