package com.harsh.mini_project.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class YouTubeSearchService {
    private final WebClient webClient;
    private final String apiKey;
    private final ObjectMapper objectMapper;

    public YouTubeSearchService(WebClient.Builder webClientBuilder,
                                @Value("${youtube.api.key}") String apiKey,
                                ObjectMapper objectMapper) {
        this.webClient = webClientBuilder
                .baseUrl("https://www.googleapis.com/youtube/v3")
                .build();
        this.apiKey = apiKey;
        this.objectMapper = objectMapper;
    }

    public Map<String, String> getBestLinksForSubtopics(List<String> subtopics) {
        Map<String, String> result = new LinkedHashMap<>();
        for (String subtopic : subtopics) {
            List<String> videoIds = searchVideoIds(subtopic);
            if (videoIds.isEmpty()) {
                result.put(subtopic, buildSearchUrl(subtopic));
                continue;
            }
            String bestId = bestVideoIdByStats(subtopic, videoIds);
            if (bestId != null) {
                result.put(subtopic, "https://www.youtube.com/watch?v=" + bestId);
            } else {
                result.put(subtopic, buildSearchUrl(subtopic));
            }
        }
        return result;
    }

    public Map<String, String> getBestLinksForSubtopics(Map<String, String> subtopicQueries) {
        Map<String, String> result = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : subtopicQueries.entrySet()) {
            String subtopic = entry.getKey();
            String query = entry.getValue();
            List<String> videoIds = searchVideoIds(query);
            if (videoIds.isEmpty()) {
                result.put(subtopic, buildSearchUrl(query));
                continue;
            }
            String bestId = bestVideoIdByStats(query, videoIds);
            if (bestId != null) {
                result.put(subtopic, "https://www.youtube.com/watch?v=" + bestId);
            } else {
                result.put(subtopic, buildSearchUrl(query));
            }
        }
        return result;
    }

    private List<String> searchVideoIds(String query) {
        JsonNode response = fetchJson("/search", query);
        List<String> ids = new ArrayList<>();
        if (response == null || response.get("items") == null) {
            return ids;
        }
        for (JsonNode item : response.get("items")) {
            JsonNode idNode = item.path("id").path("videoId");
            if (!idNode.isMissingNode()) {
                ids.add(idNode.asText());
            }
        }
        return ids;
    }

    private JsonNode fetchJson(String path, String query) {
        String body;
        try {
            body = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(path)
                            .queryParam("part", "snippet")
                            .queryParam("key", apiKey)
                            .queryParam("q", query)
                            .queryParam("type", "video")
                            .queryParam("maxResults", 5)
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception ex) {
            return null;
        }

        if (body == null || body.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readTree(body);
        } catch (Exception ex) {
            return null;
        }
    }

    private String bestVideoIdByStats(String query, List<String> videoIds) {
        String body;
        try {
            body = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/videos")
                            .queryParam("part", "statistics,snippet")
                            .queryParam("id", String.join(",", videoIds))
                            .queryParam("key", apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception ex) {
            return null;
        }

        JsonNode response = null;
        if (body != null && !body.isBlank()) {
            try {
                response = objectMapper.readTree(body);
            } catch (Exception ex) {
                response = null;
            }
        }
        if (response == null || response.get("items") == null) {
            return null;
        }

        String normalizedQuery = normalizeText(query);
        List<String> queryTokens = tokenize(normalizedQuery);

        String bestId = null;
        int bestTitleScore = -1;
        long bestLikes = -1;
        long bestViews = -1;
        for (JsonNode item : response.get("items")) {
            String id = item.path("id").asText(null);
            String title = item.path("snippet").path("title").asText("");
            JsonNode stats = item.path("statistics");
            long views = stats.path("viewCount").asLong(0);
            long likes = stats.path("likeCount").asLong(0);
            int titleScore = titleMatchScore(title, normalizedQuery, queryTokens);

            if (id == null) {
                continue;
            }

            boolean better = titleScore > bestTitleScore
                    || (titleScore == bestTitleScore && likes > bestLikes)
                    || (titleScore == bestTitleScore && likes == bestLikes && views > bestViews);

            if (better) {
                bestTitleScore = titleScore;
                bestLikes = likes;
                bestViews = views;
                bestId = id;
            }
        }

        if (bestTitleScore <= 0) {
            return null;
        }
        return bestId;
    }

    private int titleMatchScore(String title, String normalizedQuery, List<String> queryTokens) {
        if (title == null || title.isBlank() || normalizedQuery.isBlank()) {
            return 0;
        }
        String normalizedTitle = normalizeText(title);
        int score = 0;
        if (normalizedTitle.contains(normalizedQuery)) {
            score += 3;
        }
        for (String token : queryTokens) {
            if (normalizedTitle.contains(token)) {
                score += 1;
            }
        }
        return score;
    }

    private String normalizeText(String text) {
        return text == null ? "" : text.toLowerCase().replaceAll("[^a-z0-9]+", " ").trim();
    }

    private List<String> tokenize(String normalized) {
        List<String> tokens = new ArrayList<>();
        if (normalized == null || normalized.isBlank()) {
            return tokens;
        }
        for (String part : normalized.split("\\s+")) {
            if (part.length() >= 3) {
                tokens.add(part);
            }
        }
        return tokens;
    }

    private String buildSearchUrl(String query) {
        String encoded = query.replace(" ", "+");
        return "https://www.youtube.com/results?search_query=" + encoded;
    }
}
