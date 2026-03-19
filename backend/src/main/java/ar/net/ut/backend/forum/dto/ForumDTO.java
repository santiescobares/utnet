package ar.net.ut.backend.forum.dto;

import java.time.Instant;
import java.util.UUID;

public record ForumDTO(
        Long id,
        Instant createdAt,
        Instant updatedAt,
        Long topicId,
        int sortPosition,
        UUID createdById,
        String title,
        String slug,
        boolean open,
        boolean permanent
) {
}
