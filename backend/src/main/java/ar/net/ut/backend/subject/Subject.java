package ar.net.ut.backend.subject;

import ar.net.ut.backend.career.Career;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "subjects")
@Getter
@Setter
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, unique = true)
    private String name;
    @Column(length = 3, unique = true)
    private String shortName;
    private int sortPosition;

    @ManyToMany
    @JoinTable(
            name = "subject_careers",
            joinColumns = @JoinColumn(name = "subject_id"),
            inverseJoinColumns = @JoinColumn(name = "career_id")
    )
    private List<Career> careers;

    @ManyToMany
    @JoinTable(
            name = "subject_correlatives",
            joinColumns = @JoinColumn(name = "subject_id"),
            inverseJoinColumns = @JoinColumn(name = "correlative_id")
    )
    private List<Subject> correlatives;

    public List<Career> getCareers() {
        return careers != null ? Collections.unmodifiableList(careers) : Collections.emptyList();
    }

    public void setCorrelatives(List<Subject> correlatives) {
        if (correlatives != null) {
            for (Subject correlative : correlatives) {
                if (equals(correlative)) {
                    throw new IllegalArgumentException("Subject can't be correlative of its own");
                }
                if (correlative.checkCorrelativeRecursion(this)) {
                    throw new IllegalArgumentException("Subject with id = " + correlative.getId() + " is generating a circular " +
                            "dependency on subject with id = " + id);
                }
                // Transitive redundancy check
                for (Subject other : correlatives) {
                    if (!correlative.equals(other) && other.isCorrelativeOf(correlative)) {
                        throw new IllegalArgumentException("Transitive redundancy: Subject with id = " + correlative.getId() +
                                " is already implicitly required by subject with id = " + other.getId());
                    }
                }
            }
        }
        this.correlatives = correlatives;
    }

    public List<Subject> getCorrelatives() {
        return correlatives != null ? Collections.unmodifiableList(correlatives) : Collections.emptyList();
    }

    public boolean isCorrelativeOf(Subject subject) {
        return checkCorrelativeRecursion(subject);
    }

    private boolean checkCorrelativeRecursion(Subject subject) {
        if (correlatives == null || correlatives.isEmpty()) return false;
        if (correlatives.contains(subject)) return true;

        for (Subject correlative : correlatives) {
            if (correlative.checkCorrelativeRecursion(subject)) return true;
        }

        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Subject other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ", \"name\":\"" + (name != null ? name.replace("\"", "\\\"") : null) + "\"" +
                ", \"shortName\":\"" + shortName + "\"" +
                ", \"sortPosition\":" + sortPosition +
                "}";
    }
}
