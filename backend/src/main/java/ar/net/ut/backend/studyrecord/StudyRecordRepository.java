package ar.net.ut.backend.studyrecord;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface StudyRecordRepository extends JpaRepository<StudyRecord, Long> {

    Page<StudyRecord> findAllBySubjectId(Long subjectId, Pageable pageable);

    Page<StudyRecord> findAllBySubjectIdAndHiddenFalse(Long subjectId, Pageable pageable);

    boolean existsBySlug(String slug);

    Optional<StudyRecord> findBySlug(String slug);

    Optional<StudyRecord> findBySlugAndHiddenFalse(String slug);

    @Modifying
    @Transactional
    @Query("UPDATE StudyRecord s SET s.subject = null WHERE s.subject.id = :subjectId")
    void unlinkStudyRecordsFromSubject(Long subjectId);
}
