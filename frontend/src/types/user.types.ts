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
}
