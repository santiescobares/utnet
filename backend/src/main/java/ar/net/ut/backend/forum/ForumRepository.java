package ar.net.ut.backend.forum;

import ar.net.ut.backend.forum.entity.Forum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ForumRepository extends JpaRepository<Forum, Long> {

    boolean existsBySlug(String slug);

    Optional<Forum> findBySlug(String slug);

    List<Forum> findAllByOrderBySortPositionAsc();
}
