package ar.net.ut.backend.forum.repository;

import ar.net.ut.backend.forum.ForumThread;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForumThreadRepository extends JpaRepository<ForumThread, Long> {

    Page<ForumThread> findAllByDiscussionIdAndRootIsNullOrderByCreatedAtAsc(Long discussionId, Pageable pageable);

    Page<ForumThread> findAllByRootIdOrderByCreatedAtAsc(Long rootId, Pageable pageable);
}
