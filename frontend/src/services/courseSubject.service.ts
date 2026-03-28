import { api } from '@/lib/api';
import type { CourseSubjectDTO, CourseSubjectUpdateDTO } from '@/types/course.types';

export const courseSubjectService = {
    getByCourseId: async (courseId: number): Promise<CourseSubjectDTO[]> => {
        const response = await api.get<CourseSubjectDTO[]>('/course-subjects', {
            params: { courseId },
        });
        return response.data;
    },

    update: async (id: number, dto: CourseSubjectUpdateDTO): Promise<CourseSubjectDTO> => {
        const response = await api.put<CourseSubjectDTO>(`/course-subjects/${id}`, dto);
        return response.data;
    },
};
