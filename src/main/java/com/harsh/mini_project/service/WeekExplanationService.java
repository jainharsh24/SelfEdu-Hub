package com.harsh.mini_project.service;

import com.harsh.mini_project.model.Roadmap;
import com.harsh.mini_project.model.Topic;
import com.harsh.mini_project.model.WeekExplanation;
import com.harsh.mini_project.repository.WeekExplanationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

@Service
public class WeekExplanationService {
    private final WeekExplanationRepository weekExplanationRepository;
    private final GroqService groqService;

    public WeekExplanationService(WeekExplanationRepository weekExplanationRepository, GroqService groqService) {
        this.weekExplanationRepository = weekExplanationRepository;
        this.groqService = groqService;
    }

    @Transactional(readOnly = true)
    public Map<Integer, String> getExplanationsByRoadmap(Roadmap roadmap) {
        Map<Integer, String> result = new LinkedHashMap<>();
        List<WeekExplanation> stored = weekExplanationRepository.findByRoadmapId(roadmap.getId());
        for (WeekExplanation item : stored) {
            result.put(item.getWeekNumber(), item.getExplanationText());
        }
        return result;
    }

    @Transactional
    public String generateAndStore(Roadmap roadmap, int weekNumber) {
        Optional<WeekExplanation> existing = weekExplanationRepository.findByRoadmapIdAndWeekNumber(roadmap.getId(), weekNumber);
        if (existing.isPresent()) {
            return existing.get().getExplanationText();
        }
        String prompt = buildPrompt(roadmap, weekNumber);
        String explanation = groqService.generateWeekExplanation(prompt);
        WeekExplanation item = new WeekExplanation();
        item.setRoadmap(roadmap);
        item.setWeekNumber(weekNumber);
        item.setExplanationText(explanation);
        weekExplanationRepository.save(item);
        return explanation;
    }

    private String buildPrompt(Roadmap roadmap, int weekNumber) {
        StringJoiner subtopics = new StringJoiner(", ");
        String topicName = null;
        for (Topic topic : roadmap.getTopics()) {
            if (topic.getWeekNumber() != weekNumber) {
                continue;
            }
            if (topicName == null && topic.getTopicName() != null && !topic.getTopicName().isBlank()) {
                topicName = topic.getTopicName();
            }
            if (topic.getSubtopics() != null) {
                for (String sub : topic.getSubtopics()) {
                    if (sub != null && !sub.isBlank()) {
                        subtopics.add(sub.trim());
                    }
                }
            }
        }
        String topicPart = topicName == null ? "this week" : topicName;
        String subtopicPart = subtopics.length() == 0 ? "No subtopics listed." : subtopics.toString();
        return "Explain the weekly topic and subtopics for a self-study student. " +
                "Use this exact format: " +
                "1) Week Summary (exactly 3-4 lines). " +
                "2) Subtopic Details: for EACH subtopic, write at least 3-4 short lines that explain concept, why it matters, and one practice hint. " +
                "Keep each line concise and practical. " +
                "Do not skip any subtopic. " +
                "Use plain text with clear section headings. " +
                "Week topic: " + topicPart + ". " +
                "Subtopics: " + subtopicPart + ".";
    }
}
