package com.harsh.mini_project.dto;

import java.util.List;

public class TestSubmitRequest {
    private List<QuestionAnswerRequest> answers;

    public List<QuestionAnswerRequest> getAnswers() {
        return answers;
    }

    public void setAnswers(List<QuestionAnswerRequest> answers) {
        this.answers = answers;
    }
}
