import { api } from '@/lib/api';
import type {
    UserCreateDTO,
    UserDTO,
    UserProfileDTO,
    UserProfilePictureResponseDTO,
    UserProfileUpdateDTO,
    UserUpdateDTO,
} from '@/types/user.types';

export const userService = {
    createUser: async (data: UserCreateDTO, referredBy?: number): Promise<UserDTO> => {
        const params = referredBy !== undefined ? { referredBy } : undefined;
        const response = await api.post<UserDTO>('/users', data, { params });
        return response.data;
    },

    updateUser: async (data: UserUpdateDTO): Promise<UserDTO> => {
        const response = await api.put<UserDTO>('/users', data);
        return response.data;
    },

    deleteUser: async (): Promise<void> => {
        await api.delete<void>('/users');
    },

    updateUserProfilePicture: async (pictureFile: File): Promise<UserProfilePictureResponseDTO> => {
        const formData = new FormData();
        formData.append('pictureFile', pictureFile);
        const response = await api.patch<UserProfilePictureResponseDTO>(
            '/users/profile/picture',
            formData,
            { headers: { 'Content-Type': 'multipart/form-data' } }
        );
        return response.data;
    },

    updateUserProfile: async (data: UserProfileUpdateDTO): Promise<UserProfileDTO> => {
        const response = await api.put<UserProfileDTO>('/users/profile', data);
        return response.data;
    },
};
