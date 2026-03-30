package com.harsh.mini_project.service;

import com.harsh.mini_project.dto.RoadmapDraft;
import com.harsh.mini_project.dto.RoadmapWeek;
import com.harsh.mini_project.model.AppUser;
import com.harsh.mini_project.model.Roadmap;
import com.harsh.mini_project.model.RoadmapStatus;
import com.harsh.mini_project.model.Topic;
import com.harsh.mini_project.repository.RoadmapRepository;
import com.harsh.mini_project.repository.TopicRepository;
import com.harsh.mini_project.repository.WeekExplanationRepository;
import com.harsh.mini_project.repository.WeekLinksRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class RoadmapService {
    private final RoadmapRepository roadmapRepository;
    private final TopicRepository topicRepository;
    private final WeekLinksRepository weekLinksRepository;
    private final WeekExplanationRepository weekExplanationRepository;

    public RoadmapService(RoadmapRepository roadmapRepository,
                          TopicRepository topicRepository,
                          WeekLinksRepository weekLinksRepository,
                          WeekExplanationRepository weekExplanationRepository) {
        this.roadmapRepository = roadmapRepository;
        this.topicRepository = topicRepository;
        this.weekLinksRepository = weekLinksRepository;
        this.weekExplanationRepository = weekExplanationRepository;
    }

    @Transactional
    public Roadmap saveDraft(RoadmapDraft draft, AppUser user) {
        Roadmap roadmap = new Roadmap();
        roadmap.setFieldName(draft.getRequest().getField());
        roadmap.setLevel(draft.getRequest().getLevel());
        roadmap.setDurationMonths(draft.getRequest().getDurationMonths());
        roadmap.setStatus(RoadmapStatus.NOT_STARTED);
        roadmap.setCreatedAt(LocalDateTime.now());
        roadmap.setUser(user);

        for (RoadmapWeek week : draft.getResponse().getRoadmap()) {
            Topic topic = new Topic();
            topic.setWeekNumber(week.getWeek());
            topic.setTopicName(week.getTopic());
            topic.setMilestone(week.getMilestone());
            if (week.getSubtopics() != null) {
                topic.setSubtopics(week.getSubtopics());
            }
            topic.setCompleted(false);
            roadmap.addTopic(topic);
        }

        updateProgress(roadmap);
        return roadmapRepository.save(roadmap);
    }

    public List<Roadmap> getAllRoadmaps(AppUser user) {
        List<Roadmap> roadmaps = roadmapRepository.findByUserOrderByCreatedAtDesc(user);
        roadmaps.forEach(this::updateProgress);
        return roadmaps;
    }

    public Roadmap getRoadmap(Long id, AppUser user) {
        Roadmap roadmap = roadmapRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Roadmap not found"));
        updateProgress(roadmap);
        roadmap.getTopics().sort(Comparator.comparingInt(Topic::getWeekNumber));
        return roadmap;
    }

    @Transactional
    public Integer toggleTopic(Long roadmapId, Long topicId, AppUser user) {
        Roadmap roadmap = roadmapRepository.findByIdAndUser(roadmapId, user)
                .orElseThrow(() -> new IllegalArgumentException("Roadmap not found"));
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found"));
        if (!topic.getRoadmap().getId().equals(roadmap.getId())) {
            throw new IllegalStateException("Topic does not belong to roadmap");
        }
        boolean wasCompleted = topic.isCompleted();
        topic.setCompleted(!wasCompleted);
        topicRepository.save(topic);
        updateProgress(roadmap);
        roadmapRepository.save(roadmap);

        if (wasCompleted || !topic.isCompleted()) {
            return null;
        }

        int weekNumber = topic.getWeekNumber();
        boolean allCompleted = roadmap.getTopics().stream()
                .filter(item -> item.getWeekNumber() == weekNumber)
                .allMatch(Topic::isCompleted);
        return allCompleted ? weekNumber : null;
    }

    @Transactional
    public void deleteRoadmap(Long roadmapId, AppUser user) {
        Roadmap roadmap = roadmapRepository.findByIdAndUser(roadmapId, user)
                .orElseThrow(() -> new IllegalArgumentException("Roadmap not found"));
        weekLinksRepository.deleteByRoadmapId(roadmapId);
        weekExplanationRepository.deleteByRoadmapId(roadmapId);
        roadmapRepository.delete(roadmap);
    }

    private void updateProgress(Roadmap roadmap) {
        int total = roadmap.getTopics().size();
        if (total == 0) {
            roadmap.setProgressPercent(0);
            roadmap.setStatus(RoadmapStatus.NOT_STARTED);
            return;
        }
        long completed = roadmap.getTopics().stream().filter(Topic::isCompleted).count();
        double percent = (completed * 100.0) / total;
        roadmap.setProgressPercent(Math.round(percent * 100.0) / 100.0);
        if (completed == 0) {
            roadmap.setStatus(RoadmapStatus.NOT_STARTED);
        } else if (completed == total) {
            roadmap.setStatus(RoadmapStatus.COMPLETED);
        } else {
            roadmap.setStatus(RoadmapStatus.IN_PROGRESS);
        }
    }

}
