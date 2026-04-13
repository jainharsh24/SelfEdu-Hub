package com.harsh.mini_project.controller;

import com.harsh.mini_project.dto.TestCreateRequest;
import com.harsh.mini_project.dto.TestSubmitRequest;
import com.harsh.mini_project.service.TestService;
import com.harsh.mini_project.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/tests")
public class TestController {
    private final TestService testService;
    private final UserService userService;

    public TestController(TestService testService, UserService userService) {
        this.testService = testService;
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTest(@RequestBody TestCreateRequest request) {
        try {
            return ResponseEntity.ok(testService.createTest(request));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getTestsByUser(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(testService.getTestsByUser(userId));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/{testId}")
    public ResponseEntity<?> getTest(@PathVariable Long testId) {
        try {
            return ResponseEntity.ok(testService.getTest(testId));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/{testId}/take")
    public ResponseEntity<?> getTestForTaking(@PathVariable Long testId, Principal principal) {
        try {
            Long userId = userService.getByUsername(principal.getName()).getId();
            return ResponseEntity.ok(testService.getTestForTaking(testId, userId));
        } catch (IllegalArgumentException ex) {
            HttpStatus status = ex.getMessage() != null && ex.getMessage().toLowerCase().contains("not found")
                    ? HttpStatus.NOT_FOUND
                    : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(Map.of("error", ex.getMessage()));
        } catch (IllegalStateException ex) {
            HttpStatus status = ex.getMessage() != null && ex.getMessage().toLowerCase().contains("completed")
                    ? HttpStatus.CONFLICT
                    : HttpStatus.INTERNAL_SERVER_ERROR;
            return ResponseEntity.status(status).body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/{testId}/review")
    public ResponseEntity<?> getTestForReview(@PathVariable Long testId, Principal principal) {
        try {
            Long userId = userService.getByUsername(principal.getName()).getId();
            return ResponseEntity.ok(testService.getTestForReview(testId, userId));
        } catch (IllegalArgumentException ex) {
            HttpStatus status = ex.getMessage() != null && ex.getMessage().toLowerCase().contains("not found")
                    ? HttpStatus.NOT_FOUND
                    : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/{testId}/submit")
    public ResponseEntity<?> submitTest(@PathVariable Long testId, @RequestBody(required = false) TestSubmitRequest request) {
        try {
            return ResponseEntity.ok(testService.submitAnswers(testId, request));
        } catch (IllegalArgumentException ex) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("not found")) {
                status = HttpStatus.NOT_FOUND;
            }
            return ResponseEntity.status(status).body(Map.of("error", ex.getMessage()));
        } catch (IllegalStateException ex) {
            HttpStatus status = ex.getMessage() != null && ex.getMessage().toLowerCase().contains("completed")
                    ? HttpStatus.CONFLICT
                    : HttpStatus.INTERNAL_SERVER_ERROR;
            return ResponseEntity.status(status).body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/{testId}/save")
    public ResponseEntity<?> saveAnswers(@PathVariable Long testId, @RequestBody(required = false) TestSubmitRequest request, Principal principal) {
        try {
            Long userId = userService.getByUsername(principal.getName()).getId();
            return ResponseEntity.ok(testService.saveAnswers(testId, request, userId));
        } catch (IllegalArgumentException ex) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("not found")) {
                status = HttpStatus.NOT_FOUND;
            }
            return ResponseEntity.status(status).body(Map.of("error", ex.getMessage()));
        } catch (IllegalStateException ex) {
            HttpStatus status = ex.getMessage() != null && ex.getMessage().toLowerCase().contains("completed")
                    ? HttpStatus.CONFLICT
                    : HttpStatus.INTERNAL_SERVER_ERROR;
            return ResponseEntity.status(status).body(Map.of("error", ex.getMessage()));
        }
    }
}
