package com.harsh.mini_project.dto;

public class DashboardRecentTestItem {
    private final Long roadmapId;
    private final String roadmapName;
    private final String topicName;
    private final int weekNumber;
    private final double score;

    public DashboardRecentTestItem(Long roadmapId, String roadmapName, String topicName, int weekNumber, double score) {
        this.roadmapId = roadmapId;
        this.roadmapName = roadmapName;
        this.topicName = topicName;
        this.weekNumber = weekNumber;
        this.score = score;
    }

    public Long getRoadmapId() {
        return roadmapId;
    }

    public String getRoadmapName() {
        return roadmapName;
    }

    public String getTopicName() {
        return topicName;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public double getScore() {
        return score;
    }
}
