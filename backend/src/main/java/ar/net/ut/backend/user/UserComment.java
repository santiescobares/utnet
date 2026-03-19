package ar.net.ut.backend.user;

import ar.net.ut.backend.model.CommentEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "user_comments")
@Getter
@Setter
@SQLDelete(sql = "UPDATE user_comments SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
public class UserComment extends CommentEntity<User> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
