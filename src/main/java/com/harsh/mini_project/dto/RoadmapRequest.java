package com.harsh.mini_project.dto;

import com.harsh.mini_project.model.Level;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RoadmapRequest {

    @NotBlank(message = "Field is required")
    private String field;

    @NotNull(message = "Level is required")
    private Level level;

    @Min(value = 1, message = "Duration must be at least 1 month")
    @Max(value = 24, message = "Duration must be at most 24 months")
    private int durationMonths;

    @Size(max = 12000, message = "Syllabus topics are too long")
    private String syllabusTopics;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public int getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(int durationMonths) {
        this.durationMonths = durationMonths;
    }

    public String getSyllabusTopics() {
        return syllabusTopics;
    }

    public void setSyllabusTopics(String syllabusTopics) {
        this.syllabusTopics = syllabusTopics;
    }
}
