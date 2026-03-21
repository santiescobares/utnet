package ar.net.ut.backend.course;

import ar.net.ut.backend.career.Career;
import ar.net.ut.backend.model.loggable.CUDLoggableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "courses")
@Getter
@Setter
@SQLDelete(sql = "UPDATE courses SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
public class Course extends CUDLoggableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "career_id", nullable = false)
    private Career career;
    private int year, division;

    @Setter(AccessLevel.NONE)
    @Column(length = 10, unique = true)
    private String name;

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseSubject> subjects;
    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private List<CourseEvent> events;
    @OneToMany(mappedBy = "resource", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private List<CourseReview> reviews;

    public void setName() {
        if (career == null) {
            throw new IllegalArgumentException("Career can't be null");
        }
        if (year < 1) {
            throw new IllegalArgumentException("Year can't be lower than 1");
        }
        if (division < 0) {
            throw new IllegalArgumentException("Division can't be lower than 0");
        }
        name = Integer.toString(year) + career.getIdCharacter() + division;
    }

    public List<CourseSubject> getSubjects() {
        return subjects != null ? Collections.unmodifiableList(subjects) : Collections.emptyList();
    }

    public boolean addEvent(CourseEvent event) {
        if (events == null) {
            events = new ArrayList<>();
        }
        event.setCourse(this);
        return events.add(event);
    }

    public boolean removeEvent(CourseEvent event) {
        return events.remove(event);
    }

    public List<CourseEvent> getEvents() {
        return events != null ? Collections.unmodifiableList(events) : Collections.emptyList();
    }

    public boolean addReview(CourseReview review) {
        if (reviews == null) {
            reviews = new ArrayList<>();
        }
        review.setResource(this);
        return reviews.add(review);
    }

    public boolean removeReview(CourseReview review) {
        return reviews.remove(review);
    }

    public List<CourseReview> getReviews() {
        return reviews != null ? Collections.unmodifiableList(reviews) : Collections.emptyList();
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ", \"careerId\":" + (career != null ? career.getId() : null) +
                ", \"year\":" + year +
                ", \"division\":" + division +
                ", \"name\":\"" + (name != null ? name.replace("\"", "\\\"") : null) + "\"" +
                ", \"createdAt\":\"" + getCreatedAt() + "\"" +
                ", \"updatedAt\":\"" + getUpdatedAt() + "\"" +
                ", \"deletedAt\":\"" + getDeletedAt() + "\"" +
                "}";
    }
}
