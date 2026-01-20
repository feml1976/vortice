/**
 * Store de Zustand para manejo del estado de autenticación
 */

import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import toast from 'react-hot-toast';
import { authService } from '../services/authService';
import type { AuthStore, LoginRequest, RegisterRequest, User } from '../types/auth.types';

/**
 * Store de autenticación con Zustand
 */
export const useAuthStore = create<AuthStore>()(
  persist(
    (set, get) => ({
      // Estado inicial
      user: null,
      accessToken: null,
      refreshToken: null,
      isAuthenticated: false,
      isLoading: false,
      error: null,

      // Acciones
      /**
       * Iniciar sesión
       */
      login: async (credentials: LoginRequest) => {
        try {
          set({ isLoading: true, error: null });

          const response = await authService.login(credentials);

          // Guardar tokens en localStorage
          localStorage.setItem('accessToken', response.accessToken);
          localStorage.setItem('refreshToken', response.refreshToken);
          localStorage.setItem('user', JSON.stringify(response.user));

          // Actualizar estado
          set({
            user: response.user,
            accessToken: response.accessToken,
            refreshToken: response.refreshToken,
            isAuthenticated: true,
            isLoading: false,
            error: null,
          });

          toast.success(`¡Bienvenido, ${response.user.firstName}!`);
        } catch (error: any) {
          const errorMessage = error.response?.data?.message || 'Error al iniciar sesión';
          set({ error: errorMessage, isLoading: false });
          toast.error(errorMessage);
          throw error;
        }
      },

      /**
       * Registrar nuevo usuario
       */
      register: async (data: RegisterRequest) => {
        try {
          set({ isLoading: true, error: null });

          await authService.register(data);

          set({ isLoading: false, error: null });

          toast.success('¡Registro exitoso! Ahora puedes iniciar sesión.');
        } catch (error: any) {
          const errorMessage = error.response?.data?.message || 'Error al registrar usuario';
          set({ error: errorMessage, isLoading: false });
          toast.error(errorMessage);
          throw error;
        }
      },

      /**
       * Cerrar sesión
       */
      logout: async () => {
        try {
          const { refreshToken } = get();

          if (refreshToken) {
            await authService.logout(refreshToken);
          }

          // Limpiar localStorage
          localStorage.removeItem('accessToken');
          localStorage.removeItem('refreshToken');
          localStorage.removeItem('user');

          // Limpiar estado
          set({
            user: null,
            accessToken: null,
            refreshToken: null,
            isAuthenticated: false,
            isLoading: false,
            error: null,
          });

          toast.success('Sesión cerrada exitosamente');
        } catch (error: any) {
          // Incluso si falla el logout en el backend, limpiar el frontend
          localStorage.removeItem('accessToken');
          localStorage.removeItem('refreshToken');
          localStorage.removeItem('user');

          set({
            user: null,
            accessToken: null,
            refreshToken: null,
            isAuthenticated: false,
            isLoading: false,
            error: null,
          });

          console.error('Error al cerrar sesión:', error);
        }
      },

      /**
       * Refrescar access token
       */
      refreshAccessToken: async () => {
        try {
          const { refreshToken } = get();

          if (!refreshToken) {
            throw new Error('No refresh token available');
          }

          const response = await authService.refreshToken(refreshToken);

          // Guardar nuevos tokens
          localStorage.setItem('accessToken', response.accessToken);
          localStorage.setItem('refreshToken', response.refreshToken);

          // Actualizar estado
          set({
            accessToken: response.accessToken,
            refreshToken: response.refreshToken,
          });
        } catch (error) {
          // Si falla el refresh, cerrar sesión
          get().clearAuth();
          throw error;
        }
      },

      /**
       * Establecer usuario
       */
      setUser: (user: User | null) => {
        set({ user, isAuthenticated: !!user });
      },

      /**
       * Establecer tokens
       */
      setTokens: (accessToken: string, refreshToken: string) => {
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', refreshToken);

        set({
          accessToken,
          refreshToken,
          isAuthenticated: true,
        });
      },

      /**
       * Limpiar autenticación
       */
      clearAuth: () => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('user');

        set({
          user: null,
          accessToken: null,
          refreshToken: null,
          isAuthenticated: false,
          error: null,
        });
      },

      /**
       * Establecer error
       */
      setError: (error: string | null) => {
        set({ error });
      },

      /**
       * Establecer estado de carga
       */
      setLoading: (isLoading: boolean) => {
        set({ isLoading });
      },
    }),
    {
      name: 'auth-storage',
      partialize: (state) => ({
        user: state.user,
        accessToken: state.accessToken,
        refreshToken: state.refreshToken,
        isAuthenticated: state.isAuthenticated,
      }),
    }
  )
);

export default useAuthStore;
