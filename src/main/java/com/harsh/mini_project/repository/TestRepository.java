package com.harsh.mini_project.repository;

import com.harsh.mini_project.model.Test;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TestRepository extends JpaRepository<Test, Long> {
    List<Test> findByUserIdOrderByCreatedAtDesc(Long userId);

    @EntityGraph(attributePaths = "questions")
    Optional<Test> findWithQuestionsById(Long id);

    @EntityGraph(attributePaths = "questions")
    Optional<Test> findWithQuestionsByIdAndUserId(Long id, Long userId);

    Optional<Test> findFirstByUserIdAndRoadmapNameAndWeekNumberOrderByCreatedAtDesc(Long userId, String roadmapName, int weekNumber);

    List<Test> findByUserIdAndRoadmapNameOrderByCreatedAtDesc(Long userId, String roadmapName);
}
