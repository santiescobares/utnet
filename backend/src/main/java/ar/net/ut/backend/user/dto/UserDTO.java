package ar.net.ut.backend.user.dto;

import ar.net.ut.backend.user.dto.profile.UserProfileDTO;
import ar.net.ut.backend.user.enums.Role;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record UserDTO(
        UUID id,
        Instant createdAt,
        String firstName,
        String lastName,
        LocalDate birthday,
        String email,
        Role role,
        Long referralId,
        UUID referredById,
        UserProfileDTO profile
) {
}
