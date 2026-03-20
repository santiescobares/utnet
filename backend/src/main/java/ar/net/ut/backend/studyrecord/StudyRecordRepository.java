package ar.net.ut.backend.studyrecord;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudyRecordRepository extends JpaRepository<StudyRecord, Long> {

    List<StudyRecord> findBySubjectId(Long subjectId);

    List<StudyRecord> findBySubjectIdAndHiddenFalse(Long subjectId);

    boolean existsBySlug(String slug);

    Optional<StudyRecord> findBySlug(String slug);
}
