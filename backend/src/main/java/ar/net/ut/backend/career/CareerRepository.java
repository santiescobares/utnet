package ar.net.ut.backend.career;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CareerRepository extends JpaRepository<Career, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByIdCharacter(char idCharacter);
}
