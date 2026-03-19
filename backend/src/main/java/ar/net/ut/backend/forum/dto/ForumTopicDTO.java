package ar.net.ut.backend.forum.dto;

public record ForumTopicDTO(
        Long id,
        String name,
        int sortPosition
) {
}
