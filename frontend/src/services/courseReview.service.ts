import { api } from '@/lib/api';
import type { CourseReviewDTO, CourseReviewCreateDTO } from '@/types/course.types';

interface PageResponse<T> {
    content: T[];
    totalPages: number;
    totalElements: number;
    number: number;
}

export const courseReviewService = {
    getByCourse: async (
        courseId: number,
        page = 0,
        size = 8,
    ): Promise<PageResponse<CourseReviewDTO>> => {
        const response = await api.get<PageResponse<CourseReviewDTO>>('/course-reviews', {
            params: { courseId, page, size },
        });
        return response.data;
    },

    create: async (dto: CourseReviewCreateDTO): Promise<CourseReviewDTO> => {
        const response = await api.post<CourseReviewDTO>('/course-reviews', dto);
        return response.data;
    },

    delete: async (id: number): Promise<void> => {
        await api.delete(`/course-reviews/${id}`);
    },
};
