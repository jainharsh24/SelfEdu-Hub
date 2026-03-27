package com.harsh.mini_project.controller;

import com.harsh.mini_project.service.RoadmapService;
import com.harsh.mini_project.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class DashboardController {
    private final RoadmapService roadmapService;
    private final UserService userService;

    public DashboardController(RoadmapService roadmapService, UserService userService) {
        this.roadmapService = roadmapService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("roadmaps", roadmapService.getAllRoadmaps(userService.getByUsername(principal.getName())));
        return "dashboard";
    }
}
