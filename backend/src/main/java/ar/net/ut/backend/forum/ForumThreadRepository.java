package ar.net.ut.backend.forum;

import ar.net.ut.backend.forum.entity.ForumThread;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ForumThreadRepository extends JpaRepository<ForumThread, Long> {

    List<ForumThread> findAllByForumIdAndRootIsNullOrderByCreatedAtAsc(Long forumId);

    List<ForumThread> findAllByRootIdOrderByCreatedAtAsc(Long rootId);
}
