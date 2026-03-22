import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import type { UserDTO } from '@/types/user.types';

interface AuthState {
    user: UserDTO | null;
    registrationToken: string | null;
    isAuthenticated: boolean;
    setUser: (user: UserDTO) => void;
    setRegistrationToken: (token: string) => void;
    clearRegistrationToken: () => void;
    logout: () => void;
}

export const useAuthStore = create<AuthState>()(
    persist(
        (set) => ({
            user: null,
            registrationToken: null,
            isAuthenticated: false,

            setUser: (user) =>
                set({ user, isAuthenticated: true, registrationToken: null }),

            setRegistrationToken: (token) =>
                set({ registrationToken: token }),

            clearRegistrationToken: () =>
                set({ registrationToken: null }),

            logout: () =>
                set({ user: null, registrationToken: null, isAuthenticated: false }),
        }),
        {
            name: 'utnet-auth-storage',
            partialize: (state) => ({ user: state.user, isAuthenticated: state.isAuthenticated }),
        }
    )
);
