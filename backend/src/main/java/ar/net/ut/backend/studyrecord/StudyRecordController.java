package ar.net.ut.backend.studyrecord;

import ar.net.ut.backend.Global;
import ar.net.ut.backend.studyrecord.dto.StudyRecordCreateDTO;
import ar.net.ut.backend.studyrecord.dto.StudyRecordDownloadResponseDTO;
import ar.net.ut.backend.studyrecord.dto.StudyRecordDTO;
import ar.net.ut.backend.studyrecord.dto.StudyRecordUpdateDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(Global.API_VERSION_PATH + "/study-records")
@RequiredArgsConstructor
public class StudyRecordController {

    private final StudyRecordService studyRecordService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StudyRecordDTO> createStudyRecord(
            @RequestPart("dto") @Valid StudyRecordCreateDTO dto,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studyRecordService.createStudyRecord(dto, file));
    }

    @GetMapping
    public ResponseEntity<List<StudyRecordDTO>> getStudyRecordsBySubject(@RequestParam Long subjectId) {
        return ResponseEntity.ok(studyRecordService.getStudyRecordsBySubject(subjectId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudyRecordDTO> getStudyRecordById(@PathVariable Long id) {
        return ResponseEntity.ok(studyRecordService.getStudyRecordById(id));
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

    @GetMapping("/{id}/download")
    public ResponseEntity<StudyRecordDownloadResponseDTO> downloadStudyRecord(@PathVariable Long id) {
        return ResponseEntity.ok(studyRecordService.downloadStudyRecord(id));
    }
}
