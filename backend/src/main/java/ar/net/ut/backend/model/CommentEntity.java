package ar.net.ut.backend.model;

import ar.net.ut.backend.model.loggable.CDLoggableEntity;
import ar.net.ut.backend.user.User;
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

    private int likes;
    private int dislikes;

    public void addLike() {
        likes++;
    }

    public void removeLike() {
        likes = Math.max(likes - 1, 0);
    }

    public void addDislike() {
        dislikes++;
    }

    public void removeDislike() {
        dislikes = Math.max(dislikes - 1, 0);
    }
}
