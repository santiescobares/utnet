package ar.net.ut.backend.user.entity;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.model.loggable.CDLoggableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "user_contributions")
@Getter
@Setter
@SQLDelete(sql = "UPDATE user_contributions SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
public class UserContribution extends CDLoggableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private ResourceType resourceType;
    private String resourceId;

    private int awardedPoints;
}
