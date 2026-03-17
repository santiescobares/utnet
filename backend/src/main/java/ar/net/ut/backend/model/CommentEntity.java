package ar.net.ut.backend.model;

import ar.net.ut.backend.model.loggable.CDLoggableEntity;
import ar.net.ut.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class CommentEntity<T> extends CDLoggableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", nullable = false)
    private T resource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posted_by_id", nullable = false)
    private User postedBy;

    @Column(length = 500)
    private String content;
}
