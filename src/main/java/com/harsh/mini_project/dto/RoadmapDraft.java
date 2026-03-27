package com.harsh.mini_project.dto;

public class RoadmapDraft {
    private RoadmapRequest request;
    private RoadmapResponse response;
    private String rawJson;

    public RoadmapDraft(RoadmapRequest request, RoadmapResponse response, String rawJson) {
        this.request = request;
        this.response = response;
        this.rawJson = rawJson;
    }

    public RoadmapRequest getRequest() {
        return request;
    }

    public RoadmapResponse getResponse() {
        return response;
    }

    public String getRawJson() {
        return rawJson;
    }
}
