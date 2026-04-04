package com.harsh.mini_project.controller;

import com.harsh.mini_project.dto.RoadmapDraft;
import com.harsh.mini_project.dto.RoadmapRequest;
import com.harsh.mini_project.model.Level;
import com.harsh.mini_project.service.PredefinedRoadmapCatalog;
import com.harsh.mini_project.service.RoadmapGenerationService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.regex.Pattern;

@Controller
public class HomeController {
    private final RoadmapGenerationService roadmapGenerationService;
    private final PredefinedRoadmapCatalog predefinedRoadmapCatalog;
    private static final int MAX_SYLLABUS_WORDS = 1500;
    private static final Pattern WORD_SPLIT = Pattern.compile("\\s+");

    public HomeController(RoadmapGenerationService roadmapGenerationService,
                          PredefinedRoadmapCatalog predefinedRoadmapCatalog) {
        this.roadmapGenerationService = roadmapGenerationService;
        this.predefinedRoadmapCatalog = predefinedRoadmapCatalog;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("roadmapRequest", new RoadmapRequest());
        model.addAttribute("levels", Level.values());
        model.addAttribute("subjectSuggestions", predefinedRoadmapCatalog.getAvailableSubjects());
        return "index";
    }

    @PostMapping("/generate")
    public String generate(@Valid @ModelAttribute("roadmapRequest") RoadmapRequest request,
                           BindingResult bindingResult,
                           Model model,
                           HttpSession session) {
        int syllabusWordCount = countWords(request.getSyllabusTopics());
        if (syllabusWordCount > MAX_SYLLABUS_WORDS) {
            bindingResult.rejectValue("syllabusTopics", "syllabusTopics.tooLong",
                    "Syllabus topics must be within 1500 words.");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("levels", Level.values());
            model.addAttribute("subjectSuggestions", predefinedRoadmapCatalog.getAvailableSubjects());
            return "index";
        }
        RoadmapDraft draft = roadmapGenerationService.generateDraft(request);
        session.setAttribute("draftRoadmap", draft);
        model.addAttribute("draft", draft);
        return "roadmap-preview";
    }

    private int countWords(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        return WORD_SPLIT.split(text.trim()).length;
    }
}
