package com.harsh.mini_project.dto;

import com.harsh.mini_project.model.Roadmap;

import java.util.List;

public class DashboardViewData {
    private final List<Roadmap> activeRoadmaps;
    private final long totalRoadmaps;
    private final double avgScore;
    private final String nextStepLabel;
    private final Recommendation recommendation;

    public DashboardViewData(List<Roadmap> activeRoadmaps,
                             long totalRoadmaps,
                             double avgScore,
                             String nextStepLabel,
                             Recommendation recommendation) {
        this.activeRoadmaps = activeRoadmaps;
        this.totalRoadmaps = totalRoadmaps;
        this.avgScore = avgScore;
        this.nextStepLabel = nextStepLabel;
        this.recommendation = recommendation;
    }

    public List<Roadmap> getActiveRoadmaps() {
        return activeRoadmaps;
    }

    public long getTotalRoadmaps() {
        return totalRoadmaps;
    }

    public double getAvgScore() {
        return avgScore;
    }

    public String getNextStepLabel() {
        return nextStepLabel;
    }

    public Recommendation getRecommendation() {
        return recommendation;
    }
}
