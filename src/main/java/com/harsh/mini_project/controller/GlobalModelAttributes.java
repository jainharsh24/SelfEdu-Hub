package com.harsh.mini_project.controller;

import com.harsh.mini_project.dto.RoadmapRequest;
import com.harsh.mini_project.model.Level;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("roadmapRequest")
    public RoadmapRequest roadmapRequest() {
        return new RoadmapRequest();
    }

    @ModelAttribute("levels")
    public Level[] levels() {
        return Level.values();
    }
}
