import { api } from '@/lib/api';
import type { ForumDiscussionDTO } from '@/types/forum.types';

interface PageResponse<T> {
    content: T[];
    totalPages: number;
    totalElements: number;
    number: number;
}

export const forumDiscussionService = {
    // Devuelve las más recientes, ordenadas por updatedAt DESC
    getRecent: async (size = 8): Promise<ForumDiscussionDTO[]> => {
        const response = await api.get<PageResponse<ForumDiscussionDTO>>('/forum-discussions', {
            params: { page: 0, size, sort: 'updatedAt,DESC' },
        });
        return response.data.content;
    },
};
