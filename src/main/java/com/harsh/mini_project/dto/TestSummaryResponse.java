package com.harsh.mini_project.dto;

import com.harsh.mini_project.model.TestStatus;

import java.time.LocalDateTime;

public class TestSummaryResponse {
    private Long id;
    private Long userId;
    private String roadmapName;
    private String topicName;
    private int weekNumber;
    private TestStatus status;
    private double score;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRoadmapName() {
        return roadmapName;
    }

    public void setRoadmapName(String roadmapName) {
        this.roadmapName = roadmapName;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
    }

    public TestStatus getStatus() {
        return status;
    }

    public void setStatus(TestStatus status) {
        this.status = status;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
