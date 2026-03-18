package ar.net.ut.backend.user.dto.profile;

import ar.net.ut.backend.career.dto.CareerDTO;
import ar.net.ut.backend.user.enums.Preference;

import java.util.Map;

public record UserProfileDTO(
        CareerDTO career,
        String pictureURL,
        String biography,
        Map<Preference, String> preferences,
        double averageContributionPoints
) {
}
