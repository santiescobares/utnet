package ar.net.ut.backend.course.controller;

import ar.net.ut.backend.Global;
import ar.net.ut.backend.course.dto.review.CourseReviewCreateDTO;
import ar.net.ut.backend.course.dto.review.CourseReviewDTO;
import ar.net.ut.backend.course.service.CourseReviewService;
import ar.net.ut.backend.user.UserInteraction;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PatchMapping("/{id}/add-interaction")
    public ResponseEntity<Void> addReviewInteraction(
            @PathVariable Long id,
            @RequestParam(name = "type") UserInteraction.Type type
    ) {
        courseReviewService.addReviewInteraction(id, type);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/remove-interaction")
    public ResponseEntity<Void> removeReviewInteraction(
            @PathVariable Long id,
            @RequestParam(name = "type") UserInteraction.Type type
    ) {
        courseReviewService.removeReviewInteraction(id, type);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<CourseReviewDTO>> getReviewsByCourse(
            @RequestParam Long courseId,
            @PageableDefault(sort = "creationTimestamp", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(courseReviewService.getReviewsByCourse(courseId, pageable));
    }
}
