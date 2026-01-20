/**
 * Cliente HTTP con Axios configurado para la API de Vórtice
 */

import axios, { AxiosError, AxiosInstance, InternalAxiosRequestConfig } from 'axios';
import toast from 'react-hot-toast';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

/**
 * Instancia principal de Axios con configuración base
 */
export const httpClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
  },
});

/**
 * Interceptor de request para agregar el token JWT
 */
httpClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const accessToken = localStorage.getItem('accessToken');

    if (accessToken && config.headers) {
      config.headers.Authorization = `Bearer ${accessToken}`;
    }

    return config;
  },
  (error: AxiosError) => {
    return Promise.reject(error);
  }
);

/**
 * Interceptor de response para manejo centralizado de errores
 */
httpClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & {
      _retry?: boolean;
    };

    // Si el error es 401 y no es un request de login/refresh, intentar refrescar token
    if (
      error.response?.status === 401 &&
      originalRequest &&
      !originalRequest._retry &&
      !originalRequest.url?.includes('/auth/login') &&
      !originalRequest.url?.includes('/auth/refresh')
    ) {
      originalRequest._retry = true;

      try {
        const refreshToken = localStorage.getItem('refreshToken');

        if (!refreshToken) {
          throw new Error('No refresh token available');
        }

        // Intentar refrescar el token
        const response = await axios.post(
          `${API_BASE_URL}/auth/refresh`,
          { refreshToken },
          {
            headers: {
              'Content-Type': 'application/json',
            },
          }
        );

        const { accessToken: newAccessToken, refreshToken: newRefreshToken } = response.data;

        // Guardar nuevos tokens
        localStorage.setItem('accessToken', newAccessToken);
        localStorage.setItem('refreshToken', newRefreshToken);

        // Reintentar request original con nuevo token
        if (originalRequest.headers) {
          originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
        }

        return httpClient(originalRequest);
      } catch (refreshError) {
        // Si falla el refresh, limpiar tokens y redirigir a login
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('user');

        // Mostrar mensaje solo si no estamos en la página de login
        if (!window.location.pathname.includes('/login')) {
          toast.error('Sesión expirada. Por favor, inicia sesión nuevamente.');
        }

        // Redirigir a login
        window.location.href = '/login';

        return Promise.reject(refreshError);
      }
    }

    // Manejo de errores por código de estado
    if (error.response) {
      const { status, data } = error.response;

      switch (status) {
        case 400:
          toast.error((data as any)?.message || 'Solicitud inválida');
          break;
        case 401:
          if (!window.location.pathname.includes('/login')) {
            toast.error('No autorizado. Por favor, inicia sesión.');
          }
          break;
        case 403:
          toast.error('No tienes permisos para realizar esta acción');
          break;
        case 404:
          toast.error('Recurso no encontrado');
          break;
        case 429:
          const retryAfter = error.response.headers['retry-after'];
          toast.error(
            (data as any)?.message ||
              `Demasiadas peticiones. Intenta nuevamente en ${retryAfter || '60'} segundos.`
          );
          break;
        case 500:
          toast.error('Error interno del servidor. Por favor, intenta más tarde.');
          break;
        default:
          toast.error((data as any)?.message || 'Ha ocurrido un error');
      }
    } else if (error.request) {
      // Request fue hecho pero no hay respuesta
      toast.error('No se pudo conectar con el servidor. Verifica tu conexión.');
    } else {
      // Error al configurar el request
      toast.error('Error al procesar la solicitud');
    }

    return Promise.reject(error);
  }
);

export default httpClient;
