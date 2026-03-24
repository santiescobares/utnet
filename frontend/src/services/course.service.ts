import { api } from '@/lib/api';
import type { CourseDTO } from '@/types/course.types';

export const courseService = {
    // careerId opcional: si se omite, el backend devuelve todos los cursos
    getCourses: async (careerId?: number): Promise<CourseDTO[]> => {
        const response = await api.get<CourseDTO[]>('/courses', {
            params: careerId !== undefined ? { careerId } : undefined,
        });
        return response.data;
    },

    getById: async (id: number): Promise<CourseDTO> => {
        const response = await api.get<CourseDTO>(`/courses/${id}`);
        return response.data;
    },
};
