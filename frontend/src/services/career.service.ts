import { api } from '@/lib/api';
import type { CareerDTO } from '@/types/user.types';

export const careerService = {
    getAll: async (): Promise<CareerDTO[]> => {
        const response = await api.get<CareerDTO[]>('/careers');
        return response.data;
    },
};
