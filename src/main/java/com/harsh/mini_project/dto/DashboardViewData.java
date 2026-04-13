package com.harsh.mini_project.dto;

import com.harsh.mini_project.model.Roadmap;

import java.util.List;

public class DashboardViewData {
    private final List<Roadmap> activeRoadmaps;
    private final List<DashboardRecentTestItem> recentTests;
    private final long totalRoadmaps;
    private final double avgScore;

    public DashboardViewData(List<Roadmap> activeRoadmaps,
                             List<DashboardRecentTestItem> recentTests,
                             long totalRoadmaps,
                             double avgScore) {
        this.activeRoadmaps = activeRoadmaps;
        this.recentTests = recentTests;
        this.totalRoadmaps = totalRoadmaps;
        this.avgScore = avgScore;
    }

    public List<Roadmap> getActiveRoadmaps() {
        return activeRoadmaps;
    }

    public List<DashboardRecentTestItem> getRecentTests() {
        return recentTests;
    }

    public long getTotalRoadmaps() {
        return totalRoadmaps;
    }

    public double getAvgScore() {
        return avgScore;
    }
}
