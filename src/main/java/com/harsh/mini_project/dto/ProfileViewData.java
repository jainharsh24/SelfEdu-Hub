package com.harsh.mini_project.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ProfileViewData {
    private final String username;
    private final LocalDateTime joinedDate;
    private final long totalTests;
    private final double avgScore;
    private final long completedTopics;
    private final double bestScore;
    private final long totalTestsCompleted;
    private final List<ProfileChartPoint> scoreTrend;
    private final List<ProfileChartPoint> weeklyActivity;

    public ProfileViewData(String username,
                           LocalDateTime joinedDate,
                           long totalTests,
                           double avgScore,
                           long completedTopics,
                           double bestScore,
                           long totalTestsCompleted,
                           List<ProfileChartPoint> scoreTrend,
                           List<ProfileChartPoint> weeklyActivity) {
        this.username = username;
        this.joinedDate = joinedDate;
        this.totalTests = totalTests;
        this.avgScore = avgScore;
        this.completedTopics = completedTopics;
        this.bestScore = bestScore;
        this.totalTestsCompleted = totalTestsCompleted;
        this.scoreTrend = scoreTrend;
        this.weeklyActivity = weeklyActivity;
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

    public double getAvgScore() {
        return avgScore;
    }

    public long getCompletedTopics() {
        return completedTopics;
    }

    public double getBestScore() {
        return bestScore;
    }

    public long getTotalTestsCompleted() {
        return totalTestsCompleted;
    }

    public List<ProfileChartPoint> getScoreTrend() {
        return scoreTrend;
    }

    public List<ProfileChartPoint> getWeeklyActivity() {
        return weeklyActivity;
    }
}
