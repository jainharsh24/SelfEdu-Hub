package com.harsh.mini_project.controller;

import com.harsh.mini_project.dto.TestSummaryResponse;
import com.harsh.mini_project.service.TestService;
import com.harsh.mini_project.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class TestPageController {
    private final TestService testService;
    private final UserService userService;

    public TestPageController(TestService testService, UserService userService) {
        this.testService = testService;
        this.userService = userService;
    }

    @GetMapping("/dashboard-1")
    public String testPage(@RequestParam Long testId, Model model, Principal principal) {
        Long userId = userService.getByUsername(principal.getName()).getId();
        model.addAttribute("userId", userId);
        model.addAttribute("testId", testId);
        return "dashboard-1";
    }

    @GetMapping("/dashboard-3")
    public String reviewPage(@RequestParam Long testId, Model model, Principal principal) {
        Long userId = userService.getByUsername(principal.getName()).getId();
        model.addAttribute("userId", userId);
        model.addAttribute("testId", testId);
        return "dashboard-3";
    }

    @GetMapping("/dashboard-2")
    public String testLogs(Model model, Principal principal) {
        Long userId = userService.getByUsername(principal.getName()).getId();
        List<TestSummaryResponse> tests = testService.getTestsByUser(userId);
        model.addAttribute("tests", tests);
        model.addAttribute("testLogGroups", groupTestLogs(tests));
        return "dashboard-2";
    }

    private List<RoadmapTestLogGroup> groupTestLogs(List<TestSummaryResponse> tests) {
        Map<String, RoadmapTestLogGroup> roadmapGroups = new LinkedHashMap<>();
        for (TestSummaryResponse test : tests) {
            Long roadmapId = test.getRoadmapId();
            String roadmapKey = roadmapId == null ? "legacy:" + test.getRoadmapName() : "roadmap:" + roadmapId;
            RoadmapTestLogGroup roadmapGroup = roadmapGroups.computeIfAbsent(
                    roadmapKey,
                    key -> new RoadmapTestLogGroup(roadmapId, test.getRoadmapName())
            );
            WeekTestLogGroup weekGroup = roadmapGroup.weekGroupByNumber.computeIfAbsent(
                    test.getWeekNumber(),
                    week -> new WeekTestLogGroup(week, test.getTopicName())
            );
            weekGroup.tests.add(test);
        }
        List<RoadmapTestLogGroup> result = new ArrayList<>(roadmapGroups.values());
        for (RoadmapTestLogGroup roadmapGroup : result) {
            roadmapGroup.syncWeeks();
        }
        return result;
    }

    public static class RoadmapTestLogGroup {
        private final Long roadmapId;
        private final String roadmapName;
        private final Map<Integer, WeekTestLogGroup> weekGroupByNumber = new LinkedHashMap<>();
        private List<WeekTestLogGroup> weeks = new ArrayList<>();

        public RoadmapTestLogGroup(Long roadmapId, String roadmapName) {
            this.roadmapId = roadmapId;
            this.roadmapName = roadmapName;
        }

        public Long getRoadmapId() {
            return roadmapId;
        }

        public String getRoadmapName() {
            return roadmapName;
        }

        public List<WeekTestLogGroup> getWeeks() {
            return weeks;
        }

        private void syncWeeks() {
            this.weeks = new ArrayList<>(weekGroupByNumber.values());
        }
    }

    public static class WeekTestLogGroup {
        private final int weekNumber;
        private final String topicName;
        private final List<TestSummaryResponse> tests = new ArrayList<>();

        public WeekTestLogGroup(int weekNumber, String topicName) {
            this.weekNumber = weekNumber;
            this.topicName = topicName;
        }

        public int getWeekNumber() {
            return weekNumber;
        }

        public String getTopicName() {
            return topicName;
        }

        public List<TestSummaryResponse> getTests() {
            return tests;
        }
    }
}
