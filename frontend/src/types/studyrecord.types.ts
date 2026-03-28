// Espejo exacto de: ar.net.ut.backend.studyrecord.dto.StudyRecordDTO
export interface StudyRecordDTO {
    id: number;
    createdAt: string;
    updatedAt: string;
    createdById: string; // UUID
    subjectId: number;
    title: string;
    slug: string;
    description: string;
    tags: string[];
    downloads: number;
    hidden: boolean;
}

// Espejo exacto de: ar.net.ut.backend.studyrecord.dto.StudyRecordDownloadResponseDTO
export interface StudyRecordDownloadResponseDTO {
    downloadUrl: string; // presigned S3/R2 URL, valid ~3 min
}
