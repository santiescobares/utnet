package ar.net.ut.backend.studyrecord;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface StudyRecordRepository extends JpaRepository<StudyRecord, Long> {

    boolean existsBySlug(String slug);

    Optional<StudyRecord> findBySlug(String slug);

    Optional<StudyRecord> findBySlugAndHiddenFalse(String slug);

    @Query(value = "SELECT * FROM study_records s " +
            "WHERE s.deleted_at IS NULL " +
            "AND (:query IS NULL OR :query = '' OR s.search_vector @@ websearch_to_tsquery('spanish', :query)) " +
            "AND (:subjectId IS NULL OR s.subject_id = :subjectId) " +
            "AND (:type IS NULL OR s.type = :type) " +
            "AND (s.hidden = false OR :includeHidden = true)",
            countQuery = "SELECT count(*) FROM study_records s " +
                    "WHERE s.deleted_at IS NULL " +
                    "AND (:query IS NULL OR :query = '' OR s.search_vector @@ websearch_to_tsquery('spanish', :query)) " +
                    "AND (:subjectId IS NULL OR s.subject_id = :subjectId) " +
                    "AND (:type IS NULL OR s.type = :type) " +
                    "AND (s.hidden = false OR :includeHidden = true)",
            nativeQuery = true)
    Page<StudyRecord> searchStudyRecords(
            @Param("query") String query,
            @Param("subjectId") Long subjectId,
            @Param("type") StudyRecord.Type type,
            @Param("includeHidden") boolean includeHidden,
            Pageable pageable
    );

    @Modifying
    @Transactional
    @Query("UPDATE StudyRecord s SET s.subject = null WHERE s.subject.id = :subjectId")
    void unlinkStudyRecordsFromSubject(Long subjectId);
}
