package com.harsh.mini_project.repository;

import com.harsh.mini_project.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    Optional<Topic> findFirstByRoadmapIdAndWeekNumberOrderByIdAsc(Long roadmapId, int weekNumber);
}
