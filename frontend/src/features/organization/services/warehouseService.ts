/**
 * Servicio para operaciones con Almacenes
 */

import httpClient from '@/shared/services/httpClient';
import {
  Warehouse,
  CreateWarehouseRequest,
  UpdateWarehouseRequest,
  WarehouseFilters,
  PageRequest,
  PageResponse,
} from '../types/organization.types';

const BASE_URL = '/v1/warehouses';

export const warehouseService = {
  /**
   * Crear un nuevo almacén
   * Administradores de oficina y nacionales
   */
  create: async (data: CreateWarehouseRequest): Promise<Warehouse> => {
    const response = await httpClient.post<Warehouse>(BASE_URL, data);
    return response.data;
  },

  /**
   * Actualizar un almacén existente
   * Administradores de oficina y nacionales
   */
  update: async (id: string, data: UpdateWarehouseRequest): Promise<Warehouse> => {
    const response = await httpClient.put<Warehouse>(`${BASE_URL}/${id}`, data);
    return response.data;
  },

  /**
   * Eliminar un almacén (soft delete)
   * Administradores de oficina y nacionales
   */
  delete: async (id: string): Promise<void> => {
    await httpClient.delete(`${BASE_URL}/${id}`);
  },

  /**
   * Obtener un almacén por ID
   * RLS filtra automáticamente por oficina del usuario
   */
  getById: async (id: string): Promise<Warehouse> => {
    const response = await httpClient.get<Warehouse>(`${BASE_URL}/${id}`);
    return response.data;
  },

  /**
   * Obtener un almacén por código y oficina
   */
  getByCode: async (code: string, officeId: string): Promise<Warehouse> => {
    const response = await httpClient.get<Warehouse>(`${BASE_URL}/by-code/${code}`, {
      params: { officeId },
    });
    return response.data;
  },

  /**
   * Listar todos los almacenes accesibles
   * RLS filtra automáticamente por oficina del usuario
   */
  listAll: async (): Promise<Warehouse[]> => {
    const response = await httpClient.get<Warehouse[]>(BASE_URL);
    return response.data;
  },

  /**
   * Listar almacenes por oficina
   * Valida que el usuario tenga acceso a la oficina
   */
  listByOffice: async (officeId: string): Promise<Warehouse[]> => {
    const response = await httpClient.get<Warehouse[]>(`${BASE_URL}/by-office/${officeId}`);
    return response.data;
  },

  /**
   * Buscar almacenes con paginación y filtros
   * RLS filtra automáticamente por oficina del usuario
   */
  search: async (
    filters: WarehouseFilters,
    pageRequest: PageRequest
  ): Promise<PageResponse<Warehouse>> => {
    const params = {
      q: filters.searchText,
      page: pageRequest.page,
      size: pageRequest.size,
      sort: pageRequest.sort,
      direction: pageRequest.direction,
    };

    const response = await httpClient.get<PageResponse<Warehouse>>(`${BASE_URL}/search`, {
      params,
    });
    return response.data;
  },

  /**
   * Obtener almacén con información detallada (totales)
   */
  getWithDetails: async (id: string): Promise<Warehouse> => {
    const response = await httpClient.get<Warehouse>(`${BASE_URL}/${id}/details`);
    return response.data;
  },

  /**
   * Activar o desactivar un almacén
   */
  setActive: async (id: string, active: boolean): Promise<Warehouse> => {
    const response = await httpClient.patch<Warehouse>(`${BASE_URL}/${id}/active`, null, {
      params: { active },
    });
    return response.data;
  },
};

export default warehouseService;
