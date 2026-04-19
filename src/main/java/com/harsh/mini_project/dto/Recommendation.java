package com.harsh.mini_project.dto;

public class Recommendation {
    private final String message;
    private final Long roadmapId;
    private final String actionLabel;

    public Recommendation(String message, Long roadmapId, String actionLabel) {
        this.message = message;
        this.roadmapId = roadmapId;
        this.actionLabel = actionLabel;
    }

    public String getMessage() {
        return message;
    }

    public Long getRoadmapId() {
        return roadmapId;
    }

    public String getActionLabel() {
        return actionLabel;
    }
}
