// Espejo exacto de: ar.net.ut.backend.studyrecord.StudyRecord.Type
// Colores hex (sin '#') definidos en el enum del backend
export type StudyRecordType = 'SUMMARY' | 'NOTE' | 'BIBLIOGRAPHY' | 'EXAM_MODEL';

// Snapshot de materia embebido en StudyRecordDTO (sin correlatives para evitar ciclos)
export interface SubjectSnapshotDTO {
    id: number;
    name: string;
    shortName: string;
    color: string | null; // hex 6 chars sin '#', null si no definido
    careers: { id: number; name: string; color: string | null }[];
}

// Espejo exacto de: ar.net.ut.backend.user.dto.UserSnapshotDTO
export interface UserSnapshotDTO {
    id: string; // UUID
    firstName: string;
    lastName: string;
    profilePictureURL: string | null;
}

// Espejo exacto de: ar.net.ut.backend.studyrecord.dto.StudyRecordDTO
export interface StudyRecordDTO {
    id: number;
    createdAt: string;
    updatedAt: string;
    createdBy: UserSnapshotDTO;
    subjects: SubjectSnapshotDTO[];
    title: string;
    slug: string;
    description: string;
    type: StudyRecordType;
    typeColor: string;  // hex 6 chars sin '#', proveniente del enum backend
    tags: string[];
    resourceSize: number; // tamaño en bytes, proveniente del backend
    downloads: number;
    hidden: boolean;
}

// Espejo exacto de: ar.net.ut.backend.studyrecord.dto.StudyRecordDownloadResponseDTO
export interface StudyRecordDownloadResponseDTO {
    downloadUrl: string; // presigned S3/R2 URL, valid ~3 min
}

// Espejo exacto de: ar.net.ut.backend.studyrecord.dto.StudyRecordCreateDTO
export interface StudyRecordCreateDTO {
    subjectIds: number[];
    title: string;
    description: string;
    type: StudyRecordType;
    tags?: string[];
}

// Espejo exacto de: ar.net.ut.backend.studyrecord.dto.StudyRecordUpdateDTO
export interface StudyRecordUpdateDTO {
    title?: string;
    description?: string;
    type?: StudyRecordType;
    tags?: string[];
    hidden?: boolean;
}

export const STUDY_RECORD_TYPE_LABELS: Record<StudyRecordType, string> = {
    SUMMARY: 'Resumen',
    NOTE: 'Apunte',
    BIBLIOGRAPHY: 'Bibliografía',
    EXAM_MODEL: 'Modelo de parcial',
};
