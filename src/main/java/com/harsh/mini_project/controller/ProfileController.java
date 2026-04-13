package com.harsh.mini_project.controller;

import com.harsh.mini_project.service.ProfileService;
import com.harsh.mini_project.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class ProfileController {
    private final UserService userService;
    private final ProfileService profileService;

    public ProfileController(UserService userService, ProfileService profileService) {
        this.userService = userService;
        this.profileService = profileService;
    }

    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        var user = userService.getByUsername(principal.getName());
        model.addAttribute("profile", profileService.getProfile(user));
        return "profile";
    }
}
