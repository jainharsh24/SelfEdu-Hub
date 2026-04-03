package com.harsh.mini_project.service;

import com.harsh.mini_project.dto.RoadmapResponse;
import com.harsh.mini_project.dto.RoadmapWeek;
import com.harsh.mini_project.model.Level;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
public class PredefinedRoadmapCatalog {
    private static final Map<String, String> SUBJECTS = new LinkedHashMap<>();
    private static final Map<String, Map<Level, List<PredefinedTopic>>> SUBJECT_TOPICS = new LinkedHashMap<>();

//    static {
//        // Register subjects and topics here.
//        // Example:
//        // registerSubject("java", "Java");
//        // registerTopics("java", Level.BEGINNER, List.of(
//        //         new PredefinedTopic("Java Basics", List.of("JDK", "JVM", "IDE setup"), "Environment setup")
//        // ));
//    }

    static {

        // ================= JAVA =================
        registerSubject("java", "Java");
        registerTopics("java", Level.BEGINNER, List.of(
                new PredefinedTopic("Java Basics", List.of("JDK", "JVM", "IDE setup"), "Environment setup"),
                new PredefinedTopic("OOP in Java", List.of("Classes", "Objects", "Inheritance"), "OOP fundamentals")
        ));
        registerTopics("java", Level.INTERMEDIATE, List.of(
                new PredefinedTopic("Collections", List.of("List", "Map", "Set"), "Data structures"),
                new PredefinedTopic("Exception Handling", List.of("try-catch", "throws"), "Error handling")
        ));
        registerTopics("java", Level.ADVANCED, List.of(
                new PredefinedTopic("JVM Internals", List.of("GC", "JIT"), "Performance and internals"),
                new PredefinedTopic("Multithreading", List.of("Threads", "Synchronization"), "Concurrency")
        ));

        // ================= PYTHON =================
        registerSubject("python", "Python");
        registerTopics("python", Level.BEGINNER, List.of(
                new PredefinedTopic("Python Basics", List.of("Variables", "Loops", "Functions"), "Core syntax"),
                new PredefinedTopic("Data Types", List.of("List", "Tuple", "Dictionary"), "Data handling")
        ));
        registerTopics("python", Level.INTERMEDIATE, List.of(
                new PredefinedTopic("OOP in Python", List.of("Classes", "Inheritance"), "OOP concepts"),
                new PredefinedTopic("File Handling", List.of("Read/Write", "CSV", "JSON"), "File operations")
        ));
        registerTopics("python", Level.ADVANCED, List.of(
                new PredefinedTopic("Libraries", List.of("NumPy", "Pandas"), "Data processing"),
                new PredefinedTopic("Async Programming", List.of("async/await"), "Concurrency")
        ));

        // ================= DSA =================
        registerSubject("dsa", "Data Structures & Algorithms");
        registerTopics("dsa", Level.BEGINNER, List.of(
                new PredefinedTopic("Arrays & Strings", List.of("Traversal", "Basic problems"), "Basics"),
                new PredefinedTopic("Sorting", List.of("Bubble", "Selection"), "Sorting basics")
        ));
        registerTopics("dsa", Level.INTERMEDIATE, List.of(
                new PredefinedTopic("Linked List", List.of("Singly", "Doubly"), "Data structure"),
                new PredefinedTopic("Stacks & Queues", List.of("LIFO", "FIFO"), "Linear DS")
        ));
        registerTopics("dsa", Level.ADVANCED, List.of(
                new PredefinedTopic("Trees", List.of("Binary Tree", "BST"), "Hierarchical DS"),
                new PredefinedTopic("Graphs", List.of("DFS", "BFS"), "Graph algorithms")
        ));

        // ================= DATABASE =================
        registerSubject("db", "Database");
        registerTopics("db", Level.BEGINNER, List.of(
                new PredefinedTopic("SQL Basics", List.of("SELECT", "INSERT"), "Basic queries"),
                new PredefinedTopic("Normalization", List.of("1NF", "2NF"), "Design basics")
        ));
        registerTopics("db", Level.INTERMEDIATE, List.of(
                new PredefinedTopic("Joins", List.of("INNER", "LEFT"), "Data combining"),
                new PredefinedTopic("Indexes", List.of("B-Tree", "Hash"), "Performance")
        ));
        registerTopics("db", Level.ADVANCED, List.of(
                new PredefinedTopic("Transactions", List.of("ACID"), "Consistency"),
                new PredefinedTopic("Query Optimization", List.of("Execution plan"), "Performance tuning")
        ));

        // ================= WEB DEV =================
        registerSubject("web", "Web Development");
        registerTopics("web", Level.BEGINNER, List.of(
                new PredefinedTopic("HTML & CSS", List.of("Tags", "Layouts"), "UI basics"),
                new PredefinedTopic("JavaScript Basics", List.of("Variables", "DOM"), "Frontend logic")
        ));
        registerTopics("web", Level.INTERMEDIATE, List.of(
                new PredefinedTopic("Frontend Framework", List.of("React basics"), "UI building"),
                new PredefinedTopic("REST APIs", List.of("GET", "POST"), "Backend communication")
        ));
        registerTopics("web", Level.ADVANCED, List.of(
                new PredefinedTopic("Authentication", List.of("JWT", "OAuth"), "Security"),
                new PredefinedTopic("Deployment", List.of("Docker", "CI/CD"), "Production")
        ));

        // ================= OS =================
        registerSubject("os", "Operating System");
        registerTopics("os", Level.BEGINNER, List.of(
                new PredefinedTopic("Basics", List.of("Processes", "Threads"), "Core concepts"),
                new PredefinedTopic("CPU Scheduling", List.of("FCFS", "Round Robin"), "Scheduling")
        ));
        registerTopics("os", Level.INTERMEDIATE, List.of(
                new PredefinedTopic("Memory Management", List.of("Paging", "Segmentation"), "Memory handling"),
                new PredefinedTopic("Deadlocks", List.of("Detection", "Avoidance"), "System issues")
        ));
        registerTopics("os", Level.ADVANCED, List.of(
                new PredefinedTopic("File Systems", List.of("Inodes"), "Storage"),
                new PredefinedTopic("Concurrency", List.of("Mutex", "Semaphore"), "Sync")
        ));

        // ================= SPRING BOOT =================
        registerSubject("spring", "Spring Boot");
        registerTopics("spring", Level.BEGINNER, List.of(
                new PredefinedTopic("Spring Basics", List.of("DI", "IOC"), "Core concepts"),
                new PredefinedTopic("REST APIs", List.of("Controllers", "Services"), "API building")
        ));
        registerTopics("spring", Level.INTERMEDIATE, List.of(
                new PredefinedTopic("JPA & Hibernate", List.of("Entities", "Repositories"), "Database layer"),
                new PredefinedTopic("Security", List.of("Spring Security"), "Authentication")
        ));
        registerTopics("spring", Level.ADVANCED, List.of(
                new PredefinedTopic("Microservices", List.of("Feign", "Eureka"), "Architecture"),
                new PredefinedTopic("Performance", List.of("Caching"), "Optimization")
        ));

        // ================= SYSTEM DESIGN =================
        registerSubject("system_design", "System Design");
        registerTopics("system_design", Level.BEGINNER, List.of(
                new PredefinedTopic("Basics", List.of("Scalability", "Latency"), "Fundamentals"),
                new PredefinedTopic("Architecture", List.of("Monolith", "Microservices"), "Design types")
        ));
        registerTopics("system_design", Level.INTERMEDIATE, List.of(
                new PredefinedTopic("Load Balancing", List.of("Round Robin"), "Traffic handling"),
                new PredefinedTopic("Caching", List.of("Redis"), "Performance")
        ));
        registerTopics("system_design", Level.ADVANCED, List.of(
                new PredefinedTopic("Distributed Systems", List.of("CAP theorem"), "Advanced concepts"),
                new PredefinedTopic("Real-world Systems", List.of("URL shortener"), "Case studies")
        ));
    }

