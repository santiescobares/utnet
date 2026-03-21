package ar.net.ut.backend.course.controller;

import ar.net.ut.backend.Global;
import ar.net.ut.backend.course.dto.event.CourseEventCreateDTO;
import ar.net.ut.backend.course.dto.event.CourseEventDTO;
import ar.net.ut.backend.course.dto.event.CourseEventUpdateDTO;
import ar.net.ut.backend.course.service.CourseEventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(Global.API_VERSION_PATH + "/course-events")
@RequiredArgsConstructor
public class CourseEventController {

    private final CourseEventService courseEventService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('CONTRIBUTOR_1', 'CONTRIBUTOR_2', 'CONTRIBUTOR_3', 'ADMINISTRATOR')")
    public ResponseEntity<CourseEventDTO> createCourseEvent(@RequestBody @Valid CourseEventCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseEventService.createCourseEvent(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CONTRIBUTOR_1', 'CONTRIBUTOR_2', 'CONTRIBUTOR_3', 'ADMINISTRATOR')")
    public ResponseEntity<CourseEventDTO> updateCourseEvent(@PathVariable Long id, @RequestBody @Valid CourseEventUpdateDTO dto) {
        return ResponseEntity.ok(courseEventService.updateCourseEvent(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CONTRIBUTOR_3', 'ADMINISTRATOR')")
    public ResponseEntity<Void> deleteCourseEvent(@PathVariable Long id) {
        courseEventService.deleteCourseEvent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<CourseEventDTO>> getEventsByCourse(
            @RequestParam Long courseId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        if (from != null && to != null) {
            return ResponseEntity.ok(courseEventService.getEventsByCourseAndDateRange(courseId, from, to));
        }
        return ResponseEntity.ok(courseEventService.getEventsByCourse(courseId));
    }
}
