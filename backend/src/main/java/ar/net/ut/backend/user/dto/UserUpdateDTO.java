package ar.net.ut.backend.user.dto;

import ar.net.ut.backend.user.dto.profile.UserProfileUpdateDTO;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UserUpdateDTO(
        @Size(max = 20, message = "First name is too long")
        String firstName,

        @Size(max = 20, message = "Last name is too long")
        String lastName,

        @Past(message = "Birthday must be a past date")
        LocalDate birthday,

        UserProfileUpdateDTO profile
) {
}
