package com.harsh.mini_project.dto;

import java.util.ArrayList;
import java.util.List;

public class RoadmapResponse {
    private List<RoadmapWeek> roadmap = new ArrayList<>();

    public List<RoadmapWeek> getRoadmap() {
        return roadmap;
    }

    public void setRoadmap(List<RoadmapWeek> roadmap) {
        this.roadmap = roadmap;
    }
}