    public Optional<PredefinedRoadmap> findRoadmap(String field, Level level) {
        String key = normalizeKey(field);
        if (!StringUtils.hasText(key) || !SUBJECTS.containsKey(key)) {
            return Optional.empty();
        }
        Map<Level, List<PredefinedTopic>> byLevel = SUBJECT_TOPICS.get(key);
        if (byLevel == null) {
            return Optional.empty();
        }
        List<PredefinedTopic> topics = byLevel.get(level);
        if (topics == null || topics.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new PredefinedRoadmap(SUBJECTS.get(key), topics));
    }

    public List<String> getSeedTopics(String field, Level level) {
        Optional<PredefinedRoadmap> roadmap = findRoadmap(field, level);
        if (roadmap.isEmpty()) {
            return List.of();
        }
        List<String> seeds = new ArrayList<>();
        for (PredefinedTopic topic : roadmap.get().getTopics()) {
            if (topic == null) {
                continue;
            }
            if (StringUtils.hasText(topic.getTitle())) {
                seeds.add(topic.getTitle());
            }
            if (topic.getSubtopics() != null) {
                for (String subtopic : topic.getSubtopics()) {
                    if (StringUtils.hasText(subtopic)) {
                        seeds.add(subtopic);
                    }
                }
            }
        }
        return seeds;
    }

