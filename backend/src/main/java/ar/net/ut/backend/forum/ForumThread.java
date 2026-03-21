package ar.net.ut.backend.forum;

import ar.net.ut.backend.model.loggable.CUDLoggableEntity;
import ar.net.ut.backend.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "forum_threads")
@Getter
@Setter
@SQLDelete(sql = "UPDATE forum_threads SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
public class ForumThread extends CUDLoggableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discussion_id", nullable = false)
    private ForumDiscussion discussion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posted_by_id", nullable = false)
    private User postedBy;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "root_id")
    private ForumThread root;

    @Column(length = 5000)
    private String content;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> imageKeys;

    @OneToMany(mappedBy = "root", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private List<ForumThread> replies;

    private int likes, dislikes;

    public List<String> getImageKeys() {
        return imageKeys != null ? Collections.unmodifiableList(imageKeys) : Collections.emptyList();
    }

    public boolean addReply(ForumThread reply) {
        if (replies == null) {
            replies = new ArrayList<>();
        }
        reply.setDiscussion(discussion);
        reply.setRoot(this);
        return replies.add(reply);
    }

    public boolean removeReply(ForumThread reply) {
        return replies.remove(reply);
    }

    public List<ForumThread> getReplies() {
        return replies != null ? Collections.unmodifiableList(replies) : Collections.emptyList();
    }

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
