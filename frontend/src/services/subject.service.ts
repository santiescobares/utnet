import { api } from '@/lib/api';
import type { SubjectDTO } from '@/types/subject.types';

export const subjectService = {
    getAll: async (): Promise<SubjectDTO[]> => {
        const response = await api.get<SubjectDTO[]>('/subjects');
        return response.data;
    },
};