    public Optional<RoadmapResponse> buildResponse(String field, Level level, int durationMonths) {
        Optional<PredefinedRoadmap> roadmap = findRoadmap(field, level);
        if (roadmap.isEmpty()) {
            return Optional.empty();
        }
        int totalWeeks = Math.max(4, durationMonths * 4);
        List<PredefinedTopic> topics = roadmap.get().getTopics();
        if (topics.isEmpty()) {
            return Optional.empty();
        }

        List<RoadmapWeek> weeks = new ArrayList<>();
        int weekNumber = 1;
        for (PredefinedTopic topic : topics) {
            if (topic == null || !StringUtils.hasText(topic.getTitle())) {
                continue;
            }
            RoadmapWeek week = new RoadmapWeek();
            week.setWeek(weekNumber);
            week.setTopic(topic.getTitle());
            week.setSubtopics(topic.getSubtopics() == null ? new ArrayList<>() : new ArrayList<>(topic.getSubtopics()));
            week.setMilestone(topic.getMilestone());
            weeks.add(week);
            weekNumber++;
            if (weekNumber > totalWeeks) {
                weekNumber = 1;
            }
        }

        if (weeks.size() < totalWeeks) {
            while (weeks.size() < totalWeeks) {
                RoadmapWeek filler = new RoadmapWeek();
                filler.setWeek(weekNumber);
                filler.setTopic("Practice and Review");
                filler.setSubtopics(new ArrayList<>());
                filler.setMilestone("Consolidate learning");
                weeks.add(filler);
                weekNumber++;
                if (weekNumber > totalWeeks) {
                    weekNumber = 1;
                }
            }
        }

        RoadmapResponse response = new RoadmapResponse();
        response.setRoadmap(weeks);
        return Optional.of(response);
    }

    private static void registerSubject(String key, String displayName) {
        SUBJECTS.put(normalizeKey(key), displayName);
    }

    private static void registerTopics(String key, Level level, List<PredefinedTopic> topics) {
        String normalized = normalizeKey(key);
        SUBJECT_TOPICS.computeIfAbsent(normalized, value -> new LinkedHashMap<>()).put(level, topics);
    }

    private static String normalizeKey(String value) {
        if (value == null) {
            return "";
        }
        return value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", " ").trim();
    }

    public static class PredefinedRoadmap {
        private final String subject;
        private final List<PredefinedTopic> topics;

        public PredefinedRoadmap(String subject, List<PredefinedTopic> topics) {
            this.subject = subject;
            this.topics = topics;
        }

        public String getSubject() {
            return subject;
        }

        public List<PredefinedTopic> getTopics() {
            return topics;
        }
    }

    public static class PredefinedTopic {
        private final String title;
        private final List<String> subtopics;
        private final String milestone;

        public PredefinedTopic(String title, List<String> subtopics, String milestone) {
            this.title = title;
            this.subtopics = subtopics;
            this.milestone = milestone;
        }

        public String getTitle() {
            return title;
        }

        public List<String> getSubtopics() {
            return subtopics;
        }

        public String getMilestone() {
            return milestone;
        }
    }
}
