package ar.net.ut.backend.forum.dto;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ForumDiscussionUpdateDTO(
        Long topicId,

        @Size(min = 5, max = 100, message = "Title is either too short or too long")
        String title,

        @PositiveOrZero(message = "Sort position must be positive or zero")
        Integer sortPosition,

        Boolean open
) {
}
