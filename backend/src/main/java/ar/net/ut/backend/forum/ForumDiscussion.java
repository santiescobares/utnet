package ar.net.ut.backend.forum;

import ar.net.ut.backend.model.loggable.CUDLoggableEntity;
import ar.net.ut.backend.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "forum_discussions")
@Getter
@Setter
@SQLDelete(sql = "UPDATE forums SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
public class ForumDiscussion extends CUDLoggableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private ForumTopic topic;

    private int sortPosition;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    @Column(length = 100)
    private String title;
    @Column(unique = true)
    private String slug;

    private boolean open, permanent;

    @OneToMany(mappedBy = "discussion", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private List<ForumThread> threads;

    public boolean addThread(ForumThread thread) {
        if (threads == null) {
            threads = new ArrayList<>();
        }
        thread.setDiscussion(this);
        return threads.add(thread);
    }

    public boolean removeThread(ForumThread thread) {
        return threads.remove(thread);
    }

    public List<ForumThread> getThreads() {
        return threads != null ? Collections.unmodifiableList(threads) : Collections.emptyList();
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ", \"topicId\":" + (topic != null ? topic.getId() : null) +
                ", \"sortPosition\":" + sortPosition +
                ", \"createdById\":\"" + (createdBy != null ? createdBy.getId() : null) + "\"" +
                ", \"title\":\"" + (title != null ? title.replace("\"", "\\\"") : null) + "\"" +
                ", \"slug\":\"" + (slug != null ? slug.replace("\"", "\\\"") : null) + "\"" +
                ", \"open\":" + open +
                ", \"permanent\":" + permanent +
                ", \"createdAt\":\"" + getCreatedAt() + "\"" +
                ", \"updatedAt\":\"" + getUpdatedAt() + "\"" +
                ", \"deletedAt\":\"" + getDeletedAt() + "\"" +
                "}";
    }
}
