/**
 * Servicio para operaciones con Oficinas
 */

import httpClient from '@/shared/services/httpClient';
import {
  Office,
  CreateOfficeRequest,
  UpdateOfficeRequest,
  OfficeFilters,
  PageRequest,
  PageResponse,
} from '../types/organization.types';

const BASE_URL = '/v1/offices';

export const officeService = {
  /**
   * Crear una nueva oficina
   * Solo administradores nacionales
   */
  create: async (data: CreateOfficeRequest): Promise<Office> => {
    const response = await httpClient.post<Office>(BASE_URL, data);
    return response.data;
  },

  /**
   * Actualizar una oficina existente
   * Solo administradores nacionales
   */
  update: async (id: string, data: UpdateOfficeRequest): Promise<Office> => {
    const response = await httpClient.put<Office>(`${BASE_URL}/${id}`, data);
    return response.data;
  },

  /**
   * Eliminar una oficina (soft delete)
   * Solo administradores nacionales
   */
  delete: async (id: string): Promise<void> => {
    await httpClient.delete(`${BASE_URL}/${id}`);
  },

  /**
   * Obtener una oficina por ID
   */
  getById: async (id: string): Promise<Office> => {
    const response = await httpClient.get<Office>(`${BASE_URL}/${id}`);
    return response.data;
  },

  /**
   * Obtener una oficina por código
   */
  getByCode: async (code: string): Promise<Office> => {
    const response = await httpClient.get<Office>(`${BASE_URL}/by-code/${code}`);
    return response.data;
  },

  /**
   * Listar todas las oficinas accesibles
   * Admin nacional ve todas, usuarios normales solo ven su oficina
   */
  listAll: async (): Promise<Office[]> => {
    const response = await httpClient.get<Office[]>(BASE_URL);
    return response.data;
  },

  /**
   * Buscar oficinas con paginación y filtros
   * Solo administradores nacionales
   */
  search: async (
    filters: OfficeFilters,
    pageRequest: PageRequest
  ): Promise<PageResponse<Office>> => {
    const params = {
      q: filters.searchText,
      page: pageRequest.page,
      size: pageRequest.size,
      sort: pageRequest.sort,
      direction: pageRequest.direction,
    };

    const response = await httpClient.get<PageResponse<Office>>(`${BASE_URL}/search`, {
      params,
    });
    return response.data;
  },

  /**
   * Obtener oficina con información detallada (totales)
   * Solo administradores nacionales
   */
  getWithDetails: async (id: string): Promise<Office> => {
    const response = await httpClient.get<Office>(`${BASE_URL}/${id}/details`);
    return response.data;
  },

  /**
   * Activar o desactivar una oficina
   * Solo administradores nacionales
   */
  setActive: async (id: string, active: boolean): Promise<Office> => {
    const response = await httpClient.patch<Office>(`${BASE_URL}/${id}/active`, null, {
      params: { active },
    });
    return response.data;
  },
};

export default officeService;
