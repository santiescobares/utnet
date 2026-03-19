package ar.net.ut.backend.course.controller;

import ar.net.ut.backend.Global;
import ar.net.ut.backend.course.dto.subject.CourseSubjectCreateDTO;
import ar.net.ut.backend.course.dto.subject.CourseSubjectDTO;
import ar.net.ut.backend.course.dto.subject.CourseSubjectUpdateDTO;
import ar.net.ut.backend.course.service.CourseSubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Global.API_VERSION_PATH + "/course-subjects")
@RequiredArgsConstructor
public class CourseSubjectController {

    private final CourseSubjectService courseSubjectService;

    @PostMapping
    public ResponseEntity<CourseSubjectDTO> addSubjectToCourse(@RequestBody @Valid CourseSubjectCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseSubjectService.addSubjectToCourse(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseSubjectDTO> updateCourseSubject(@PathVariable Long id, @RequestBody @Valid CourseSubjectUpdateDTO dto) {
        return ResponseEntity.ok(courseSubjectService.updateCourseSubject(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeCourseSubject(@PathVariable Long id) {
        courseSubjectService.removeCourseSubject(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<CourseSubjectDTO>> getSubjectsByCourse(@RequestParam Long courseId) {
        return ResponseEntity.ok(courseSubjectService.getSubjectsByCourse(courseId));
    }
}
