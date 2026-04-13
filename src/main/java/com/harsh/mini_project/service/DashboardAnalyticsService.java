package com.harsh.mini_project.service;

import com.harsh.mini_project.dto.RoadmapAnalyticsResponse;
import com.harsh.mini_project.model.AppUser;
import com.harsh.mini_project.model.Roadmap;
import com.harsh.mini_project.model.Test;
import com.harsh.mini_project.model.TestStatus;
import com.harsh.mini_project.model.Topic;
import com.harsh.mini_project.repository.RoadmapRepository;
import com.harsh.mini_project.repository.TestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardAnalyticsService {
    private final RoadmapRepository roadmapRepository;
    private final TestRepository testRepository;

    public DashboardAnalyticsService(RoadmapRepository roadmapRepository, TestRepository testRepository) {
        this.roadmapRepository = roadmapRepository;
        this.testRepository = testRepository;
    }

    @Transactional(readOnly = true)
    public List<RoadmapAnalyticsResponse> getAnalyticsByUser(AppUser user) {
        return roadmapRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toAnalytics)
                .sorted(Comparator.comparingDouble(RoadmapAnalyticsResponse::getProgressPercent).reversed())
                .toList();
    }

    private RoadmapAnalyticsResponse toAnalytics(Roadmap roadmap) {
        List<Topic> topics = roadmap.getTopics();
        List<Test> tests = testRepository.findByUserIdAndRoadmapIdOrderByCreatedAtDesc(
                roadmap.getUser().getId(),
                roadmap.getId()
        );

        int totalWeeks = (int) topics.stream()
                .map(Topic::getWeekNumber)
                .distinct()
                .count();
        Map<Integer, List<Topic>> topicsByWeek = topics.stream()
                .collect(Collectors.groupingBy(Topic::getWeekNumber));
        long completedWeeks = topicsByWeek.values().stream()
                .filter(weekTopics -> weekTopics.stream().allMatch(Topic::isCompleted))
                .count();
        long testsAttempted = tests.stream()
                .filter(test -> test.getStatus() == TestStatus.COMPLETED)
                .count();
        long pendingTests = tests.stream()
                .filter(test -> test.getStatus() == TestStatus.PENDING)
                .count();
        List<Test> completedTests = tests.stream()
                .filter(test -> test.getStatus() == TestStatus.COMPLETED)
                .toList();
        double avgScore = round(completedTests.stream()
                .mapToDouble(Test::getScore)
                .average()
                .orElse(0.0));
        double highestScore = round(completedTests.stream()
                .mapToDouble(Test::getScore)
                .max()
                .orElse(0.0));
        int totalLinkClicks = topics.stream()
                .mapToInt(Topic::getLinkClickCount)
                .sum();
        int explanationViews = topics.stream()
                .mapToInt(Topic::getExplanationViewCount)
                .sum();
        long retestCount = tests.stream()
                .filter(test -> normalizeAttemptNumber(test) > 1)
                .count();
        double completionRate = totalWeeks == 0 ? 0.0 : round((completedWeeks * 100.0) / totalWeeks);
        double consistency = totalWeeks == 0 ? 0.0 : round(testsAttempted / (double) totalWeeks);

        RoadmapAnalyticsResponse response = new RoadmapAnalyticsResponse();
        response.setRoadmapId(roadmap.getId());
        response.setFieldName(roadmap.getFieldName());
        response.setTotalWeeks(totalWeeks);
        response.setCompletedWeeks(completedWeeks);
        response.setProgressPercent(roadmap.getProgressPercent());
        response.setTestsAttempted(testsAttempted);
        response.setPendingTests(pendingTests);
        response.setAvgScore(avgScore);
        response.setHighestScore(highestScore);
        response.setTotalLinkClicks(totalLinkClicks);
        response.setExplanationViews(explanationViews);
        response.setRetestCount(retestCount);
        response.setCompletionRate(completionRate);
        response.setConsistency(consistency);
        response.setPerformanceLevel(resolvePerformanceLevel(avgScore));
        return response;
    }

    private int normalizeAttemptNumber(Test test) {
        Integer attemptNumber = test.getAttemptNumber();
        return attemptNumber == null || attemptNumber <= 0 ? 1 : attemptNumber;
    }

    private String resolvePerformanceLevel(double avgScore) {
        if (avgScore > 75.0) {
            return "High";
        }
        if (avgScore >= 40.0) {
            return "Medium";
        }
        return "Low";
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
