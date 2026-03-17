package ar.net.ut.backend.forum.entity;

import ar.net.ut.backend.model.loggable.CUDLoggableEntity;
import ar.net.ut.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "forums")
@Getter
@Setter
@SQLDelete(sql = "UPDATE forums SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
public class Forum extends CUDLoggableEntity {

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

    @OneToMany(mappedBy = "forum", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private List<ForumThread> threads;

    public boolean addThread(ForumThread thread) {
        if (threads == null) {
            threads = new ArrayList<>();
        }
        thread.setForum(this);
        return threads.add(thread);
    }

    public boolean removeThread(ForumThread thread) {
        thread.setForum(null);
        return threads.remove(thread);
    }

    public List<ForumThread> getThreads() {
        return threads != null ? Collections.unmodifiableList(threads) : Collections.emptyList();
    }
}
