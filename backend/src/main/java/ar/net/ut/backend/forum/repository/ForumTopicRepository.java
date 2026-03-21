package ar.net.ut.backend.forum.repository;

import ar.net.ut.backend.forum.ForumTopic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ForumTopicRepository extends JpaRepository<ForumTopic, Long> {

    boolean existsByNameIgnoreCase(String name);

    List<ForumTopic> findAllByOrderBySortPositionDesc();
}
