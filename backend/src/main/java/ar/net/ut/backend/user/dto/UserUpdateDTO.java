package ar.net.ut.backend.user.dto;

import ar.net.ut.backend.user.dto.profile.UserProfileUpdateDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UserUpdateDTO(
        @Size(min = 3, max = 20, message = "First name is either too short or too long")
        String firstName,

        @Size(min = 3, max = 20, message = "Last name is either too short or too long")
        String lastName,

        @Past(message = "Birthday must be a past date")
        LocalDate birthday,

        UserProfileUpdateDTO profile
) {
}
