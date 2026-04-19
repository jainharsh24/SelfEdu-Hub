package com.harsh.mini_project.service;

import com.harsh.mini_project.dto.ProfileViewData;
import com.harsh.mini_project.model.AppUser;
import com.harsh.mini_project.model.Test;
import com.harsh.mini_project.model.TestStatus;
import com.harsh.mini_project.model.Topic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProfileService {
    private final RoadmapService roadmapService;
    private final TestService testService;

    public ProfileService(RoadmapService roadmapService, TestService testService) {
        this.roadmapService = roadmapService;
        this.testService = testService;
    }

    @Transactional(readOnly = true)
    public ProfileViewData getProfile(AppUser user) {
        List<Test> tests = testService.getAllByUser(user.getId());
        List<Test> completedTests = tests.stream()
                .filter(test -> test.getStatus() == TestStatus.COMPLETED)
                .toList();
        List<Topic> topics = roadmapService.getAllRoadmaps(user).stream()
                .flatMap(roadmap -> roadmap.getTopics().stream())
                .toList();

        long completedTopics = topics.stream()
                .filter(Topic::isCompleted)
                .count();

        double bestScore = completedTests.stream()
                .mapToDouble(Test::getScore)
                .max()
                .orElse(0.0);

        return new ProfileViewData(
                user.getUsername(),
                user.getCreatedAt(),
                tests.size(),
                round(bestScore),
                completedTopics
        );
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
