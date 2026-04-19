package com.harsh.mini_project.service;

import com.harsh.mini_project.dto.DashboardRecentTestItem;
import com.harsh.mini_project.dto.GlobalAnalyticsResponse;
import com.harsh.mini_project.dto.ProfileChartPoint;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;

@Service
public class DashboardAnalyticsService {
    private static final DateTimeFormatter SCORE_TREND_FORMAT = DateTimeFormatter.ofPattern("dd MMM");

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

    @Transactional(readOnly = true)
    public GlobalAnalyticsResponse getGlobalAnalyticsByUser(AppUser user) {
        List<RoadmapAnalyticsResponse> roadmapAnalytics = getAnalyticsByUser(user);
        List<Test> allTests = testRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        List<Test> completedTests = allTests.stream()
                .filter(test -> test.getStatus() == TestStatus.COMPLETED)
                .toList();
        List<Roadmap> allRoadmaps = roadmapRepository.findByUserOrderByCreatedAtDesc(user);
        List<Topic> allTopics = allRoadmaps.stream()
                .flatMap(roadmap -> roadmap.getTopics().stream())
                .toList();

        List<DashboardRecentTestItem> recentTests = completedTests.stream()
                .limit(3)
                .map(test -> new DashboardRecentTestItem(
                        test.getRoadmapId(),
                        test.getRoadmapName(),
                        test.getTopicName(),
                        test.getWeekNumber(),
                        round(test.getScore())
                ))
                .toList();

        List<ProfileChartPoint> scoreTrend = completedTests.stream()
                .sorted(Comparator.comparing(Test::getCreatedAt))
                .map(test -> new ProfileChartPoint(
                        test.getCreatedAt().format(SCORE_TREND_FORMAT),
                        round(test.getScore())
                ))
                .toList();

        Map<Integer, Long> completedTopicsByWeek = allTopics.stream()
                .filter(Topic::isCompleted)
                .collect(Collectors.groupingBy(Topic::getWeekNumber, Collectors.counting()));

        List<ProfileChartPoint> weeklyActivity = completedTopicsByWeek.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new ProfileChartPoint("Week " + entry.getKey(), entry.getValue()))
                .toList();

        List<ProfileChartPoint> overallProgressTrend = allRoadmaps.stream()
                .sorted(Comparator.comparing(Roadmap::getCreatedAt))
                .map(roadmap -> new ProfileChartPoint(
                        roadmap.getCreatedAt().format(SCORE_TREND_FORMAT),
                        round(roadmap.getProgressPercent())
                ))
                .toList();

        double avgScore = round(completedTests.stream()
                .mapToDouble(Test::getScore)
                .average()
                .orElse(0.0));
        double bestScore = round(completedTests.stream()
                .mapToDouble(Test::getScore)
                .max()
                .orElse(0.0));

        Map<String, Double> roadmapAvgScore = completedTests.stream()
                .collect(Collectors.groupingBy(
                        Test::getRoadmapName,
                        LinkedHashMap::new,
                        Collectors.averagingDouble(Test::getScore)
                ));
        Map<String, Double> topicAvgScore = completedTests.stream()
                .collect(Collectors.groupingBy(
                        Test::getTopicName,
                        LinkedHashMap::new,
                        Collectors.averagingDouble(Test::getScore)
                ));

        Map.Entry<String, Double> bestRoadmap = roadmapAvgScore.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);
        Map.Entry<String, Double> strongTopic = topicAvgScore.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);
        Map.Entry<String, Double> weakTopic = topicAvgScore.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .orElse(null);

        GlobalAnalyticsResponse response = new GlobalAnalyticsResponse();
        response.setTotalRoadmaps(roadmapAnalytics.size());
        response.setTotalTests(allTests.size());
        response.setWeeksCompleted(roadmapAnalytics.stream().mapToLong(RoadmapAnalyticsResponse::getCompletedWeeks).sum());
        response.setAverageScore(avgScore);
        response.setBestScore(bestScore);
        response.setScoreTrend(scoreTrend);
        response.setWeeklyActivity(weeklyActivity);
        response.setOverallProgressTrend(overallProgressTrend);
        response.setRecentTests(recentTests);
        response.setRoadmaps(roadmapAnalytics);
        response.setBestRoadmapName(bestRoadmap == null ? "N/A" : bestRoadmap.getKey());
        response.setBestRoadmapScore(bestRoadmap == null ? 0.0 : round(bestRoadmap.getValue()));
        response.setStrongTopicName(strongTopic == null ? "N/A" : strongTopic.getKey());
        response.setStrongTopicScore(strongTopic == null ? 0.0 : round(strongTopic.getValue()));
        response.setWeakTopicName(weakTopic == null ? "N/A" : weakTopic.getKey());
        response.setWeakTopicScore(weakTopic == null ? 0.0 : round(weakTopic.getValue()));
        return response;
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
