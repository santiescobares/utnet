package ar.net.ut.backend.forum.dto.topic;

public record ForumTopicDTO(
        Long id,
        String name,
        int sortPosition
) {
}
