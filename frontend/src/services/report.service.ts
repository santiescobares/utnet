import { api } from '@/lib/api';
import type { ReportCreateDTO } from '@/types/report.types';

export const reportService = {
    create: async (dto: ReportCreateDTO): Promise<void> => {
        await api.post('/reports', dto);
    },
};
