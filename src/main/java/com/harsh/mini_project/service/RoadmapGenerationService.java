package com.harsh.mini_project.service;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.harsh.mini_project.dto.RoadmapDraft;
import com.harsh.mini_project.dto.RoadmapRequest;
import com.harsh.mini_project.dto.RoadmapResponse;
import com.harsh.mini_project.dto.RoadmapWeek;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class RoadmapGenerationService {
    private final GroqService groqService;
    private final ObjectMapper objectMapper;
    private final PredefinedRoadmapCatalog predefinedRoadmapCatalog;

    public RoadmapGenerationService(GroqService groqService,
                                    ObjectMapper objectMapper,
                                    PredefinedRoadmapCatalog predefinedRoadmapCatalog) {
        this.groqService = groqService;
        this.objectMapper = objectMapper;
        this.predefinedRoadmapCatalog = predefinedRoadmapCatalog;
    }

    public RoadmapResponse generateRoadmap(RoadmapRequest request) {
        return generateDraft(request).getResponse();
    }

    public RoadmapDraft generateDraft(RoadmapRequest request) {
        Optional<List<String>> referenceTopics = predefinedRoadmapCatalog.getReferenceTopics(request.getField());
        String rawJson = groqService.generateRoadmapJson(request, referenceTopics.orElse(List.of()));
        String jsonPayload = normalizeJson(rawJson);
        try {
            RoadmapResponse response = objectMapper.readValue(jsonPayload, RoadmapResponse.class);
            normalizeWeeks(response, request.getDurationMonths());
            addMissingSyllabusTopics(response, request);
            markSyllabusTopics(response, request);
            return new RoadmapDraft(request, response, jsonPayload);
        } catch (Exception ex) {
            try {
                RoadmapResponse response = lenientMapper().readValue(jsonPayload, RoadmapResponse.class);
                normalizeWeeks(response, request.getDurationMonths());
                addMissingSyllabusTopics(response, request);
                markSyllabusTopics(response, request);
                return new RoadmapDraft(request, response, jsonPayload);
            } catch (Exception nested) {
                throw new IllegalStateException("Groq returned invalid JSON for roadmap.", nested);
            }
        }
    }

    private String normalizeJson(String rawJson) {
        String trimmed = rawJson == null ? "" : rawJson.trim();
        if (trimmed.startsWith("```")) {
            int firstBrace = trimmed.indexOf('{');
            int lastBrace = trimmed.lastIndexOf('}');
            if (firstBrace >= 0 && lastBrace > firstBrace) {
                return trimmed.substring(firstBrace, lastBrace + 1);
            }
        }
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            return trimmed;
        }
        int start = trimmed.indexOf('{');
        int end = trimmed.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return trimmed.substring(start, end + 1);
        }
        throw new IllegalStateException("No JSON object found in AI response.");
    }

    private ObjectMapper lenientMapper() {
        return JsonMapper.builder()
                .enable(JsonReadFeature.ALLOW_TRAILING_COMMA)
                .enable(JsonReadFeature.ALLOW_SINGLE_QUOTES)
                .enable(JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES)
                .enable(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER)
                .build();
    }

    private void normalizeWeeks(RoadmapResponse response, int durationMonths) {
        if (response == null || response.getRoadmap() == null) {
            return;
        }
        int totalWeeks = Math.max(4, durationMonths * 4);

        List<RoadmapWeek> ordered = new ArrayList<>();
        java.util.Map<Integer, java.util.List<RoadmapWeek>> byWeek = new java.util.LinkedHashMap<>();
        for (RoadmapWeek week : response.getRoadmap()) {
            if (week == null) {
                continue;
            }
            int weekNum = week.getWeek();
            if (weekNum < 1 || weekNum > totalWeeks) {
                continue;
            }
            byWeek.computeIfAbsent(weekNum, key -> new java.util.ArrayList<>()).add(week);
        }
        for (int week = 1; week <= totalWeeks; week++) {
            java.util.List<RoadmapWeek> weeks = byWeek.get(week);
            if (weeks == null) {
                continue;
            }
            ordered.addAll(weeks);
        }
        response.setRoadmap(ordered);
    }

    private void addMissingSyllabusTopics(RoadmapResponse response, RoadmapRequest request) {
        if (response == null || response.getRoadmap() == null || request == null) {
            return;
        }
        List<String> syllabusTopics = parseSyllabusTopics(request.getSyllabusTopics());
        if (syllabusTopics.isEmpty()) {
            return;
        }
        int totalWeeks = Math.max(4, request.getDurationMonths() * 4);
        List<RoadmapWeek> roadmap = response.getRoadmap();

        List<String> missing = new ArrayList<>();
        for (String topic : syllabusTopics) {
            if (!containsTopic(roadmap, topic)) {
                missing.add(topic);
            }
        }

        if (missing.isEmpty()) {
            return;
        }

        int weekPointer = 1;
        for (String topic : missing) {
            RoadmapWeek week = new RoadmapWeek();
            week.setWeek(weekPointer);
            week.setTopic(topic);
            week.setMilestone("Syllabus topic");
            week.setSubtopics(new ArrayList<>());
            roadmap.add(week);
            weekPointer++;
            if (weekPointer > totalWeeks) {
                weekPointer = 1;
            }
        }

        roadmap.sort(java.util.Comparator.comparingInt(RoadmapWeek::getWeek));
    }

    private void markSyllabusTopics(RoadmapResponse response, RoadmapRequest request) {
        if (response == null || response.getRoadmap() == null || request == null) {
            return;
        }
        List<String> syllabusTopics = parseSyllabusTopics(request.getSyllabusTopics());
        if (syllabusTopics.isEmpty()) {
            return;
        }
        List<String> normalized = new ArrayList<>();
        for (String topic : syllabusTopics) {
            String norm = normalizeText(topic);
            if (!norm.isBlank()) {
                normalized.add(norm);
            }
        }
        if (normalized.isEmpty()) {
            return;
        }
        for (RoadmapWeek week : response.getRoadmap()) {
            if (week == null) {
                continue;
            }
            if (matchesAny(week.getTopic(), normalized)) {
                week.setTopic(appendAsterisk(week.getTopic()));
            }
            if (week.getSubtopics() != null && !week.getSubtopics().isEmpty()) {
                List<String> updated = new ArrayList<>();
                for (String sub : week.getSubtopics()) {
                    if (matchesAny(sub, normalized)) {
                        updated.add(appendAsterisk(sub));
                    } else {
                        updated.add(sub);
                    }
                }
                week.setSubtopics(updated);
            }
        }
    }

    private boolean matchesAny(String text, List<String> normalizedTopics) {
        if (text == null || text.isBlank()) {
            return false;
        }
        String hay = normalizeText(text);
        for (String needle : normalizedTopics) {
            if (hay.contains(needle) || needle.contains(hay)) {
                return true;
            }
        }
        return false;
    }

    private String appendAsterisk(String text) {
        if (text == null || text.isBlank()) {
            return text;
        }
        String trimmed = text.trim();
        if (trimmed.endsWith("*")) {
            return text;
        }
        return trimmed + " *";
    }

    private List<String> parseSyllabusTopics(String raw) {
        List<String> topics = new ArrayList<>();
        if (raw == null || raw.isBlank()) {
            return topics;
        }
        String[] parts = raw.split(",");
        for (String part : parts) {
            String trimmed = part == null ? "" : part.trim();
            if (!trimmed.isEmpty()) {
                topics.add(trimmed);
            }
        }
        return topics;
    }

    private boolean containsTopic(List<RoadmapWeek> roadmap, String syllabusTopic) {
        if (syllabusTopic == null || syllabusTopic.isBlank()) {
            return true;
        }
        String needle = normalizeText(syllabusTopic);
        for (RoadmapWeek week : roadmap) {
            if (week == null) {
                continue;
            }
            if (textMatches(week.getTopic(), needle)) {
                return true;
            }
            if (week.getSubtopics() != null) {
                for (String sub : week.getSubtopics()) {
                    if (textMatches(sub, needle)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean textMatches(String text, String needle) {
        if (text == null || text.isBlank()) {
            return false;
        }
        String hay = normalizeText(text);
        return hay.contains(needle) || needle.contains(hay);
    }

    private String normalizeText(String text) {
        if (text == null) {
            return "";
        }
        return text.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", " ").trim();
    }
}
