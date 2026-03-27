package com.harsh.mini_project.repository;

import com.harsh.mini_project.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Long> {
}
