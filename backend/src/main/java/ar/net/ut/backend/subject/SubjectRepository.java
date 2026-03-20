package ar.net.ut.backend.subject;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByShortNameIgnoreCase(String shortName);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM subject_correlatives WHERE subject_id = :subjectId OR correlative_id = :subjectId", nativeQuery = true)
    void unlinkSubjectFromCorrelatives(Long subjectId);
}
