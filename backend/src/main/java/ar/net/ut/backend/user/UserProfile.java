package ar.net.ut.backend.user;

import ar.net.ut.backend.career.Career;
import ar.net.ut.backend.user.enums.Preference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
public class UserProfile {

    @Id
    private UUID id;
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "career_id")
    private Career career;

    private String pictureKey;
    @Column(length = 500)
    private String biography;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<Preference, String> preferences;

    private double averageContributionPoints;

    public Map<Preference, String> getPreferences() {
        return preferences != null ? Collections.unmodifiableMap(preferences) : Collections.emptyMap();
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":\"" + id + "\"" +
                ", \"userId\":\"" + (user != null ? user.getId() : null) + "\"" +
                ", \"careerId\":" + (career != null ? career.getId() : null) +
                ", \"pictureKey\":\"" + pictureKey + "\"" +
                ", \"biography\":\"" + (biography != null ? biography.replace("\"", "\\\"") : null) + "\"" +
                ", \"preferences\":" + preferences +
                ", \"averageContributionPoints\":" + averageContributionPoints +
                "}";
    }
}
