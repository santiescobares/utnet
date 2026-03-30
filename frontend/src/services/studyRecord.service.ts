import { api } from '@/lib/api';
import type {
    StudyRecordCreateDTO,
    StudyRecordDTO,
    StudyRecordDownloadResponseDTO,
    StudyRecordType,
    StudyRecordUpdateDTO,
} from '@/types/studyrecord.types';

interface PageResponse<T> {
    content: T[];
    totalPages: number;
    totalElements: number;
    number: number;
}

export const studyRecordService = {
    search: async (
        q?: string,
        subjectId?: number,
        type?: StudyRecordType,
        page = 0,
        size = 10,
        sort = 'downloads,DESC',
    ): Promise<PageResponse<StudyRecordDTO>> => {
        const response = await api.get<PageResponse<StudyRecordDTO>>('/study-records/search', {
            params: { q, subjectId, type, page, size, sort },
        });
        return response.data;
    },

    getBySubject: async (
        subjectId: number,
        page = 0,
        size = 8,
    ): Promise<PageResponse<StudyRecordDTO>> => {
        const response = await api.get<PageResponse<StudyRecordDTO>>('/study-records/search', {
            params: { subjectId, page, size, sort: 'downloads,DESC' },
        });
        return response.data;
    },

    getBySlug: async (slug: string): Promise<StudyRecordDTO> => {
        const response = await api.get<StudyRecordDTO>(`/study-records/${slug}`);
        return response.data;
    },

    getById: async (id: number): Promise<StudyRecordDTO> => {
        const response = await api.get<StudyRecordDTO>(`/study-records/id/${id}`);
        return response.data;
    },

    create: async (dto: StudyRecordCreateDTO, file: File): Promise<StudyRecordDTO> => {
        const formData = new FormData();
        formData.append('dto', new Blob([JSON.stringify(dto)], { type: 'application/json' }));
        formData.append('file', file);
        const response = await api.post<StudyRecordDTO>('/study-records', formData, {
            headers: { 'Content-Type': 'multipart/form-data' },
        });
        return response.data;
    },

    update: async (id: number, dto: StudyRecordUpdateDTO): Promise<StudyRecordDTO> => {
        const response = await api.put<StudyRecordDTO>(`/study-records/${id}`, dto);
        return response.data;
    },

    deleteRecord: async (id: number): Promise<void> => {
        await api.delete(`/study-records/${id}`);
    },

    getDownloadUrl: async (id: number): Promise<string> => {
        const response = await api.get<StudyRecordDownloadResponseDTO>(`/study-records/${id}/download`);
        return response.data.downloadUrl;
    },

    getPreviewUrl: async (id: number): Promise<string> => {
        const response = await api.get<StudyRecordDownloadResponseDTO>(`/study-records/${id}/preview`);
        return response.data.downloadUrl;
    },
};
