// Espejo exacto de: ar.net.ut.backend.report.dto.ReportCreateDTO
export interface ReportCreateDTO {
    resourceType: string; // ResourceType enum value, ej: "COURSE_REVIEW"
    resourceId: string;   // String representation of the resource ID
    reason: string;       // 10-500 chars
}
