package com.harsh.mini_project.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
public class PredefinedRoadmapCatalog {
    private static final Map<String, List<String>> CATALOG = PredefinedRoadmapData.CATALOG;

    public Optional<List<String>> getReferenceTopics(String field) {
        String key = normalizeKey(field);
        if (!StringUtils.hasText(key)) {
            return Optional.empty();
        }
        List<String> topics = CATALOG.get(key);
        if (topics == null || topics.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(topics);
    }

    public List<String> getAvailableSubjects() {
        if (CATALOG.isEmpty()) {
            return List.of();
        }
        List<String> subjects = new ArrayList<>(CATALOG.keySet());
        Collections.sort(subjects);
        return subjects;
    }

    private static String normalizeKey(String value) {
        if (value == null) {
            return "";
        }
        return value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", " ").trim();
    }
}
