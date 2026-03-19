package ar.net.ut.backend.punishment;

import ar.net.ut.backend.model.loggable.CLoggableEntity;
import ar.net.ut.backend.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "punishments")
@Getter
@Setter
public class Punishment extends CLoggableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 500)
    private String reason;
    private LocalDateTime expirationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "punished_by_id", nullable = false)
    private User punishedBy;
}
