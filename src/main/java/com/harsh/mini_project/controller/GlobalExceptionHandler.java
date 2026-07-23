package com.harsh.mini_project.controller;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 1. Catch missing static files (CSS, JS, images, missing 404 routes in Spring Boot 3)
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoResourceFound(NoResourceFoundException ex, Model model) {
        model.addAttribute("errorTitle", "404 - File or Page Not Found");
        model.addAttribute("errorMessage", "The requested resource or page was not found.");
        return "error"; // Or "error/404" if you created a custom 404 page
    }

    // 2. Catch custom database/entity not found errors
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(ResourceNotFoundException ex, Model model) {
        model.addAttribute("errorTitle", "404 - Not Found");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    // 3. Fallback for actual server/code crashes (500 errors)
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneralException(Exception ex, Model model) {
        model.addAttribute("errorTitle", "500 - Something Went Wrong");
        model.addAttribute("errorMessage", "An unexpected error occurred: " + ex.getMessage());
        return "error";
    }
}