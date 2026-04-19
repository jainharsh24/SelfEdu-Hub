package com.harsh.mini_project.service;

import com.harsh.mini_project.dto.RoadmapSuggestion;
import com.harsh.mini_project.model.AppUser;
import com.harsh.mini_project.model.Roadmap;
import com.harsh.mini_project.model.Test;
import com.harsh.mini_project.model.TestStatus;
import com.harsh.mini_project.model.Topic;
import com.harsh.mini_project.repository.TestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoadmapSuggestionService {
    private final TestRepository testRepository;
    private final WeekLinksService weekLinksService;
    private final WeekExplanationService weekExplanationService;

    public RoadmapSuggestionService(TestRepository testRepository,
                                    WeekLinksService weekLinksService,
                                    WeekExplanationService weekExplanationService) {
        this.testRepository = testRepository;
        this.weekLinksService = weekLinksService;
        this.weekExplanationService = weekExplanationService;
    }

    @Transactional(readOnly = true)
    public List<RoadmapSuggestion> getSuggestionsForRoadmap(AppUser user, Roadmap roadmap) {
        List<RoadmapSuggestion> suggestions = new ArrayList<>();
        List<Test> tests = testRepository.findByUserIdAndRoadmapIdOrderByCreatedAtDesc(user.getId(), roadmap.getId());
        Map<Integer, Test> latestTestByWeek = new LinkedHashMap<>();
        for (Test test : tests) {
            latestTestByWeek.putIfAbsent(test.getWeekNumber(), test);
        }

        Map<Integer, Double> latestCompletedScoreByWeek = new LinkedHashMap<>();
        tests.stream()
                .filter(test -> test.getStatus() == TestStatus.COMPLETED)
                .forEach(test -> latestCompletedScoreByWeek.putIfAbsent(test.getWeekNumber(), test.getScore()));

        Map<Integer, List<Topic>> topicsByWeek = roadmap.getTopics().stream()
                .collect(java.util.stream.Collectors.groupingBy(Topic::getWeekNumber, LinkedHashMap::new, java.util.stream.Collectors.toList()));
        Map<Integer, String> explanationsByWeek = weekExplanationService.getExplanationsByRoadmap(roadmap);
        Map<Integer, Map<String, String>> linksByWeek = weekLinksService.getWeekLinksByRoadmap(roadmap);

        Integer firstIncompleteWeek = null;
        for (Map.Entry<Integer, List<Topic>> entry : topicsByWeek.entrySet()) {
            int week = entry.getKey();
            List<Topic> weekTopics = entry.getValue();
            boolean completed = weekTopics.stream().allMatch(Topic::isCompleted);
            if (!completed && firstIncompleteWeek == null) {
                firstIncompleteWeek = week;
            }

            if (completed) {
                Test latest = latestTestByWeek.get(week);
                if (latest == null || latest.getStatus() != TestStatus.COMPLETED) {
                    suggestions.add(new RoadmapSuggestion(
                            10,
                            "Week " + week + " is completed. Take the pending test now.",
                            "Take Test",
                            "/roadmaps/" + roadmap.getId() + "?action=test&week=" + week,
                            "test",
                            week
                    ));
                }
            }
        }

        if (firstIncompleteWeek != null) {
            suggestions.add(new RoadmapSuggestion(
                    20,
                    "Continue Week " + firstIncompleteWeek + " and complete remaining topics.",
                    "Continue Week",
                    "/roadmaps/" + roadmap.getId() + "?week=" + firstIncompleteWeek,
                    "continue",
                    firstIncompleteWeek
            ));

            if (!explanationsByWeek.containsKey(firstIncompleteWeek)) {
                suggestions.add(new RoadmapSuggestion(
                        30,
                        "Generate and review explanation for Week " + firstIncompleteWeek + ".",
                        "Review Explanation",
                        "/roadmaps/" + roadmap.getId() + "?action=explain&week=" + firstIncompleteWeek,
                        "explain",
                        firstIncompleteWeek
                ));
            }

            Map<String, String> links = linksByWeek.get(firstIncompleteWeek);
            if (links == null || links.isEmpty()) {
                suggestions.add(new RoadmapSuggestion(
                        40,
                        "Fetch study links for Week " + firstIncompleteWeek + " and revise notes.",
                        "Load Links",
                        "/roadmaps/" + roadmap.getId() + "?action=links&week=" + firstIncompleteWeek,
                        "links",
                        firstIncompleteWeek
                ));
            }
        }

        Map.Entry<Integer, Double> poorWeek = latestCompletedScoreByWeek.entrySet().stream()
                .filter(entry -> entry.getValue() < 50.0)
                .min(Map.Entry.comparingByValue())
                .orElse(null);
        if (poorWeek != null) {
            suggestions.add(new RoadmapSuggestion(
                    5,
                    "Retest Week " + poorWeek.getKey() + " (score " + round(poorWeek.getValue()) + "%) after revision.",
                    "Retest Week",
                    "/roadmaps/" + roadmap.getId() + "?action=retest&week=" + poorWeek.getKey(),
                    "retest",
                    poorWeek.getKey()
            ));
        }

        Map.Entry<Integer, Double> mediumWeek = latestCompletedScoreByWeek.entrySet().stream()
                .filter(entry -> entry.getValue() >= 50.0 && entry.getValue() <= 75.0)
                .min(Map.Entry.comparingByValue())
                .orElse(null);
        if (mediumWeek != null) {
            suggestions.add(new RoadmapSuggestion(
                    50,
                    "Review weak questions from Week " + mediumWeek.getKey() + " (" + round(mediumWeek.getValue()) + "%).",
                    "Review Questions",
                    "/roadmaps/" + roadmap.getId() + "?action=review&week=" + mediumWeek.getKey(),
                    "review",
                    mediumWeek.getKey()
            ));
        }

        if (suggestions.isEmpty()) {
            suggestions.add(new RoadmapSuggestion(
                    90,
                    "Roadmap is on track. Continue to the next available week and maintain consistency.",
                    "Open Roadmap",
                    "/roadmaps/" + roadmap.getId(),
                    "open",
                    null
            ));
        }

        return suggestions.stream()
                .sorted(Comparator.comparingInt(RoadmapSuggestion::getPriority))
                .limit(6)
                .toList();
    }

    @Transactional(readOnly = true)
    public Map<Long, List<RoadmapSuggestion>> getSuggestionsByRoadmap(AppUser user, List<Roadmap> roadmaps) {
        Map<Long, List<RoadmapSuggestion>> result = new LinkedHashMap<>();
        for (Roadmap roadmap : roadmaps) {
            result.put(roadmap.getId(), getSuggestionsForRoadmap(user, roadmap));
        }
        return result;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
