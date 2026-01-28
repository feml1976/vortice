/**
 * Servicio para operaciones con Proveedores de Llantas
 */

import httpClient from '@/shared/services/httpClient';
import {
  TireSupplier,
  CreateTireSupplierRequest,
  UpdateTireSupplierRequest,
  TireSupplierFilters,
  PageRequest,
  PageResponse,
} from '../types/organization.types';

const BASE_URL = '/v1/tire-suppliers';

export const tireSupplierService = {
  /**
   * Crear un nuevo proveedor de llantas
   * Administradores de oficina y nacionales
   */
  create: async (data: CreateTireSupplierRequest): Promise<TireSupplier> => {
    const response = await httpClient.post<TireSupplier>(BASE_URL, data);
    return response.data;
  },

  /**
   * Actualizar un proveedor existente
   * Administradores de oficina y nacionales
   */
  update: async (id: string, data: UpdateTireSupplierRequest): Promise<TireSupplier> => {
    const response = await httpClient.put<TireSupplier>(`${BASE_URL}/${id}`, data);
    return response.data;
  },

  /**
   * Eliminar un proveedor (soft delete)
   * Administradores de oficina y nacionales
   */
  delete: async (id: string): Promise<void> => {
    await httpClient.delete(`${BASE_URL}/${id}`);
  },

  /**
   * Obtener un proveedor por ID
   * RLS filtra automáticamente por oficina del usuario
   */
  getById: async (id: string): Promise<TireSupplier> => {
    const response = await httpClient.get<TireSupplier>(`${BASE_URL}/${id}`);
    return response.data;
  },

  /**
   * Obtener un proveedor por código y oficina
   */
  getByCode: async (code: string, officeId: string): Promise<TireSupplier> => {
    const response = await httpClient.get<TireSupplier>(`${BASE_URL}/by-code/${code}`, {
      params: { officeId },
    });
    return response.data;
  },

  /**
   * Obtener proveedores por NIT
   * Puede retornar múltiples si el mismo NIT está en diferentes oficinas
   */
  getByTaxId: async (taxId: string): Promise<TireSupplier[]> => {
    const response = await httpClient.get<TireSupplier[]>(`${BASE_URL}/by-tax-id/${taxId}`);
    return response.data;
  },

  /**
   * Listar todos los proveedores accesibles
   * RLS filtra automáticamente por oficina del usuario
   */
  listAll: async (): Promise<TireSupplier[]> => {
    const response = await httpClient.get<TireSupplier[]>(BASE_URL);
    return response.data;
  },

  /**
   * Listar proveedores por oficina
   * Valida que el usuario tenga acceso a la oficina
   */
  listByOffice: async (officeId: string): Promise<TireSupplier[]> => {
    const response = await httpClient.get<TireSupplier[]>(`${BASE_URL}/by-office/${officeId}`);
    return response.data;
  },

  /**
   * Buscar proveedores con paginación y filtros
   * RLS filtra automáticamente por oficina del usuario
   */
  search: async (
    filters: TireSupplierFilters,
    pageRequest: PageRequest
  ): Promise<PageResponse<TireSupplier>> => {
    const params = {
      q: filters.searchText,
      page: pageRequest.page,
      size: pageRequest.size,
      sort: pageRequest.sort,
      direction: pageRequest.direction,
    };

    const response = await httpClient.get<PageResponse<TireSupplier>>(`${BASE_URL}/search`, {
      params,
    });
    return response.data;
  },

  /**
   * Activar o desactivar un proveedor
   */
  setActive: async (id: string, active: boolean): Promise<TireSupplier> => {
    const response = await httpClient.patch<TireSupplier>(`${BASE_URL}/${id}/active`, null, {
      params: { active },
    });
    return response.data;
  },
};

export default tireSupplierService;
