/**
 * Servicio de autenticación para interactuar con la API
 */

import httpClient from '@/shared/services/httpClient';
import type {
  LoginRequest,
  LoginResponse,
  RegisterRequest,
  RegisterResponse,
  RefreshTokenRequest,
  RefreshTokenResponse,
  LogoutRequest,
} from '../types/auth.types';

/**
 * Servicio de autenticación
 */
export const authService = {
  /**
   * Iniciar sesión
   */
  async login(credentials: LoginRequest): Promise<LoginResponse> {
    const response = await httpClient.post<LoginResponse>('/auth/login', credentials);
    return response.data;
  },

  /**
   * Registrar nuevo usuario
   */
  async register(data: RegisterRequest): Promise<RegisterResponse> {
    const response = await httpClient.post<RegisterResponse>('/auth/register', data);
    return response.data;
  },

  /**
   * Cerrar sesión
   */
  async logout(refreshToken: string): Promise<void> {
    const payload: LogoutRequest = { refreshToken };
    await httpClient.post('/auth/logout', payload);
  },

  /**
   * Refrescar access token
   */
  async refreshToken(refreshToken: string): Promise<RefreshTokenResponse> {
    const payload: RefreshTokenRequest = { refreshToken };
    const response = await httpClient.post<RefreshTokenResponse>('/auth/refresh', payload);
    return response.data;
  },

  /**
   * Validar si el token es válido
   */
  async validateToken(): Promise<boolean> {
    try {
      await httpClient.get('/auth/validate');
      return true;
    } catch {
      return false;
    }
  },

  /**
   * Obtener información del usuario actual
   */
  async getCurrentUser() {
    const response = await httpClient.get('/auth/me');
    return response.data;
  },
};

export default authService;
