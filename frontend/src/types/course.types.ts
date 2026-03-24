// Espejo exacto de: ar.net.ut.backend.course.dto.CourseDTO
export interface CourseDTO {
    id: number;
    createdAt: string;
    updatedAt: string;
    careerId: number;
    year: number;
    division: number;
    name: string;
}

// Espejo exacto de: ar.net.ut.backend.course.dto.CourseCreateDTO
export interface CourseCreateDTO {
    careerId: number;
    year: number;
    division: number;
}

// Espejo exacto de: ar.net.ut.backend.course.dto.CourseUpdateDTO
export interface CourseUpdateDTO {
    careerId?: number | null;
    year?: number | null;
    division?: number | null;
}

// Espejo exacto de: ar.net.ut.backend.course.dto.CourseEventDTO
export interface CourseEventDTO {
    id: number;
    createdAt: string;
    updatedAt: string;
    courseId: number;
    date: string;           // LocalDate → ISO date string "YYYY-MM-DD"
    startTime: string | null; // LocalTime → "HH:mm:ss"
    endTime: string | null;
    description: string;
    createdById: string;    // UUID
    lastEditorId: string;   // UUID
}

// Espejo exacto de: ar.net.ut.backend.course.dto.CourseEventCreateDTO
export interface CourseEventCreateDTO {
    courseId: number;
    date: string;
    startTime?: string | null;
    endTime?: string | null;
    description: string;
}

// Espejo exacto de: ar.net.ut.backend.course.dto.CourseEventUpdateDTO
export interface CourseEventUpdateDTO {
    date?: string | null;
    startTime?: string | null;
    endTime?: string | null;
    description?: string | null;
}

// Espejo exacto de: ar.net.ut.backend.course.dto.CourseReviewDTO
export interface CourseReviewDTO {
    id: number;
    createdAt: string;
    courseId: number;
    postedById: string; // UUID
    content: string;
    rating: number;
}

// Espejo exacto de: ar.net.ut.backend.course.dto.CourseReviewCreateDTO
export interface CourseReviewCreateDTO {
    courseId: number;
    content: string;
    rating: number; // 1.0 - 5.0
}

// Espejo exacto de: ar.net.ut.backend.course.dto.CourseSubjectDTO
export interface CourseSubjectDTO {
    id: number;
    createdAt: string;
    updatedAt: string;
    courseId: number;
    subjectId: number;
    professors: Record<string, string>; // ProfessorPosition → name
    classDays: Record<string, string>;  // DayOfWeek → description
}

// Espejo exacto de: ar.net.ut.backend.course.dto.CourseSubjectCreateDTO
export interface CourseSubjectCreateDTO {
    courseId: number;
    subjectId: number;
    professors: Record<string, string>;
    classDays: Record<string, string>;
}

// Espejo exacto de: ar.net.ut.backend.course.dto.CourseSubjectUpdateDTO
export interface CourseSubjectUpdateDTO {
    professors?: Record<string, string> | null;
    classDays?: Record<string, string> | null;
}
