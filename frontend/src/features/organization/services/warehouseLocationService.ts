/**
 * Servicio para operaciones con Ubicaciones de Almacén
 */

import httpClient from '@/shared/services/httpClient';
import {
  WarehouseLocation,
  CreateWarehouseLocationRequest,
  UpdateWarehouseLocationRequest,
  WarehouseLocationFilters,
  PageRequest,
  PageResponse,
} from '../types/organization.types';

const BASE_URL = '/v1/warehouse-locations';

export const warehouseLocationService = {
  /**
   * Crear una nueva ubicación de almacén
   * Gerentes de almacén, administradores de oficina y nacionales
   */
  create: async (data: CreateWarehouseLocationRequest): Promise<WarehouseLocation> => {
    const response = await httpClient.post<WarehouseLocation>(BASE_URL, data);
    return response.data;
  },

  /**
   * Actualizar una ubicación existente
   * Gerentes de almacén, administradores de oficina y nacionales
   */
  update: async (
    id: string,
    data: UpdateWarehouseLocationRequest
  ): Promise<WarehouseLocation> => {
    const response = await httpClient.put<WarehouseLocation>(`${BASE_URL}/${id}`, data);
    return response.data;
  },

  /**
   * Eliminar una ubicación (soft delete)
   * Gerentes de almacén, administradores de oficina y nacionales
   */
  delete: async (id: string): Promise<void> => {
    await httpClient.delete(`${BASE_URL}/${id}`);
  },

  /**
   * Obtener una ubicación por ID
   * RLS filtra automáticamente por oficina del usuario
   */
  getById: async (id: string): Promise<WarehouseLocation> => {
    const response = await httpClient.get<WarehouseLocation>(`${BASE_URL}/${id}`);
    return response.data;
  },

  /**
   * Obtener una ubicación por código y almacén
   */
  getByCode: async (code: string, warehouseId: string): Promise<WarehouseLocation> => {
    const response = await httpClient.get<WarehouseLocation>(`${BASE_URL}/by-code/${code}`, {
      params: { warehouseId },
    });
    return response.data;
  },

  /**
   * Listar todas las ubicaciones accesibles
   * RLS filtra automáticamente por oficina del usuario
   */
  listAll: async (): Promise<WarehouseLocation[]> => {
    const response = await httpClient.get<WarehouseLocation[]>(BASE_URL);
    return response.data;
  },

  /**
   * Listar ubicaciones por almacén
   * Valida que el usuario tenga acceso al almacén
   */
  listByWarehouse: async (warehouseId: string): Promise<WarehouseLocation[]> => {
    const response = await httpClient.get<WarehouseLocation[]>(
      `${BASE_URL}/by-warehouse/${warehouseId}`
    );
    return response.data;
  },

  /**
   * Buscar ubicaciones con paginación y filtros
   * RLS filtra automáticamente por oficina del usuario
   */
  search: async (
    filters: WarehouseLocationFilters,
    pageRequest: PageRequest
  ): Promise<PageResponse<WarehouseLocation>> => {
    const params = {
      q: filters.searchText,
      page: pageRequest.page,
      size: pageRequest.size,
      sort: pageRequest.sort,
      direction: pageRequest.direction,
    };

    const response = await httpClient.get<PageResponse<WarehouseLocation>>(
      `${BASE_URL}/search`,
      { params }
    );
    return response.data;
  },

  /**
   * Activar o desactivar una ubicación
   */
  setActive: async (id: string, active: boolean): Promise<WarehouseLocation> => {
    const response = await httpClient.patch<WarehouseLocation>(
      `${BASE_URL}/${id}/active`,
      null,
      {
        params: { active },
      }
    );
    return response.data;
  },
};

export default warehouseLocationService;
