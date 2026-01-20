/**
 * Servicio para gestión de usuarios
 */

import httpClient from '@/shared/services/httpClient';
import type {
  User,
  UpdateProfileRequest,
  ChangePasswordRequest,
} from '../types/auth.types';

const API_URL = '/users';

/**
 * Obtiene el perfil del usuario autenticado
 */
export const getCurrentUserProfile = async (): Promise<User> => {
  const response = await httpClient.get<User>(`${API_URL}/me`);
  return response.data;
};

/**
 * Actualiza el perfil del usuario autenticado
 */
export const updateCurrentUserProfile = async (
  data: UpdateProfileRequest
): Promise<User> => {
  const response = await httpClient.put<User>(`${API_URL}/me`, data);
  return response.data;
};

/**
 * Cambia la contraseña del usuario autenticado
 */
export const changePassword = async (
  data: ChangePasswordRequest
): Promise<void> => {
  await httpClient.put<void>(`${API_URL}/me/password`, data);
};

const userService = {
  getCurrentUserProfile,
  updateCurrentUserProfile,
  changePassword,
};

export default userService;
