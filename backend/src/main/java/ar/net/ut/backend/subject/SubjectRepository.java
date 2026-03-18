package ar.net.ut.backend.subject;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByShortNameIgnoreCase(String shortName);
}
