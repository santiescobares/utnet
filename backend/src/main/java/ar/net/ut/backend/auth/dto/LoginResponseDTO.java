package ar.net.ut.backend.auth.dto;

import ar.net.ut.backend.user.dto.UserDTO;

public record LoginResponseDTO(
        String registrationToken,
        UserDTO user
) {
}
