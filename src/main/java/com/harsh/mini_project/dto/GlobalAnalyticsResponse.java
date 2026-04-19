package com.harsh.mini_project.dto;

import java.util.List;

public class GlobalAnalyticsResponse {
    private long totalRoadmaps;
    private long totalTests;
    private long weeksCompleted;
    private double averageScore;
    private double bestScore;
    private String bestRoadmapName;
    private double bestRoadmapScore;
    private String strongTopicName;
    private double strongTopicScore;
    private String weakTopicName;
    private double weakTopicScore;
    private List<ProfileChartPoint> scoreTrend;
    private List<ProfileChartPoint> weeklyActivity;
    private List<ProfileChartPoint> overallProgressTrend;
    private List<DashboardRecentTestItem> recentTests;
    private List<RoadmapAnalyticsResponse> roadmaps;

    public long getTotalRoadmaps() {
        return totalRoadmaps;
    }

    public void setTotalRoadmaps(long totalRoadmaps) {
        this.totalRoadmaps = totalRoadmaps;
    }

    public long getTotalTests() {
        return totalTests;
    }

    public void setTotalTests(long totalTests) {
        this.totalTests = totalTests;
    }

    public long getWeeksCompleted() {
        return weeksCompleted;
    }

    public void setWeeksCompleted(long weeksCompleted) {
        this.weeksCompleted = weeksCompleted;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }

    public double getBestScore() {
        return bestScore;
    }

    public void setBestScore(double bestScore) {
        this.bestScore = bestScore;
    }

    public List<ProfileChartPoint> getScoreTrend() {
        return scoreTrend;
    }

    public void setScoreTrend(List<ProfileChartPoint> scoreTrend) {
        this.scoreTrend = scoreTrend;
    }

    public List<ProfileChartPoint> getWeeklyActivity() {
        return weeklyActivity;
    }

    public void setWeeklyActivity(List<ProfileChartPoint> weeklyActivity) {
        this.weeklyActivity = weeklyActivity;
    }

    public List<ProfileChartPoint> getOverallProgressTrend() {
        return overallProgressTrend;
    }

    public void setOverallProgressTrend(List<ProfileChartPoint> overallProgressTrend) {
        this.overallProgressTrend = overallProgressTrend;
    }

    public List<DashboardRecentTestItem> getRecentTests() {
        return recentTests;
    }

    public void setRecentTests(List<DashboardRecentTestItem> recentTests) {
        this.recentTests = recentTests;
    }

    public List<RoadmapAnalyticsResponse> getRoadmaps() {
        return roadmaps;
    }

    public void setRoadmaps(List<RoadmapAnalyticsResponse> roadmaps) {
        this.roadmaps = roadmaps;
    }

    public String getBestRoadmapName() {
        return bestRoadmapName;
    }

    public void setBestRoadmapName(String bestRoadmapName) {
        this.bestRoadmapName = bestRoadmapName;
    }

    public double getBestRoadmapScore() {
        return bestRoadmapScore;
    }

    public void setBestRoadmapScore(double bestRoadmapScore) {
        this.bestRoadmapScore = bestRoadmapScore;
    }

    public String getStrongTopicName() {
        return strongTopicName;
    }

    public void setStrongTopicName(String strongTopicName) {
        this.strongTopicName = strongTopicName;
    }

    public double getStrongTopicScore() {
        return strongTopicScore;
    }

    public void setStrongTopicScore(double strongTopicScore) {
        this.strongTopicScore = strongTopicScore;
    }

    public String getWeakTopicName() {
        return weakTopicName;
    }

    public void setWeakTopicName(String weakTopicName) {
        this.weakTopicName = weakTopicName;
    }

    public double getWeakTopicScore() {
        return weakTopicScore;
    }

    public void setWeakTopicScore(double weakTopicScore) {
        this.weakTopicScore = weakTopicScore;
    }
}
