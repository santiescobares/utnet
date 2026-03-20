package ar.net.ut.backend.context;

import ar.net.ut.backend.user.enums.Role;

import java.util.UUID;

public record RequestContextData(
        UUID userId,
        Role role,
        String email
) {
}
