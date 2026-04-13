package com.harsh.mini_project.controller;

import com.harsh.mini_project.model.Level;
import com.harsh.mini_project.service.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class SettingsController {
    private final UserService userService;

    public SettingsController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/settings")
    public String settingsPage(Model model, Principal principal) {
        model.addAttribute("settingsUser", userService.getByUsername(principal.getName()));
        return "settings";
    }

    @PostMapping("/settings/profile")
    public String updateProfile(@RequestParam(required = false) String username,
                                @RequestParam(required = false) Level preferredLevel,
                                @RequestParam(required = false) String preferredDomain,
                                Principal principal,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            var updatedUser = userService.updateSettings(principal.getName(), username, preferredLevel, preferredDomain);
            refreshAuthentication(updatedUser.getUsername());
        } catch (IllegalArgumentException ex) {
            model.addAttribute("profileError", ex.getMessage());
            model.addAttribute("settingsUser", userService.getByUsername(principal.getName()));
            return "settings";
        }
        redirectAttributes.addFlashAttribute("profileSuccess", "Settings updated successfully.");
        return "redirect:/settings";
    }

    @PostMapping("/settings/password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Principal principal,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("passwordError", "New password and confirm password must match.");
            model.addAttribute("settingsUser", userService.getByUsername(principal.getName()));
            return "settings";
        }
        try {
            userService.changePassword(principal.getName(), currentPassword, newPassword);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("passwordError", ex.getMessage());
            model.addAttribute("settingsUser", userService.getByUsername(principal.getName()));
            return "settings";
        }
        redirectAttributes.addFlashAttribute("passwordSuccess", "Password updated successfully.");
        return "redirect:/settings";
    }

    private void refreshAuthentication(String username) {
        var userDetails = userService.loadUserByUsername(username);
        var authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                userDetails.getPassword(),
                userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
