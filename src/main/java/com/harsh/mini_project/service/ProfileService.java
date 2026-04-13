package com.harsh.mini_project.service;

import com.harsh.mini_project.dto.ProfileChartPoint;
import com.harsh.mini_project.dto.ProfileViewData;
import com.harsh.mini_project.model.AppUser;
import com.harsh.mini_project.model.Test;
import com.harsh.mini_project.model.TestStatus;
import com.harsh.mini_project.model.Topic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProfileService {
    private static final DateTimeFormatter SCORE_TREND_FORMAT = DateTimeFormatter.ofPattern("dd MMM");

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

        double avgScore = completedTests.stream()
                .mapToDouble(Test::getScore)
                .average()
                .orElse(0.0);

        double bestScore = completedTests.stream()
                .mapToDouble(Test::getScore)
                .max()
                .orElse(0.0);

        List<ProfileChartPoint> scoreTrend = completedTests.stream()
                .sorted(Comparator.comparing(Test::getCreatedAt))
                .map(test -> new ProfileChartPoint(
                        test.getCreatedAt().format(SCORE_TREND_FORMAT),
                        round(test.getScore())
                ))
                .toList();

        Map<Integer, Long> completedTopicsByWeek = topics.stream()
                .filter(Topic::isCompleted)
                .collect(Collectors.groupingBy(Topic::getWeekNumber, Collectors.counting()));

        List<ProfileChartPoint> weeklyActivity = completedTopicsByWeek.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new ProfileChartPoint("Week " + entry.getKey(), entry.getValue()))
                .toList();

        return new ProfileViewData(
                user.getUsername(),
                user.getCreatedAt(),
                tests.size(),
                round(avgScore),
                completedTopics,
                round(bestScore),
                completedTests.size(),
                scoreTrend,
                weeklyActivity
        );
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
