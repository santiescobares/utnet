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

    @Column(length = 50)
    private String name;
    private char idCharacter;
    private int sortPosition;
}
