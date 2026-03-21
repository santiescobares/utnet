package ar.net.ut.backend.forum;

import ar.net.ut.backend.forum.repository.ForumDiscussionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class ForumScheduler {

    private final ForumDiscussionRepository forumDiscussionRepository;

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void deleteForumDiscussionsAfter24Hours() {
        int deleted = forumDiscussionRepository.deleteByCreatedAtBefore(Instant.now().minus(24, ChronoUnit.HOURS));
        log.info("Forum discussions cleanup performed. {} entities deleted", deleted);
    }
}
