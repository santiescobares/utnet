package ar.net.ut.backend.course.controller;

import ar.net.ut.backend.Global;
import ar.net.ut.backend.course.dto.review.CourseReviewCreateDTO;
import ar.net.ut.backend.course.dto.review.CourseReviewDTO;
import ar.net.ut.backend.course.service.CourseReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Global.API_VERSION_PATH + "/course-reviews")
@RequiredArgsConstructor
public class CourseReviewController {

    private final CourseReviewService courseReviewService;

    @PostMapping
    public ResponseEntity<CourseReviewDTO> createReview(@RequestBody @Valid CourseReviewCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseReviewService.createReview(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        courseReviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<CourseReviewDTO>> getReviewsByCourse(@RequestParam Long courseId) {
        return ResponseEntity.ok(courseReviewService.getReviewsByCourse(courseId));
    }
}
