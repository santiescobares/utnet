package ar.net.ut.backend.user.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UserCreateDTO(
        @NotBlank(message = "Registration token is required")
        String registrationToken,

        @NotBlank(message = "First name is required")
        @Size(min = 3, max = 20, message = "First name is either too short or too long")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(min = 3, max = 20, message = "Last name is either too short or too long")
        String lastName,

        @NotNull(message = "Birthday is required")
        @Past(message = "Birthday must be a past date")
        LocalDate birthday
) {
}
