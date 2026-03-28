import { api } from '@/lib/api';
import type { CourseEventDTO, CourseEventCreateDTO, CourseEventUpdateDTO } from '@/types/course.types';

export const courseEventService = {
    getByCourse: async (courseId: number, from?: string, to?: string): Promise<CourseEventDTO[]> => {
        const response = await api.get<CourseEventDTO[]>('/course-events', {
            params: { courseId, ...(from && { from }), ...(to && { to }) },
        });
        return response.data;
    },

    create: async (dto: CourseEventCreateDTO): Promise<CourseEventDTO> => {
        const response = await api.post<CourseEventDTO>('/course-events', dto);
        return response.data;
    },

    update: async (id: number, dto: CourseEventUpdateDTO): Promise<CourseEventDTO> => {
        const response = await api.put<CourseEventDTO>(`/course-events/${id}`, dto);
        return response.data;
    },

    delete: async (id: number): Promise<void> => {
        await api.delete(`/course-events/${id}`);
    },
};
