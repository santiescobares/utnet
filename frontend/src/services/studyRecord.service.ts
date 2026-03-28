import { api } from '@/lib/api';
import type { StudyRecordDTO, StudyRecordDownloadResponseDTO } from '@/types/studyrecord.types';

interface PageResponse<T> {
    content: T[];
    totalPages: number;
    totalElements: number;
    number: number;
}

export const studyRecordService = {
    getBySubject: async (
        subjectId: number,
        page = 0,
        size = 8,
    ): Promise<PageResponse<StudyRecordDTO>> => {
        const response = await api.get<PageResponse<StudyRecordDTO>>('/study-records', {
            params: { subjectId, page, size, sort: 'downloads,DESC' },
        });
        return response.data;
    },

    getDownloadUrl: async (id: number): Promise<string> => {
        const response = await api.get<StudyRecordDownloadResponseDTO>(`/study-records/${id}/download`);
        return response.data.downloadUrl;
    },
};
