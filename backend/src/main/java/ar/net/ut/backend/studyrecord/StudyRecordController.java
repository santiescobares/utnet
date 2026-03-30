package ar.net.ut.backend.studyrecord;

import ar.net.ut.backend.Global;
import ar.net.ut.backend.studyrecord.dto.StudyRecordCreateDTO;
import ar.net.ut.backend.studyrecord.dto.StudyRecordDownloadResponseDTO;
import ar.net.ut.backend.studyrecord.dto.StudyRecordDTO;
import ar.net.ut.backend.studyrecord.dto.StudyRecordUpdateDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(Global.API_VERSION_PATH + "/study-records")
@RequiredArgsConstructor
public class StudyRecordController {

    private final StudyRecordService studyRecordService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('CONTRIBUTOR_1', 'CONTRIBUTOR_2', 'CONTRIBUTOR_3', 'ADMINISTRATOR')")
    public ResponseEntity<StudyRecordDTO> createStudyRecord(
            @RequestPart("dto") @Valid StudyRecordCreateDTO dto,
            @RequestPart(value = "file") MultipartFile file
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studyRecordService.createStudyRecord(dto, file));
    }

    @GetMapping("/id/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<StudyRecordDTO> getStudyRecordById(@PathVariable Long id) {
        return ResponseEntity.ok(studyRecordService.getStudyRecordById(id));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<StudyRecordDTO> getStudyRecordBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(studyRecordService.getStudyRecordBySlug(slug));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<StudyRecordDTO>> searchStudyRecords(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) List<Long> subjectIds,
            @RequestParam(required = false) StudyRecord.Type type,
            @PageableDefault(sort = "downloads", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(studyRecordService.searchStudyRecords(q, subjectIds, type, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudyRecordDTO> updateStudyRecord(
            @PathVariable Long id,
            @RequestBody @Valid StudyRecordUpdateDTO dto
    ) {
        return ResponseEntity.ok(studyRecordService.updateStudyRecord(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudyRecord(@PathVariable Long id) {
        studyRecordService.deleteStudyRecord(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/preview")
    public ResponseEntity<StudyRecordDownloadResponseDTO> previewStudyRecord(@PathVariable Long id) {
        return ResponseEntity.ok(studyRecordService.previewStudyRecord(id));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<StudyRecordDownloadResponseDTO> downloadStudyRecord(@PathVariable Long id) {
        return ResponseEntity.ok(studyRecordService.downloadStudyRecord(id));
    }
}
