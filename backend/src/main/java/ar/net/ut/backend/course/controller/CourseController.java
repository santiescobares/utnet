package ar.net.ut.backend.course.controller;

import ar.net.ut.backend.Global;
import ar.net.ut.backend.course.dto.CourseCreateDTO;
import ar.net.ut.backend.course.dto.CourseDTO;
import ar.net.ut.backend.course.dto.CourseUpdateDTO;
import ar.net.ut.backend.course.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Global.API_VERSION_PATH + "/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<CourseDTO> createCourse(@RequestBody @Valid CourseCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.createCourse(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<CourseDTO> updateCourse(@PathVariable Long id, @RequestBody @Valid CourseUpdateDTO dto) {
        return ResponseEntity.ok(courseService.updateCourse(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<CourseDTO>> getAllCourses(@RequestParam(name = "careerId", required = false) Long careerId) {
        return ResponseEntity.ok(courseService.getAllCourses(careerId));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @GetMapping("/{name}")
    public ResponseEntity<CourseDTO> getCourseByName(@PathVariable String name) {
        return ResponseEntity.ok(courseService.getCourseByName(name));
    }
}
