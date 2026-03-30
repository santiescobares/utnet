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
            "AND (CAST(:query AS text) IS NULL OR CAST(:query AS text) = '' OR s.search_vector @@ websearch_to_tsquery('spanish', CAST(:query AS text))) " +
            "AND (CAST(:subjectIds AS text) IS NULL OR CAST(:subjectIds AS text) = '' OR s.subject_id = ANY(string_to_array(CAST(:subjectIds AS text), ',')::bigint[])) " +
            "AND (CAST(:type AS text) IS NULL OR s.type = CAST(:type AS text)) " +
            "AND (s.hidden = false OR CAST(:includeHidden AS boolean) = true)",
            countQuery = "SELECT count(*) FROM study_records s " +
                    "WHERE s.deleted_at IS NULL " +
                    "AND (CAST(:query AS text) IS NULL OR CAST(:query AS text) = '' OR s.search_vector @@ websearch_to_tsquery('spanish', CAST(:query AS text))) " +
                    "AND (CAST(:subjectIds AS text) IS NULL OR CAST(:subjectIds AS text) = '' OR s.subject_id = ANY(string_to_array(CAST(:subjectIds AS text), ',')::bigint[])) " +
                    "AND (CAST(:type AS text) IS NULL OR s.type = CAST(:type AS text)) " +
                    "AND (s.hidden = false OR CAST(:includeHidden AS boolean) = true)",
            nativeQuery = true)
    Page<StudyRecord> searchStudyRecords(
            @Param("query") String query,
            @Param("subjectIds") String subjectIds,
            @Param("type") String type,
            @Param("includeHidden") boolean includeHidden,
            Pageable pageable
    );

    @Modifying
    @Transactional
    @Query("UPDATE StudyRecord s SET s.subject = null WHERE s.subject.id = :subjectId")
    void unlinkStudyRecordsFromSubject(Long subjectId);
}
