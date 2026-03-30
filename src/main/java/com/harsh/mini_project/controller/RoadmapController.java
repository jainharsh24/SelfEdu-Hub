package com.harsh.mini_project.controller;

import com.harsh.mini_project.dto.RoadmapDraft;
import com.harsh.mini_project.model.Roadmap;
import com.harsh.mini_project.model.Topic;
import com.harsh.mini_project.service.PdfExportService;
import com.harsh.mini_project.service.RoadmapService;
import com.harsh.mini_project.service.TestService;
import com.harsh.mini_project.service.UserService;
import com.harsh.mini_project.service.WeekExplanationService;
import com.harsh.mini_project.service.WeekLinksService;
import com.harsh.mini_project.service.YouTubeSearchService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class RoadmapController {
    private final RoadmapService roadmapService;
    private final PdfExportService pdfExportService;
    private final UserService userService;
    private final TestService testService;
    private final YouTubeSearchService youTubeSearchService;
    private final WeekLinksService weekLinksService;
    private final WeekExplanationService weekExplanationService;

    public RoadmapController(RoadmapService roadmapService,
                             PdfExportService pdfExportService,
                             UserService userService,
                             TestService testService,
                             YouTubeSearchService youTubeSearchService,
                             WeekLinksService weekLinksService,
                             WeekExplanationService weekExplanationService) {
        this.roadmapService = roadmapService;
        this.pdfExportService = pdfExportService;
        this.userService = userService;
        this.testService = testService;
        this.youTubeSearchService = youTubeSearchService;
        this.weekLinksService = weekLinksService;
        this.weekExplanationService = weekExplanationService;
    }

    @PostMapping("/roadmaps/start")
    public String startRoadmap(HttpSession session, Principal principal) {
        RoadmapDraft draft = (RoadmapDraft) session.getAttribute("draftRoadmap");
        if (draft == null) {
            return "redirect:/";
        }
        Roadmap saved = roadmapService.saveDraft(draft, userService.getByUsername(principal.getName()));
        session.removeAttribute("draftRoadmap");
        return "redirect:/roadmaps/" + saved.getId();
    }

    @GetMapping("/roadmaps/{id}")
    public String viewRoadmap(@PathVariable Long id, Model model, Principal principal) {
        var user = userService.getByUsername(principal.getName());
        Roadmap roadmap = roadmapService.getRoadmap(id, user);
        model.addAttribute("roadmap", roadmap);
        model.addAttribute("userId", user.getId());
        model.addAttribute("testStatusByWeek", testService.getLatestStatusByWeek(user.getId(), roadmap.getFieldName()));
        model.addAttribute("weekGroups", groupByWeek(roadmap.getTopics()));
        model.addAttribute("weekLinksByWeek", weekLinksService.getWeekLinksByRoadmap(roadmap));
        model.addAttribute("weekExplanations", weekExplanationService.getExplanationsByRoadmap(roadmap));
        return "roadmap-detail";
    }

    @PostMapping("/roadmaps/{id}/topics/{topicId}/toggle")
    public String toggleTopic(@PathVariable Long id, @PathVariable Long topicId, Principal principal) {
        Integer weekCompleted = roadmapService.toggleTopic(id, topicId, userService.getByUsername(principal.getName()));
        if (weekCompleted != null) {
            return "redirect:/roadmaps/" + id + "?weekCompleted=" + weekCompleted;
        }
        return "redirect:/roadmaps/" + id;
    }

    @PostMapping("/roadmaps/{id}/delete")
    public String deleteRoadmap(@PathVariable Long id, Principal principal) {
        roadmapService.deleteRoadmap(id, userService.getByUsername(principal.getName()));
        return "redirect:/dashboard";
    }

    @PostMapping("/roadmaps/{id}/weeks/{weekNumber}/links")
    public String fetchWeekLinks(@PathVariable Long id, @PathVariable int weekNumber, Model model, Principal principal) {
        Roadmap roadmap = roadmapService.getRoadmap(id, userService.getByUsername(principal.getName()));
        Map<String, String> subtopicQueries = new LinkedHashMap<>();
        for (Topic topic : roadmap.getTopics()) {
            if (topic.getWeekNumber() == weekNumber) {
                String topicName = topic.getTopicName();
                if (topic.getSubtopics() != null) {
                    for (String subtopic : topic.getSubtopics()) {
                        if (subtopic == null || subtopic.isBlank()) {
                            continue;
                        }
                        String query = subtopic;
                        if (topicName != null && !topicName.isBlank()) {
                            query = topicName + " " + subtopic;
                        }
                        subtopicQueries.put(subtopic, query.trim());
                    }
                }
            }
        }
        Map<String, String> links = youTubeSearchService.getBestLinksForSubtopics(subtopicQueries);
        weekLinksService.saveWeekLinks(roadmap, weekNumber, links);
        return "redirect:/roadmaps/" + id;
    }

    @PostMapping("/roadmaps/{id}/weeks/{weekNumber}/explain")
    public String generateWeekExplanation(@PathVariable Long id, @PathVariable int weekNumber, Principal principal) {
        Roadmap roadmap = roadmapService.getRoadmap(id, userService.getByUsername(principal.getName()));
        weekExplanationService.generateAndStore(roadmap, weekNumber);
        return "redirect:/roadmaps/" + id + "?week=" + weekNumber;
    }

    @GetMapping("/roadmaps/{id}/export")
    public ResponseEntity<byte[]> export(@PathVariable Long id, Principal principal) {
        Roadmap roadmap = roadmapService.getRoadmap(id, userService.getByUsername(principal.getName()));
        byte[] pdf = pdfExportService.exportRoadmap(roadmap, weekLinksService.getWeekLinksByRoadmap(roadmap));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=roadmap-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    private List<WeekGroup> groupByWeek(List<Topic> topics) {
        Map<Integer, List<Topic>> grouped = new LinkedHashMap<>();
        for (Topic topic : topics) {
            grouped.computeIfAbsent(topic.getWeekNumber(), key -> new ArrayList<>()).add(topic);
        }
        List<WeekGroup> result = new ArrayList<>();
        for (Map.Entry<Integer, List<Topic>> entry : grouped.entrySet()) {
            result.add(new WeekGroup(entry.getKey(), entry.getValue()));
        }
        return result;
    }

    public static class WeekGroup {
        private final int weekNumber;
        private final List<Topic> topics;

        public WeekGroup(int weekNumber, List<Topic> topics) {
            this.weekNumber = weekNumber;
            this.topics = topics;
        }

        public int getWeekNumber() {
            return weekNumber;
        }

        public List<Topic> getTopics() {
            return topics;
        }
    }
}
