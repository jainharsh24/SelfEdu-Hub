package com.harsh.mini_project.dto;

public class RoadmapAnalyticsResponse {
    private Long roadmapId;
    private String fieldName;
    private int totalWeeks;
    private long completedWeeks;
    private double progressPercent;
    private long testsAttempted;
    private long pendingTests;
    private double avgScore;
    private double highestScore;
    private int totalLinkClicks;
    private int explanationViews;
    private long retestCount;
    private double completionRate;
    private double consistency;
    private String performanceLevel;

    public Long getRoadmapId() {
        return roadmapId;
    }

    public void setRoadmapId(Long roadmapId) {
        this.roadmapId = roadmapId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public int getTotalWeeks() {
        return totalWeeks;
    }

    public void setTotalWeeks(int totalWeeks) {
        this.totalWeeks = totalWeeks;
    }

    public long getCompletedWeeks() {
        return completedWeeks;
    }

    public void setCompletedWeeks(long completedWeeks) {
        this.completedWeeks = completedWeeks;
    }

    public double getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(double progressPercent) {
        this.progressPercent = progressPercent;
    }

    public long getTestsAttempted() {
        return testsAttempted;
    }

    public void setTestsAttempted(long testsAttempted) {
        this.testsAttempted = testsAttempted;
    }

    public long getPendingTests() {
        return pendingTests;
    }

    public void setPendingTests(long pendingTests) {
        this.pendingTests = pendingTests;
    }

    public double getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(double avgScore) {
        this.avgScore = avgScore;
    }

    public double getHighestScore() {
        return highestScore;
    }

    public void setHighestScore(double highestScore) {
        this.highestScore = highestScore;
    }

    public int getTotalLinkClicks() {
        return totalLinkClicks;
    }

    public void setTotalLinkClicks(int totalLinkClicks) {
        this.totalLinkClicks = totalLinkClicks;
    }

    public int getExplanationViews() {
        return explanationViews;
    }

    public void setExplanationViews(int explanationViews) {
        this.explanationViews = explanationViews;
    }

    public long getRetestCount() {
        return retestCount;
    }

    public void setRetestCount(long retestCount) {
        this.retestCount = retestCount;
    }

    public double getCompletionRate() {
        return completionRate;
    }

    public void setCompletionRate(double completionRate) {
        this.completionRate = completionRate;
    }

    public double getConsistency() {
        return consistency;
    }

    public void setConsistency(double consistency) {
        this.consistency = consistency;
    }

    public String getPerformanceLevel() {
        return performanceLevel;
    }

    public void setPerformanceLevel(String performanceLevel) {
        this.performanceLevel = performanceLevel;
    }
}
