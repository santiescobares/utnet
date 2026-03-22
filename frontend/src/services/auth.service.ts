import { api } from '@/lib/api';
import type { LoginRequestDTO, LoginResponseDTO } from '@/types/auth.types';

export const authService = {
    login: async (data: LoginRequestDTO): Promise<LoginResponseDTO> => {
        const response = await api.post<LoginResponseDTO>('/auth/login', data);
        return response.data;
    },

    logout: async (): Promise<void> => {
        await api.post<void>('/auth/logout');
    },
};
