package com.harsh.mini_project.service;

import com.harsh.mini_project.dto.DashboardViewData;
import com.harsh.mini_project.dto.Recommendation;
import com.harsh.mini_project.model.AppUser;
import com.harsh.mini_project.model.Roadmap;
import com.harsh.mini_project.model.RoadmapStatus;
import com.harsh.mini_project.model.Test;
import com.harsh.mini_project.model.TestStatus;
import com.harsh.mini_project.repository.TestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardHomeService {
    private final RoadmapService roadmapService;
    private final TestRepository testRepository;

    public DashboardHomeService(RoadmapService roadmapService, TestRepository testRepository) {
        this.roadmapService = roadmapService;
        this.testRepository = testRepository;
    }

    @Transactional(readOnly = true)
    public DashboardViewData getDashboardView(AppUser user) {
        List<Roadmap> allRoadmaps = roadmapService.getAllRoadmaps(user);
        List<Roadmap> activeRoadmaps = allRoadmaps.stream()
                .filter(roadmap -> roadmap.getStatus() != RoadmapStatus.COMPLETED)
                .sorted(Comparator.comparingDouble(Roadmap::getProgressPercent).reversed())
                .toList();

        List<Test> completedTests = testRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .filter(test -> test.getStatus() == TestStatus.COMPLETED)
                .toList();

        double avgScore = completedTests.stream()
                .mapToDouble(Test::getScore)
                .average()
                .orElse(0.0);

        Map<Long, Double> avgScoreByRoadmap = completedTests.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        Test::getRoadmapId,
                        LinkedHashMap::new,
                        java.util.stream.Collectors.averagingDouble(Test::getScore)
                ));

        Recommendation recommendation = buildRecommendation(activeRoadmaps, avgScoreByRoadmap);
        String nextStepLabel = activeRoadmaps.isEmpty() ? "Create" : "Continue";
        return new DashboardViewData(activeRoadmaps, allRoadmaps.size(), round(avgScore), nextStepLabel, recommendation);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private Recommendation buildRecommendation(List<Roadmap> activeRoadmaps, Map<Long, Double> avgScoreByRoadmap) {
        if (activeRoadmaps.isEmpty()) {
            return new Recommendation(
                    "No active roadmap found. Create a new roadmap and start with Week 1.",
                    null,
                    "Create Roadmap"
            );
        }

        Roadmap target = selectTargetRoadmap(activeRoadmaps, avgScoreByRoadmap);
        double progress = target.getProgressPercent();
        double score = avgScoreByRoadmap.getOrDefault(target.getId(), -1.0);
        String message;
        String actionLabel;

        if (target.getStatus() == RoadmapStatus.COMPLETED || progress >= 100.0) {
            message = "Roadmap completed. Start a new roadmap to keep momentum.";
            actionLabel = "Create Roadmap";
            return new Recommendation(message, null, actionLabel);
        }

        if (progress < 30.0) {
            message = target.getFieldName() + ": early stage (" + round(progress) + "%). Continue current week and finish fundamentals.";
            actionLabel = "Continue Roadmap";
            return new Recommendation(message, target.getId(), actionLabel);
        }

        if (score < 0) {
            message = target.getFieldName() + ": continue progress and take your next test to calibrate performance.";
            actionLabel = "Continue Roadmap";
            return new Recommendation(message, target.getId(), actionLabel);
        }

        double roundedScore = round(score);
        if (roundedScore < 50.0) {
            message = target.getFieldName() + ": low score (" + roundedScore + "%). Revise recent topics and retake the test.";
            actionLabel = "Revise + Retake";
        } else if (roundedScore <= 75.0) {
            message = target.getFieldName() + ": moderate score (" + roundedScore + "%). Proceed, but review weak questions.";
            actionLabel = "Continue + Review";
        } else {
            message = target.getFieldName() + ": strong score (" + roundedScore + "%). Move to the next week.";
            actionLabel = "Move Next";
        }
        return new Recommendation(message, target.getId(), actionLabel);
    }

    private Roadmap selectTargetRoadmap(List<Roadmap> activeRoadmaps, Map<Long, Double> avgScoreByRoadmap) {
        return activeRoadmaps.stream()
                .sorted((a, b) -> {
                    double scoreA = avgScoreByRoadmap.getOrDefault(a.getId(), -1.0);
                    double scoreB = avgScoreByRoadmap.getOrDefault(b.getId(), -1.0);
                    boolean weakA = scoreA >= 0 && scoreA < 50.0;
                    boolean weakB = scoreB >= 0 && scoreB < 50.0;
                    if (weakA != weakB) {
                        return weakA ? -1 : 1;
                    }
                    if (weakA) {
                        return Double.compare(scoreA, scoreB);
                    }
                    if (a.getProgressPercent() < 30.0 && b.getProgressPercent() >= 30.0) {
                        return -1;
                    }
                    if (b.getProgressPercent() < 30.0 && a.getProgressPercent() >= 30.0) {
                        return 1;
                    }
                    return Double.compare(b.getProgressPercent(), a.getProgressPercent());
                })
                .findFirst()
                .orElse(activeRoadmaps.getFirst());
    }
}
