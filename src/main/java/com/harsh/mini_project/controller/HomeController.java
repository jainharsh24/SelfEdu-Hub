package com.harsh.mini_project.controller;

import com.harsh.mini_project.dto.RoadmapDraft;
import com.harsh.mini_project.dto.RoadmapRequest;
import com.harsh.mini_project.model.Level;
import com.harsh.mini_project.service.PredefinedRoadmapCatalog;
import com.harsh.mini_project.service.RoadmapGenerationService;
import com.harsh.mini_project.service.RoadmapService;
import com.harsh.mini_project.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.regex.Pattern;

@Controller
public class HomeController {
    private final RoadmapGenerationService roadmapGenerationService;
    private final PredefinedRoadmapCatalog predefinedRoadmapCatalog;
    private final RoadmapService roadmapService;
    private final UserService userService;
    private static final int MAX_SYLLABUS_WORDS = 1500;
    private static final Pattern WORD_SPLIT = Pattern.compile("\\s+");

    public HomeController(RoadmapGenerationService roadmapGenerationService,
                          PredefinedRoadmapCatalog predefinedRoadmapCatalog,
                          RoadmapService roadmapService,
                          UserService userService) {
        this.roadmapGenerationService = roadmapGenerationService;
        this.predefinedRoadmapCatalog = predefinedRoadmapCatalog;
        this.roadmapService = roadmapService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/generate-roadmap")
    public String roadmapBuilder(Model model) {
        model.addAttribute("roadmapRequest", new RoadmapRequest());
        model.addAttribute("levels", Level.values());
        model.addAttribute("subjectSuggestions", predefinedRoadmapCatalog.getAvailableSubjects());
        return "roadmap-builder";
    }

    @PostMapping("/generate")
    public String generate(@Valid @ModelAttribute("roadmapRequest") RoadmapRequest request,
                           BindingResult bindingResult,
                           Model model,
                           HttpSession session,
                           Principal principal,
                           RedirectAttributes redirectAttributes) {
        int syllabusWordCount = countWords(request.getSyllabusTopics());
        if (syllabusWordCount > MAX_SYLLABUS_WORDS) {
            bindingResult.rejectValue("syllabusTopics", "syllabusTopics.tooLong",
                    "Syllabus topics must be within 1500 words.");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("levels", Level.values());
            model.addAttribute("subjectSuggestions", predefinedRoadmapCatalog.getAvailableSubjects());
            return "roadmap-builder";
        }
        if (principal != null) {
            var user = userService.getByUsername(principal.getName());
            var existing = roadmapService.findExistingRoadmapForRequest(request, user);
            if (existing.isPresent()) {
                redirectAttributes.addFlashAttribute("infoMessage",
                        "Same roadmap inputs already exist. Opened your existing roadmap.");
                return "redirect:/roadmaps/" + existing.get().getId();
            }
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
