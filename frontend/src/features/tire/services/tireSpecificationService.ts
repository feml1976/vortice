/**
 * Servicio para gestión de Especificaciones Técnicas de Llantas
 */

import httpClient from '@/shared/services/httpClient';
import type {
  TireSpecification,
  TireSpecificationSummary,
  CreateTireSpecificationRequest,
  UpdateTireSpecificationRequest,
  TireSpecificationFilters,
  PageRequest,
  PageResponse,
} from '../types/tire.types';

const API_URL = '/v1/tire-specifications';

/**
 * Crea una nueva especificación técnica
 */
export const createTireSpecification = async (
  data: CreateTireSpecificationRequest
): Promise<TireSpecification> => {
  const response = await httpClient.post<TireSpecification>(API_URL, data);
  return response.data;
};

/**
 * Lista todas las especificaciones técnicas con paginación
 */
export const listTireSpecifications = async (
  pageRequest: PageRequest
): Promise<PageResponse<TireSpecificationSummary>> => {
  const { page, size, sort = 'code', direction = 'asc' } = pageRequest;
  const response = await httpClient.get<PageResponse<TireSpecificationSummary>>(
    API_URL,
    {
      params: {
        page,
        size,
        sort,
        direction,
      },
    }
  );
  return response.data;
};

/**
 * Lista todas las especificaciones técnicas activas sin paginación
 */
export const listActiveTireSpecifications = async (): Promise<
  TireSpecificationSummary[]
> => {
  const response = await httpClient.get<TireSpecificationSummary[]>(
    `${API_URL}/active`
  );
  return response.data;
};

/**
 * Obtiene una especificación técnica por su ID
 */
export const getTireSpecificationById = async (
  id: string
): Promise<TireSpecification> => {
  const response = await httpClient.get<TireSpecification>(`${API_URL}/${id}`);
  return response.data;
};

/**
 * Actualiza una especificación técnica existente
 */
export const updateTireSpecification = async (
  id: string,
  data: UpdateTireSpecificationRequest
): Promise<TireSpecification> => {
  const response = await httpClient.put<TireSpecification>(
    `${API_URL}/${id}`,
    data
  );
  return response.data;
};

/**
 * Elimina una especificación técnica (soft delete)
 */
export const deleteTireSpecification = async (id: string): Promise<void> => {
  await httpClient.delete(`${API_URL}/${id}`);
};

/**
 * Busca especificaciones técnicas por texto libre
 */
export const searchTireSpecifications = async (
  searchText: string,
  pageRequest: PageRequest
): Promise<PageResponse<TireSpecificationSummary>> => {
  const { page, size } = pageRequest;
  const response = await httpClient.get<PageResponse<TireSpecificationSummary>>(
    `${API_URL}/search`,
    {
      params: {
        query: searchText,
        page,
        size,
      },
    }
  );
  return response.data;
};

/**
 * Filtra especificaciones técnicas con múltiples criterios
 */
export const filterTireSpecifications = async (
  filters: TireSpecificationFilters,
  pageRequest: PageRequest
): Promise<PageResponse<TireSpecificationSummary>> => {
  const { page, size, sort = 'code', direction = 'asc' } = pageRequest;
  const response = await httpClient.get<PageResponse<TireSpecificationSummary>>(
    `${API_URL}/filter`,
    {
      params: {
        ...filters,
        page,
        size,
        sort,
        direction,
      },
    }
  );
  return response.data;
};

const tireSpecificationService = {
  createTireSpecification,
  listTireSpecifications,
  listActiveTireSpecifications,
  getTireSpecificationById,
  updateTireSpecification,
  deleteTireSpecification,
  searchTireSpecifications,
  filterTireSpecifications,
};

export default tireSpecificationService;
