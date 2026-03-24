package ar.net.ut.backend.user.dto;

import java.util.UUID;

public record UserSnapshotDTO(
        UUID id,
        String firstName,
        String lastName,
        String profilePictureURL
) {
}
