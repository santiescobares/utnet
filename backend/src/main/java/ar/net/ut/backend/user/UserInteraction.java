package ar.net.ut.backend.user;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.model.loggable.CLoggableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_interactions")
@Getter
@Setter
public class UserInteraction extends CLoggableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Enumerated(EnumType.STRING)
    private ResourceType resourceType;
    private String resourceId;

    public enum Type {
        LIKE,
        DISLIKE,
        VOTED_IN_FAVOR,
        VOTED_AGAINST;

        public Type opposite() {
            return switch (this) {
                case LIKE -> DISLIKE;
                case DISLIKE -> LIKE;
                case VOTED_IN_FAVOR -> VOTED_AGAINST;
                case VOTED_AGAINST -> VOTED_IN_FAVOR;
            };
        }
    }
}
