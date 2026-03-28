// Espejo exacto de: ar.net.ut.backend.forum.dto.ForumDiscussionDTO
export interface ForumDiscussionDTO {
    id: number;
    createdAt: string;
    updatedAt: string;
    topicId: number;
    sortPosition: number;
    createdById: string; // UUID
    title: string;
    slug: string;
    open: boolean;
    permanent: boolean;
}
