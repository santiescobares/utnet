import type { CourseDTO } from '@/types/course.types';

// Espejo exacto de: ar.net.ut.backend.user.dto.UserSnapshotDTO
export interface UserSnapshotDTO {
    id: string;             // UUID
    firstName: string;
    lastName: string;
    profilePictureURL: string | null;
}

// Espejo exacto de: ar.net.ut.backend.user.enums.Role
export type Role =
    | 'NEW_USER'
    | 'CONTRIBUTOR_1'
    | 'CONTRIBUTOR_2'
    | 'CONTRIBUTOR_3'
    | 'ADMINISTRATOR';

// Espejo exacto de: ar.net.ut.backend.user.enums.Preference (vacío — TODO en backend)
export type Preference = never;

// Espejo exacto de: ar.net.ut.backend.career.dto.CareerDTO
export interface CareerDTO {
    id: number;
    name: string;
    idCharacter: string;
    sortPosition: number;
    color: string; // hex 6 chars sin '#', ej: "FF5733"
}

// Espejo exacto de: ar.net.ut.backend.user.dto.profile.UserProfileDTO
export interface UserProfileDTO {
    career: CareerDTO | null;
    pictureURL: string | null;
    biography: string | null;
    preferences: Record<string, string>;
    averageContributionPoints: number;
}

// Espejo exacto de: ar.net.ut.backend.user.dto.profile.UserProfilePictureResponseDTO
export interface UserProfilePictureResponseDTO {
    pictureURL: string;
}

// Espejo exacto de: ar.net.ut.backend.user.dto.profile.UserProfileUpdateDTO
export interface UserProfileUpdateDTO {
    careerId: number | null;
    biography: string | null;
    preferences: Record<string, string> | null;
}

// Espejo exacto de: ar.net.ut.backend.user.dto.UserDTO
export interface UserDTO {
    id: string;
    createdAt: string;
    firstName: string;
    lastName: string;
    birthday: string;
    email: string;
    role: Role;
    referralId: number | null;
    referredById: string | null;
    profile: UserProfileDTO;
    bookmarkedCourses: CourseDTO[];  // espejo de List<CourseDTO>, ordenado por sortPosition
}

// Espejo exacto de: ar.net.ut.backend.user.dto.UserCreateDTO
export interface UserCreateDTO {
    registrationToken: string;
    firstName: string;
    lastName: string;
    birthday: string; // ISO format: YYYY-MM-DD
}

// Espejo exacto de: ar.net.ut.backend.user.dto.UserUpdateDTO
export interface UserUpdateDTO {
    firstName: string | null;
    lastName: string | null;
    birthday: string | null; // ISO format: YYYY-MM-DD
    profile: UserProfileUpdateDTO | null;
    bookmarkedCourseIds: number[] | null;  // espejo de List<Long>; null = no modificar
}

// Espejo exacto de: ar.net.ut.backend.user.enums.ResourceType (solo los tipos rastreados)
export type ResourceType = 'STUDY_RECORD' | 'SUBJECT' | 'COURSE' | 'FORUM_THREAD' | 'FORUM_TOPIC';

// Espejo exacto de: ar.net.ut.backend.user.dto.activity.UserActivityDTO
export interface UserActivityDTO {
    resourceType: ResourceType;
    resourceId: string;
    timestamp: string; // ISO-8601 (Java Instant serializado como string)
}
