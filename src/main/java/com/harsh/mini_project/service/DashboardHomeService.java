package com.harsh.mini_project.service;

import com.harsh.mini_project.dto.DashboardRecentTestItem;
import com.harsh.mini_project.dto.DashboardViewData;
import com.harsh.mini_project.model.AppUser;
import com.harsh.mini_project.model.Roadmap;
import com.harsh.mini_project.model.RoadmapStatus;
import com.harsh.mini_project.model.Test;
import com.harsh.mini_project.model.TestStatus;
import com.harsh.mini_project.repository.TestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

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

        double avgScore = completedTests.stream()
                .mapToDouble(Test::getScore)
                .average()
                .orElse(0.0);

        return new DashboardViewData(activeRoadmaps, recentTests, allRoadmaps.size(), round(avgScore));
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
