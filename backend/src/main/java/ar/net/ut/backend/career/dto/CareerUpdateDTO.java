package ar.net.ut.backend.career.dto;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CareerUpdateDTO(
        @Size(max = 50, message = "Name is too long")
        String name,

        Character idCharacter,

        @PositiveOrZero(message = "Sort position must be positive or zero")
        Integer sortPosition
) {
}
