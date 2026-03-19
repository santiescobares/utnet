package ar.net.ut.backend.course.dto.review;

import jakarta.validation.constraints.*;

public record CourseReviewCreateDTO(

        @NotNull(message = "Course ID is required")
        Long courseId,

        @NotBlank(message = "Content is required")
        @Size(max = 500, message = "Content must not exceed 500 characters")
        String content,

        @DecimalMin(value = "1.0", message = "Rating must be at least 1")
        @DecimalMax(value = "5.0", message = "Rating must not exceed 5")
        double rating

) {
}
