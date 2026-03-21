package ar.net.ut.backend.forum.dto.thread;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ForumThreadDTO(
        Long id,
        Instant createdAt,
        Instant updatedAt,
        Long discussionId,
        UUID postedById,
        Long rootId,
        String content,
        List<String> imageURLs,
        int likes,
        int dislikes
) {
}
