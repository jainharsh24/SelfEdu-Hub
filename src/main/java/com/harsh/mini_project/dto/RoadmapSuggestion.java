package com.harsh.mini_project.dto;

public class RoadmapSuggestion {
    private final int priority;
    private final String message;
    private final String actionLabel;
    private final String actionUrl;
    private final String actionType;
    private final Integer actionWeek;

    public RoadmapSuggestion(int priority, String message, String actionLabel, String actionUrl, String actionType, Integer actionWeek) {
        this.priority = priority;
        this.message = message;
        this.actionLabel = actionLabel;
        this.actionUrl = actionUrl;
        this.actionType = actionType;
        this.actionWeek = actionWeek;
    }

    public int getPriority() {
        return priority;
    }

    public String getMessage() {
        return message;
    }

    public String getActionLabel() {
        return actionLabel;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public String getActionType() {
        return actionType;
    }

    public Integer getActionWeek() {
        return actionWeek;
    }
}
