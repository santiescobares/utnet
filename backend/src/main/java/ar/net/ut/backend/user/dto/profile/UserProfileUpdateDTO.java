package ar.net.ut.backend.user.dto.profile;

import ar.net.ut.backend.user.enums.Preference;

import java.util.Map;

public record UserProfileUpdateDTO(
        Long careerId,

        String biography,

        Map<Preference, String> preferences
) {
}
