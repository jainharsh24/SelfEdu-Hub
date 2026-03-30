package com.harsh.mini_project.dto;

import java.util.List;

public class TestCreateRequest {
    private Long userId;
    private String roadmapName;
    private String topicName;
    private int weekNumber;
    private List<QuestionCreateRequest> questions;
    private boolean generateMcqs;
    private Integer questionCount;
    private String mcqTopic;

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

    public List<QuestionCreateRequest> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionCreateRequest> questions) {
        this.questions = questions;
    }

    public boolean isGenerateMcqs() {
        return generateMcqs;
    }

    public void setGenerateMcqs(boolean generateMcqs) {
        this.generateMcqs = generateMcqs;
    }

    public Integer getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }

    public String getMcqTopic() {
        return mcqTopic;
    }

    public void setMcqTopic(String mcqTopic) {
        this.mcqTopic = mcqTopic;
    }
}
