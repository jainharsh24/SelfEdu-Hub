package com.harsh.mini_project.dto;

import java.time.LocalDateTime;

public class ProfileViewData {
    private final String username;
    private final LocalDateTime joinedDate;
    private final long totalTests;
    private final double bestScore;
    private final long milestonesCompleted;

    public ProfileViewData(String username,
                           LocalDateTime joinedDate,
                           long totalTests,
                           double bestScore,
                           long milestonesCompleted) {
        this.username = username;
        this.joinedDate = joinedDate;
        this.totalTests = totalTests;
        this.bestScore = bestScore;
        this.milestonesCompleted = milestonesCompleted;
    }

    public String getUsername() {
        return username;
    }

    public LocalDateTime getJoinedDate() {
        return joinedDate;
    }

    public long getTotalTests() {
        return totalTests;
    }

    public double getBestScore() {
        return bestScore;
    }

    public long getMilestonesCompleted() {
        return milestonesCompleted;
    }
}
