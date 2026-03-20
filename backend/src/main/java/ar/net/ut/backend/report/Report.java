package ar.net.ut.backend.report;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.model.loggable.CDLoggableEntity;
import ar.net.ut.backend.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "reports")
@Getter
@Setter
@SQLDelete(sql = "UPDATE reports SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
public class Report extends CDLoggableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @Enumerated(EnumType.STRING)
    private ResourceType resourceType;
    private String resourceId;

    @Column(length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    private Status status;

    private int votesInFavor, votesAgainst, requiredVotes;

    public enum Status {
        UNRESOLVED,
        RESOLVED_ACCEPTED,
        RESOLVED_DECLINED;
    }
}
