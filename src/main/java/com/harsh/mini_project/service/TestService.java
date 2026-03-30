package com.harsh.mini_project.service;

import com.harsh.mini_project.dto.QuestionAnswerRequest;
import com.harsh.mini_project.dto.QuestionCreateRequest;
import com.harsh.mini_project.dto.QuestionResponse;
import com.harsh.mini_project.dto.TestCreateRequest;
import com.harsh.mini_project.dto.TestResponse;
import com.harsh.mini_project.dto.TestSubmitRequest;
import com.harsh.mini_project.dto.TestSummaryResponse;
import com.harsh.mini_project.model.Question;
import com.harsh.mini_project.model.Test;
import com.harsh.mini_project.model.TestStatus;
import com.harsh.mini_project.repository.TestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TestService {
    private final TestRepository testRepository;
    private final GroqService groqService;

    public TestService(TestRepository testRepository, GroqService groqService) {
        this.testRepository = testRepository;
        this.groqService = groqService;
    }

    @Transactional
    public TestResponse createTest(TestCreateRequest request) {
        validateCreateRequest(request);
        Test existing = testRepository.findFirstByUserIdAndRoadmapNameAndWeekNumberOrderByCreatedAtDesc(
                request.getUserId(),
                request.getRoadmapName().trim(),
                request.getWeekNumber()
        ).orElse(null);
        if (existing != null) {
            return toResponse(existing);
        }

        Test test = new Test();
        test.setUserId(request.getUserId());
        test.setRoadmapName(request.getRoadmapName().trim());
        test.setTopicName(request.getTopicName().trim());
        test.setWeekNumber(request.getWeekNumber());
        test.setStatus(TestStatus.PENDING);
        test.setScore(0.0);
        test.setCreatedAt(LocalDateTime.now());

        if (request.getQuestions() != null) {
            for (QuestionCreateRequest questionRequest : request.getQuestions()) {
                if (questionRequest == null) {
                    throw new IllegalArgumentException("Question data is required");
                }
                Question question = new Question();
                question.setQuestion(questionRequest.getQuestion());
                if (questionRequest.getOptions() != null) {
                    question.setOptions(questionRequest.getOptions());
                }
                question.setCorrectAnswer(questionRequest.getCorrectAnswer());
                test.addQuestion(question);
            }
        }

        Test saved = testRepository.save(test);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<TestSummaryResponse> getTestsByUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User id is required");
        }
        return testRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public TestResponse getTest(Long testId) {
        Test test = testRepository.findWithQuestionsById(testId)
                .orElseThrow(() -> new IllegalArgumentException("Test not found"));
        return toResponse(test);
    }

    @Transactional
    public TestResponse getTestForTaking(Long testId, Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User id is required");
        }
        Test test = testRepository.findWithQuestionsByIdAndUserId(testId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Test not found"));
        if (test.getStatus() == TestStatus.COMPLETED) {
            throw new IllegalStateException("Test already completed");
        }
        if (test.getQuestions().isEmpty() && test.getStatus() != TestStatus.COMPLETED) {
            TestCreateRequest request = new TestCreateRequest();
            request.setUserId(test.getUserId());
            request.setRoadmapName(test.getRoadmapName());
            request.setTopicName(test.getTopicName());
            request.setWeekNumber(test.getWeekNumber());
            request.setGenerateMcqs(true);
            request.setQuestionCount(10);
            request.setMcqTopic(test.getRoadmapName() + " - " + test.getTopicName());
            addGeneratedQuestions(test, request);
            testRepository.save(test);
        }
        return toResponse(test);
    }

    @Transactional(readOnly = true)
    public TestResponse getTestForReview(Long testId, Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User id is required");
        }
        Test test = testRepository.findWithQuestionsByIdAndUserId(testId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Test not found"));
        return toResponse(test);
    }

    @Transactional(readOnly = true)
    public Map<Integer, TestStatus> getLatestStatusByWeek(Long userId, String roadmapName) {
        if (userId == null || !StringUtils.hasText(roadmapName)) {
            return Map.of();
        }
        List<Test> tests = testRepository.findByUserIdAndRoadmapNameOrderByCreatedAtDesc(userId, roadmapName);
        Map<Integer, TestStatus> statusByWeek = new HashMap<>();
        for (Test test : tests) {
            if (!statusByWeek.containsKey(test.getWeekNumber())) {
                statusByWeek.put(test.getWeekNumber(), test.getStatus());
            }
        }
        return statusByWeek;
    }

    @Transactional
    public TestResponse submitAnswers(Long testId, TestSubmitRequest request) {
        Test test = testRepository.findWithQuestionsById(testId)
                .orElseThrow(() -> new IllegalArgumentException("Test not found"));
        if (test.getStatus() == TestStatus.COMPLETED) {
            throw new IllegalStateException("Test already completed");
        }

        Map<Long, Question> questionMap = new HashMap<>();
        for (Question question : test.getQuestions()) {
            questionMap.put(question.getId(), question);
        }

        if (request != null && request.getAnswers() != null) {
            for (QuestionAnswerRequest answer : request.getAnswers()) {
                if (answer.getQuestionId() == null) {
                    throw new IllegalArgumentException("Question id is required");
                }
                Question question = questionMap.get(answer.getQuestionId());
                if (question == null) {
                    throw new IllegalArgumentException("Question does not belong to test");
                }
                question.setUserAnswer(answer.getUserAnswer());
            }
        }

        updateScoreAndStatus(test);
        test.setStatus(TestStatus.COMPLETED);
        testRepository.save(test);
        return toResponse(test);
    }

    @Transactional
    public TestResponse saveAnswers(Long testId, TestSubmitRequest request, Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User id is required");
        }
        Test test = testRepository.findWithQuestionsByIdAndUserId(testId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Test not found"));
        if (test.getStatus() == TestStatus.COMPLETED) {
            throw new IllegalStateException("Test already completed");
        }

        Map<Long, Question> questionMap = new HashMap<>();
        for (Question question : test.getQuestions()) {
            questionMap.put(question.getId(), question);
        }

        if (request != null && request.getAnswers() != null) {
            for (QuestionAnswerRequest answer : request.getAnswers()) {
                if (answer.getQuestionId() == null) {
                    throw new IllegalArgumentException("Question id is required");
                }
                Question question = questionMap.get(answer.getQuestionId());
                if (question == null) {
                    throw new IllegalArgumentException("Question does not belong to test");
                }
                question.setUserAnswer(answer.getUserAnswer());
            }
        }

        testRepository.save(test);
        return toResponse(test);
    }

    private void updateScoreAndStatus(Test test) {
        int total = test.getQuestions().size();
        int correct = 0;
        int answered = 0;
        for (Question question : test.getQuestions()) {
            String userAnswer = question.getUserAnswer();
            if (userAnswer != null && !userAnswer.isBlank()) {
                answered++;
            }
            String correctAnswer = question.getCorrectAnswer();
            if (correctAnswer != null && userAnswer != null &&
                    correctAnswer.trim().equalsIgnoreCase(userAnswer.trim())) {
                correct++;
            }
        }

        double score = 0.0;
        if (total > 0) {
            score = (correct * 100.0) / total;
            score = Math.round(score * 100.0) / 100.0;
        }

        test.setScore(score);
        if (total == 0 || answered < total) {
            test.setStatus(TestStatus.REVIEW);
        } else {
            test.setStatus(TestStatus.COMPLETED);
        }
    }

    private TestResponse toResponse(Test test) {
        TestResponse response = new TestResponse();
        response.setId(test.getId());
        response.setUserId(test.getUserId());
        response.setRoadmapName(test.getRoadmapName());
        response.setTopicName(test.getTopicName());
        response.setWeekNumber(test.getWeekNumber());
        response.setStatus(test.getStatus());
        response.setScore(test.getScore());
        response.setCreatedAt(test.getCreatedAt());
        response.setQuestions(test.getQuestions()
                .stream()
                .map(this::toQuestionResponse)
                .toList());
        return response;
    }

    private QuestionResponse toQuestionResponse(Question question) {
        QuestionResponse response = new QuestionResponse();
        response.setId(question.getId());
        response.setQuestion(question.getQuestion());
        response.setOptions(question.getOptions());
        response.setCorrectAnswer(question.getCorrectAnswer());
        response.setUserAnswer(question.getUserAnswer());
        return response;
    }

    private TestSummaryResponse toSummary(Test test) {
        TestSummaryResponse response = new TestSummaryResponse();
        response.setId(test.getId());
        response.setUserId(test.getUserId());
        response.setRoadmapName(test.getRoadmapName());
        response.setTopicName(test.getTopicName());
        response.setWeekNumber(test.getWeekNumber());
        response.setStatus(test.getStatus());
        response.setScore(test.getScore());
        response.setCreatedAt(test.getCreatedAt());
        return response;
    }

    private void validateCreateRequest(TestCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("User id is required");
        }
        if (request.getRoadmapName() == null || request.getRoadmapName().isBlank()) {
            throw new IllegalArgumentException("Roadmap name is required");
        }
        if (request.getTopicName() == null || request.getTopicName().isBlank()) {
            throw new IllegalArgumentException("Topic name is required");
        }
        if (request.getWeekNumber() <= 0) {
            throw new IllegalArgumentException("Week number must be positive");
        }
    }

    private void addGeneratedQuestions(Test test, TestCreateRequest request) {
        int count = request.getQuestionCount() == null ? 10 : request.getQuestionCount();
        if (count <= 0) {
            throw new IllegalArgumentException("Question count must be positive");
        }
        String topic = StringUtils.hasText(request.getMcqTopic()) ? request.getMcqTopic() : request.getTopicName();
        List<QuestionCreateRequest> generated = groqService.generateMcqs(topic, count);
        for (QuestionCreateRequest questionRequest : generated) {
            if (questionRequest == null) {
                continue;
            }
            Question question = new Question();
            question.setQuestion(questionRequest.getQuestion());
            if (questionRequest.getOptions() != null) {
                question.setOptions(questionRequest.getOptions());
            }
            question.setCorrectAnswer(questionRequest.getCorrectAnswer());
            test.addQuestion(question);
        }
    }
}
