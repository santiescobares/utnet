package ar.net.ut.backend.forum.dto;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ForumUpdateDTO(
        Long topicId,

        @Size(max = 100, message = "Title is too long")
        String title,

        @PositiveOrZero(message = "Sort position must be positive or zero")
        Integer sortPosition
) {
}
