package ar.net.ut.backend.course.dto.review;

import jakarta.validation.constraints.*;

public record CourseReviewCreateDTO(
        @NotNull(message = "Course ID is required")
        Long courseId,

        @NotBlank(message = "Content is required")
        @Size(max = 500, message = "Content is too long")
        String content,

        @DecimalMin(value = "1.0", message = "Rating must be greater or equal to 1")
        @DecimalMax(value = "5.0", message = "Rating must be lower or equal to 5")
        double rating
) {
}
