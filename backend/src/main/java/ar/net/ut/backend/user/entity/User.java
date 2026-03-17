package ar.net.ut.backend.user.entity;

import ar.net.ut.backend.model.loggable.CUDLoggableEntity;
import ar.net.ut.backend.user.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@SQLDelete(sql = "UPDATE users SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
public class User extends CUDLoggableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 20)
    private String firstName;
    @Column(length = 20)
    private String lastName;
    private LocalDateTime birthday;
    @Column(length = 320, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(unique = true)
    private String googleId;

    @Column(unique = true)
    private Long referralId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referred_by_id")
    private User referredBy;

    private LocalDateTime bannedUntil;

    @OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @PrimaryKeyJoinColumn
    private UserProfile profile;
    @OneToMany(mappedBy = "resource", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserComment> comments;
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<UserContribution> contributions;
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserInteraction> interactions;

    public boolean isBanned() {
        return LocalDateTime.now().isBefore(bannedUntil);
    }

    public boolean addComment(UserComment comment) {
        if (comments == null) {
            comments = new ArrayList<>();
        }
        comment.setResource(this);
        return comments.add(comment);
    }

    public boolean removeComment(UserComment comment) {
        comment.setResource(null);
        return comments.remove(comment);
    }

    public List<UserComment> getComments() {
        return comments != null ? Collections.unmodifiableList(comments) : Collections.emptyList();
    }

    public boolean addContribution(UserContribution contribution) {
        if (contributions == null) {
            contributions = new ArrayList<>();
        }
        contribution.setUser(this);
        return contributions.add(contribution);
    }

    public boolean removeContribution(UserContribution contribution) {
        contribution.setUser(null);
        return contributions.remove(contribution);
    }

    public List<UserContribution> getContributions() {
        return contributions != null ? Collections.unmodifiableList(contributions) : Collections.emptyList();
    }

    public boolean addInteraction(UserInteraction interaction) {
        if (interactions == null) {
            interactions = new ArrayList<>();
        }
        interaction.setUser(this);
        return interactions.add(interaction);
    }

    public boolean removeInteraction(UserInteraction interaction) {
        interaction.setUser(null);
        return interactions.remove(interaction);
    }

    public List<UserInteraction> getInteractions() {
        return interactions != null ? Collections.unmodifiableList(interactions) : Collections.emptyList();
    }
}
