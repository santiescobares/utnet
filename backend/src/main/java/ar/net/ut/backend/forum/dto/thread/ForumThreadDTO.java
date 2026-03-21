package ar.net.ut.backend.forum.dto.thread;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ForumThreadDTO(
        Long id,
        Instant createdAt,
        Instant updatedAt,
        Long forumId,
        UUID postedById,
        Long rootId,
        String content,
        List<String> imageKeys
) {
}
