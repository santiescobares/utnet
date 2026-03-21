package ar.net.ut.backend.forum;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "forum_topics")
@Getter
@Setter
public class ForumTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30, unique = true)
    private String name;
    private int sortPosition;

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ", \"name\":\"" + (name != null ? name.replace("\"", "\\\"") : null) + "\"" +
                ", \"sortPosition\":" + sortPosition +
                "}";
    }
}
