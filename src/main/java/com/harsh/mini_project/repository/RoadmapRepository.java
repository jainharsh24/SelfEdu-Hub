package com.harsh.mini_project.repository;

import com.harsh.mini_project.model.AppUser;
import com.harsh.mini_project.model.Roadmap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoadmapRepository extends JpaRepository<Roadmap, Long> {
    List<Roadmap> findByUserOrderByCreatedAtDesc(AppUser user);
    Optional<Roadmap> findByIdAndUser(Long id, AppUser user);
    Optional<Roadmap> findFirstByUserAndInputSignature(AppUser user, String inputSignature);
}
