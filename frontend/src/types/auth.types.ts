import type { UserDTO } from './user.types';

// Espejo exacto de: ar.net.ut.backend.auth.dto.LoginRequestDTO
export interface LoginRequestDTO {
    googleIdToken: string;
}

// Espejo exacto de: ar.net.ut.backend.auth.dto.LoginResponseDTO
export interface LoginResponseDTO {
    registrationToken: string | null;
    user: UserDTO | null;
}
