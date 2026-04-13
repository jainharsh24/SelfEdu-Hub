package com.harsh.mini_project.controller;

import com.harsh.mini_project.service.DashboardAnalyticsService;
import com.harsh.mini_project.service.DashboardHomeService;
import com.harsh.mini_project.service.RoadmapService;
import com.harsh.mini_project.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@Controller
public class DashboardController {
    private final RoadmapService roadmapService;
    private final UserService userService;
    private final DashboardAnalyticsService dashboardAnalyticsService;
    private final DashboardHomeService dashboardHomeService;

    public DashboardController(RoadmapService roadmapService,
                               UserService userService,
                               DashboardAnalyticsService dashboardAnalyticsService,
                               DashboardHomeService dashboardHomeService) {
        this.roadmapService = roadmapService;
        this.userService = userService;
        this.dashboardAnalyticsService = dashboardAnalyticsService;
        this.dashboardHomeService = dashboardHomeService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        var user = userService.getByUsername(principal.getName());
        var dashboardView = dashboardHomeService.getDashboardView(user);
        model.addAttribute("username", user.getUsername());
        model.addAttribute("activeRoadmaps", dashboardView.getActiveRoadmaps());
        model.addAttribute("recentTests", dashboardView.getRecentTests());
        model.addAttribute("totalRoadmaps", dashboardView.getTotalRoadmaps());
        model.addAttribute("avgScore", dashboardView.getAvgScore());
        return "dashboard";
    }

    @GetMapping("/my-learning")
    public String myLearning(Model model, Principal principal) {
        model.addAttribute("roadmaps", roadmapService.getAllRoadmaps(userService.getByUsername(principal.getName())));
        return "my-learning";
    }

    @GetMapping("/dashboard-main")
    public String analyticsDashboard(Model model, Principal principal) {
        model.addAttribute("userId", userService.getByUsername(principal.getName()).getId());
        return "dashboard-main";
    }

    @GetMapping("/dashboard/analytics/{userId}")
    @ResponseBody
    public ResponseEntity<?> analytics(@PathVariable Long userId, Principal principal) {
        var user = userService.getByUsername(principal.getName());
        if (!user.getId().equals(userId)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(dashboardAnalyticsService.getAnalyticsByUser(user));
    }
}
