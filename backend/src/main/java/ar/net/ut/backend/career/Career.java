package ar.net.ut.backend.career;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "careers")
@Getter
@Setter
public class Career {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, unique = true)
    private String name;
    @Column(unique = true)
    private char idCharacter;
    private int sortPosition;
    @Column(length = 6)
    private String color;

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ", \"name\":\"" + (name != null ? name.replace("\"", "\\\"") : null) + "\"" +
                ", \"idCharacter\":\"" + idCharacter + "\"" +
                ", \"sortPosition\":" + sortPosition +
                "}";
    }
}
