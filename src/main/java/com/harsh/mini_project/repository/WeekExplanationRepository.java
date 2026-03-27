package com.harsh.mini_project.repository;

import com.harsh.mini_project.model.WeekExplanation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WeekExplanationRepository extends JpaRepository<WeekExplanation, Long> {
    List<WeekExplanation> findByRoadmapId(Long roadmapId);
    Optional<WeekExplanation> findByRoadmapIdAndWeekNumber(Long roadmapId, int weekNumber);
    void deleteByRoadmapId(Long roadmapId);
}
