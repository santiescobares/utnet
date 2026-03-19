package ar.net.ut.backend.forum.dto;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ForumTopicUpdateDTO(
        @Size(max = 30, message = "Name is too long")
        String name,

        @PositiveOrZero(message = "Sort position must be positive or zero")
        Integer sortPosition
) {
}
