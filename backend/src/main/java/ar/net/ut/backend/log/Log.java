package ar.net.ut.backend.log;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.model.loggable.CLoggableEntity;
import ar.net.ut.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "logs")
@Getter
@Setter
public class Log extends CLoggableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private ResourceType resourceType;
    private String resourceId;

    @Enumerated(EnumType.STRING)
    private Action action;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String details;

    public enum Action {
        CREATE,
        EDIT,
        DELETE,
        ROLL_BACK,
        OPEN,
        CLOSE,
        LOG_IN,
        LOG_OUT;
    }
}
