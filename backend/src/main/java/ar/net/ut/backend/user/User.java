package ar.net.ut.backend.user;

import ar.net.ut.backend.course.Course;
import ar.net.ut.backend.model.loggable.CUDLoggableEntity;
import ar.net.ut.backend.user.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

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
    private LocalDate birthday;
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

    private Instant bannedUntil;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserProfile profile;
    @OneToMany(mappedBy = "resource", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserComment> comments;
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<UserContribution> contributions;
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserInteraction> interactions;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookmarkedCourse> bookmarkedCourses;

    public boolean isBanned() {
        return bannedUntil != null && Instant.now().isBefore(bannedUntil);
    }

    public void createProfile() {
        if (profile != null) {
            throw new IllegalStateException("User profile is already created");
        }
        profile = new UserProfile();
        profile.setUser(this);

        // TODO set default preferences
    }

    public boolean addComment(UserComment comment) {
        if (comments == null) {
            comments = new ArrayList<>();
        }
        comment.setResource(this);
        return comments.add(comment);
    }

    public boolean removeComment(UserComment comment) {
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
        return interactions.remove(interaction);
    }

    public List<UserInteraction> getInteractions() {
        return interactions != null ? Collections.unmodifiableList(interactions) : Collections.emptyList();
    }

    public boolean addBookmarkedCourse(BookmarkedCourse course) {
        if (bookmarkedCourses == null) {
            bookmarkedCourses = new ArrayList<>();
        }
        return bookmarkedCourses.add(course);
    }

    public boolean removeBookmarkedCourse(BookmarkedCourse course) {
        return bookmarkedCourses.remove(course);
    }

    public List<BookmarkedCourse> getBookmarkedCourses() {
        return bookmarkedCourses != null ? Collections.unmodifiableList(bookmarkedCourses) : Collections.emptyList();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User other)) return false;
        return other.id.equals(id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":\"" + id + "\"" +
                ", \"firstName\":\"" + firstName + "\"" +
                ", \"lastName\":\"" + lastName + "\"" +
                ", \"birthday\":\"" + birthday + "\"" +
                ", \"email\":\"" + email + "\"" +
                ", \"role\":\"" + role + "\"" +
                ", \"googleId\":\"" + googleId + "\"" +
                ", \"referralId\":" + referralId +
                ", \"referredById\":\"" + (referredBy != null ? referredBy.getId() : null) + "\"" +
                ", \"bannedUntil\":\"" + bannedUntil + "\"" +
                ", \"createdAt\":\"" + getCreatedAt() + "\"" +
                ", \"updatedAt\":\"" + getUpdatedAt() + "\"" +
                ", \"deletedAt\":\"" + getDeletedAt() + "\"" +
                "}";
    }

    @Entity
    @Table(name = "user_bookmarked_courses")
    @Getter
    @Setter
    public static class BookmarkedCourse {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        private User user;
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "course_id", nullable = false)
        private Course course;

        private int sortPosition;
    }
}
