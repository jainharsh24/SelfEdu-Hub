package com.harsh.mini_project.controller;

import com.harsh.mini_project.service.TestService;
import com.harsh.mini_project.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class TestPageController {
    private final TestService testService;
    private final UserService userService;

    public TestPageController(TestService testService, UserService userService) {
        this.testService = testService;
        this.userService = userService;
    }

    @GetMapping("/dashboard-1")
    public String testPage(@RequestParam Long testId, Model model, Principal principal) {
        Long userId = userService.getByUsername(principal.getName()).getId();
        model.addAttribute("userId", userId);
        model.addAttribute("testId", testId);
        return "dashboard-1";
    }

    @GetMapping("/dashboard-3")
    public String reviewPage(@RequestParam Long testId, Model model, Principal principal) {
        Long userId = userService.getByUsername(principal.getName()).getId();
        model.addAttribute("userId", userId);
        model.addAttribute("testId", testId);
        return "dashboard-3";
    }

    @GetMapping("/dashboard-2")
    public String testLogs(Model model, Principal principal) {
        Long userId = userService.getByUsername(principal.getName()).getId();
        model.addAttribute("tests", testService.getTestsByUser(userId));
        return "dashboard-2";
    }
}
