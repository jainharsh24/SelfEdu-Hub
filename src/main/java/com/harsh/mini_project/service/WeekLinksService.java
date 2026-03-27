package com.harsh.mini_project.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harsh.mini_project.model.Roadmap;
import com.harsh.mini_project.model.WeekLinks;
import com.harsh.mini_project.repository.WeekLinksRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class WeekLinksService {
    private final WeekLinksRepository weekLinksRepository;
    private final ObjectMapper objectMapper;

    public WeekLinksService(WeekLinksRepository weekLinksRepository, ObjectMapper objectMapper) {
        this.weekLinksRepository = weekLinksRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public Map<Integer, Map<String, String>> getWeekLinksByRoadmap(Roadmap roadmap) {
        List<WeekLinks> stored = weekLinksRepository.findByRoadmapId(roadmap.getId());
        Map<Integer, Map<String, String>> result = new LinkedHashMap<>();
        for (WeekLinks weekLinks : stored) {
            Map<String, String> links = parseLinks(weekLinks.getLinksJson());
            result.put(weekLinks.getWeekNumber(), links);
        }
        return result;
    }

    public void saveWeekLinks(Roadmap roadmap, int weekNumber, Map<String, String> links) {
        String json = toJson(links);
        Optional<WeekLinks> existing = weekLinksRepository.findByRoadmapIdAndWeekNumber(roadmap.getId(), weekNumber);
        WeekLinks weekLinks = existing.orElseGet(WeekLinks::new);
        weekLinks.setRoadmap(roadmap);
        weekLinks.setWeekNumber(weekNumber);
        weekLinks.setLinksJson(json);
        weekLinksRepository.save(weekLinks);
    }

    private Map<String, String> parseLinks(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, String>>() {});
        } catch (Exception ex) {
            return Collections.emptyMap();
        }
    }

    private String toJson(Map<String, String> links) {
        try {
            return objectMapper.writeValueAsString(links);
        } catch (Exception ex) {
            return "{}";
        }
    }
}
