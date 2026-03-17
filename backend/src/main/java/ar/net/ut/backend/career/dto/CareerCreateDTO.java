package ar.net.ut.backend.career.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CareerCreateDTO(
        @NotBlank(message = "Name is required")
        @Size(max = 50, message = "Name is too long")
        String name,

        char idCharacter,

        @PositiveOrZero(message = "Sort position must be positive or zero")
        int sortPosition
) {
}
