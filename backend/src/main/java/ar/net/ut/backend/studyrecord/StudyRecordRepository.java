package ar.net.ut.backend.studyrecord;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface StudyRecordRepository extends JpaRepository<StudyRecord, Long> {

    List<StudyRecord> findBySubjectId(Long subjectId);

    List<StudyRecord> findBySubjectIdAndHiddenFalse(Long subjectId);

    boolean existsBySlug(String slug);

    Optional<StudyRecord> findBySlug(String slug);

    @Modifying
    @Transactional
    @Query("UPDATE StudyRecord s SET s.subject = null WHERE s.subject.id = :subjectId")
    void unlinkStudyRecordsFromSubject(Long subjectId);
}
