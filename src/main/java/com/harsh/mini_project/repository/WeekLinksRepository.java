package com.harsh.mini_project.repository;

import com.harsh.mini_project.model.WeekLinks;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WeekLinksRepository extends JpaRepository<WeekLinks, Long> {
    List<WeekLinks> findByRoadmapId(Long roadmapId);
    Optional<WeekLinks> findByRoadmapIdAndWeekNumber(Long roadmapId, int weekNumber);
    void deleteByRoadmapId(Long roadmapId);
}
