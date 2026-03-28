import type { CareerDTO } from '@/types/user.types';

// Espejo exacto de: ar.net.ut.backend.subject.dto.SubjectDTO
export interface SubjectDTO {
    id: number;
    name: string;
    shortName: string;
    sortPosition: number;
    color: string | null;   // hex 6 chars sin '#', ej: "FF5733" (null si no definido)
    careers: CareerDTO[];
    correlatives: SubjectDTO[];
}
