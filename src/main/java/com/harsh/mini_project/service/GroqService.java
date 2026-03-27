package com.harsh.mini_project.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harsh.mini_project.config.GroqProperties;
import com.harsh.mini_project.dto.RoadmapRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GroqService {
    private final WebClient groqWebClient;
    private final GroqProperties groqProperties;
    private final ObjectMapper objectMapper;

    public GroqService(WebClient.Builder webClientBuilder, GroqProperties groqProperties, ObjectMapper objectMapper) {
        this.groqWebClient = webClientBuilder
                .baseUrl("https://api.groq.com/openai/v1")
                .build();
        this.groqProperties = groqProperties;
        this.objectMapper = objectMapper;
    }

    public String generateRoadmapJson(RoadmapRequest request) {
        if (!StringUtils.hasText(groqProperties.getApiKey()) || "REPLACE_WITH_YOUR_GROQ_API_KEY".equals(groqProperties.getApiKey())) {
            throw new IllegalStateException("Groq API key is not configured.");
        }

        int weeks = Math.max(4, request.getDurationMonths() * 4);
        String systemPrompt = "You are an API that returns ONLY valid JSON. No markdown, no extra text.";
        String syllabus = request.getSyllabusTopics();
        String syllabusInstruction = "";
        if (StringUtils.hasText(syllabus)) {
            syllabusInstruction = "You MUST include ALL topics from this comma-separated list. " +
                    "Spread them across weeks (multiple topics per week is allowed). " +
                    "Use the same wording as provided where possible. " +
                    "Syllabus topics: " + syllabus + ". ";
        }
        String userPrompt = String.format(
                "Create a %d-week learning roadmap for field \"%s\" at %s level. " +
                        "%s" +
                        "Return STRICT JSON with this exact schema: " +
                        "{\"roadmap\":[{\"week\":1,\"topic\":\"Topic Name\",\"subtopics\":[\"Sub1\",\"Sub2\"],\"milestone\":\"Goal\"}]}. " +
                        "Each week must be unique. Output JSON only.",
                weeks,
                request.getField(),
                request.getLevel().name().toLowerCase(),
                syllabusInstruction
        );

        Map<String, Object> body = new HashMap<>();
        body.put("model", groqProperties.getModel());
        body.put("temperature", 0.2);
        body.put("max_tokens", 2048);
        body.put("messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userPrompt)
        ));

        String response = groqWebClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + groqProperties.getApiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchangeToMono(result -> {
                    if (result.statusCode().is2xxSuccessful()) {
                        return result.bodyToMono(String.class);
                    }
                    return result.bodyToMono(String.class)
                            .defaultIfEmpty("")
                            .flatMap(payload -> Mono.error(new IllegalStateException(
                                    "Groq API error: HTTP " + result.statusCode().value() + " " + payload
                            )));
                })
                .block();

        return extractContent(response);
    }

    public String generateWeekExplanation(String prompt) {
        if (!StringUtils.hasText(groqProperties.getApiKey()) || "REPLACE_WITH_YOUR_GROQ_API_KEY".equals(groqProperties.getApiKey())) {
            throw new IllegalStateException("Groq API key is not configured.");
        }

        Map<String, Object> body = new HashMap<>();
        body.put("model", groqProperties.getModel());
        body.put("temperature", 0.3);
        body.put("max_tokens", 800);
        body.put("messages", List.of(
                Map.of("role", "system", "content", "You are a helpful study assistant. Return plain text only."),
                Map.of("role", "user", "content", prompt)
        ));

        String response = groqWebClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + groqProperties.getApiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchangeToMono(result -> {
                    if (result.statusCode().is2xxSuccessful()) {
                        return result.bodyToMono(String.class);
                    }
                    return result.bodyToMono(String.class)
                            .defaultIfEmpty("")
                            .flatMap(payload -> Mono.error(new IllegalStateException(
                                    "Groq API error: HTTP " + result.statusCode().value() + " " + payload
                            )));
                })
                .block();

        return extractContent(response);
    }

    private String extractContent(String rawResponse) {
        try {
            JsonNode root = objectMapper.readTree(rawResponse);
            JsonNode contentNode = root.path("choices").get(0).path("message").path("content");
            if (contentNode.isMissingNode()) {
                throw new IllegalStateException("Groq response missing content.");
            }
            return contentNode.asText();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to parse Groq response.", ex);
        }
    }
}
