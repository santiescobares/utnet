import type { UserSnapshotDTO } from '@/types/user.types';
import type { SubjectDTO } from '@/types/subject.types';

// Espejo exacto de: ar.net.ut.backend.course.CourseEvent.Tag
export type CourseEventTag =
    | 'MIDTERM_EXAM'
    | 'REMEDIAL_EXAM'
    | 'FINAL_EXAM'
    | 'PRACTICAL_WORK'
    | 'QUESTIONNAIRE'
    | 'SPECIAL_LESSON'
    | 'SPECIAL_DAY'
    | 'OTHER'

export const COURSE_EVENT_TAGS: { value: CourseEventTag; label: string; color: string }[] = [
    { value: 'MIDTERM_EXAM',   label: 'Parcial',          color: '#B4E600' },
    { value: 'REMEDIAL_EXAM',  label: 'Recuperatorio',    color: '#E68D00' },
    { value: 'FINAL_EXAM',     label: 'Final',            color: '#E60400' },
    { value: 'PRACTICAL_WORK', label: 'Trabajo Práctico', color: '#00E2E6' },
    { value: 'QUESTIONNAIRE',  label: 'Cuestionario',     color: '#BB00E6' },
    { value: 'SPECIAL_LESSON', label: 'Clase Especial',   color: '#3200E6' },
    { value: 'SPECIAL_DAY',    label: 'Día Especial',     color: '#0058E6' },
    { value: 'OTHER',          label: 'Otro',             color: '#505250' },
]

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
    tag: CourseEventTag | null;
    tagColor: string | null;  // hex sin '#', ej. "B4E600"
    createdBy: UserSnapshotDTO;
    lastEditor: UserSnapshotDTO;
}

// Espejo exacto de: ar.net.ut.backend.course.dto.CourseEventCreateDTO
export interface CourseEventCreateDTO {
    courseId: number;
    date: string;
    startTime?: string | null;
    endTime?: string | null;
    description: string;
    tag: CourseEventTag;
}

// Espejo exacto de: ar.net.ut.backend.course.dto.CourseEventUpdateDTO
export interface CourseEventUpdateDTO {
    date?: string | null;
    startTime?: string | null;
    endTime?: string | null;
    description?: string | null;
    tag?: CourseEventTag | null;
}

// Espejo exacto de: ar.net.ut.backend.course.dto.CourseReviewDTO
export interface CourseReviewDTO {
    id: number;
    createdAt: string;
    courseId: number;
    postedBy: UserSnapshotDTO;
    content: string;
    rating: number;
    subjectTags: SubjectDTO[];   // etiquetas de materias asociadas (hasta 3, puede ser vacío)
}

// Espejo exacto de: ar.net.ut.backend.course.dto.CourseReviewCreateDTO
export interface CourseReviewCreateDTO {
    courseId: number;
    content: string;
    rating: number; // 1.0 - 5.0
    subjectTagIds?: number[];   // IDs de materias como etiquetas (hasta 3)
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
