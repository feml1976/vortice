/**
 * Custom hooks para gestión de Especificaciones Técnicas con React Query
 */

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import tireSpecificationService from '../services/tireSpecificationService';
import type {
  TireSpecification,
  TireSpecificationSummary,
  CreateTireSpecificationRequest,
  UpdateTireSpecificationRequest,
  TireSpecificationFilters,
  PageRequest,
  PageResponse,
} from '../types/tire.types';

// =====================================================
// QUERY KEYS
// =====================================================

export const tireSpecificationKeys = {
  all: ['tireSpecifications'] as const,
  lists: () => [...tireSpecificationKeys.all, 'list'] as const,
  list: (params: PageRequest) =>
    [...tireSpecificationKeys.lists(), params] as const,
  actives: () => [...tireSpecificationKeys.all, 'active'] as const,
  details: () => [...tireSpecificationKeys.all, 'detail'] as const,
  detail: (id: string) => [...tireSpecificationKeys.details(), id] as const,
  search: (query: string, params: PageRequest) =>
    [...tireSpecificationKeys.all, 'search', query, params] as const,
  filter: (filters: TireSpecificationFilters, params: PageRequest) =>
    [...tireSpecificationKeys.all, 'filter', filters, params] as const,
};

// =====================================================
// LIST QUERIES
// =====================================================

/**
 * Hook para listar especificaciones técnicas con paginación
 */
export const useTireSpecifications = (pageRequest: PageRequest) => {
  return useQuery<PageResponse<TireSpecificationSummary>, Error>({
    queryKey: tireSpecificationKeys.list(pageRequest),
    queryFn: () => tireSpecificationService.listTireSpecifications(pageRequest),
    staleTime: 30000, // 30 segundos
  });
};

/**
 * Hook para listar especificaciones técnicas activas (sin paginación)
 */
export const useActiveTireSpecifications = () => {
  return useQuery<TireSpecificationSummary[], Error>({
    queryKey: tireSpecificationKeys.actives(),
    queryFn: () => tireSpecificationService.listActiveTireSpecifications(),
    staleTime: 60000, // 1 minuto
  });
};

// =====================================================
// DETAIL QUERY
// =====================================================

/**
 * Hook para obtener una especificación técnica por ID
 */
export const useTireSpecification = (id: string | undefined) => {
  return useQuery<TireSpecification, Error>({
    queryKey: tireSpecificationKeys.detail(id!),
    queryFn: () => tireSpecificationService.getTireSpecificationById(id!),
    enabled: !!id, // Solo ejecutar si hay ID
    staleTime: 30000, // 30 segundos
  });
};

// =====================================================
// SEARCH QUERY
// =====================================================

/**
 * Hook para buscar especificaciones técnicas por texto
 */
export const useSearchTireSpecifications = (
  searchText: string,
  pageRequest: PageRequest,
  enabled: boolean = true
) => {
  return useQuery<PageResponse<TireSpecificationSummary>, Error>({
    queryKey: tireSpecificationKeys.search(searchText, pageRequest),
    queryFn: () =>
      tireSpecificationService.searchTireSpecifications(searchText, pageRequest),
    enabled: enabled && searchText.length > 0,
    staleTime: 30000, // 30 segundos
  });
};

// =====================================================
// FILTER QUERY
// =====================================================

/**
 * Hook para filtrar especificaciones técnicas
 */
export const useFilterTireSpecifications = (
  filters: TireSpecificationFilters,
  pageRequest: PageRequest
) => {
  return useQuery<PageResponse<TireSpecificationSummary>, Error>({
    queryKey: tireSpecificationKeys.filter(filters, pageRequest),
    queryFn: () =>
      tireSpecificationService.filterTireSpecifications(filters, pageRequest),
    staleTime: 30000, // 30 segundos
  });
};

// =====================================================
// CREATE MUTATION
// =====================================================

/**
 * Hook para crear una especificación técnica
 */
export const useCreateTireSpecification = () => {
  const queryClient = useQueryClient();

  return useMutation<
    TireSpecification,
    Error,
    CreateTireSpecificationRequest
  >({
    mutationFn: (data) =>
      tireSpecificationService.createTireSpecification(data),
    onSuccess: (data) => {
      // Invalidar todas las listas para refrescar
      queryClient.invalidateQueries({
        queryKey: tireSpecificationKeys.lists(),
      });
      queryClient.invalidateQueries({
        queryKey: tireSpecificationKeys.actives(),
      });

      toast.success(
        `Especificación técnica ${data.code} creada exitosamente`
      );
    },
    onError: (error) => {
      toast.error(
        `Error al crear especificación técnica: ${error.message}`
      );
    },
  });
};

// =====================================================
// UPDATE MUTATION
// =====================================================

/**
 * Hook para actualizar una especificación técnica
 */
export const useUpdateTireSpecification = () => {
  const queryClient = useQueryClient();

  return useMutation<
    TireSpecification,
    Error,
    { id: string; data: UpdateTireSpecificationRequest }
  >({
    mutationFn: ({ id, data }) =>
      tireSpecificationService.updateTireSpecification(id, data),
    onSuccess: (data, variables) => {
      // Invalidar el detalle específico
      queryClient.invalidateQueries({
        queryKey: tireSpecificationKeys.detail(variables.id),
      });

      // Invalidar las listas
      queryClient.invalidateQueries({
        queryKey: tireSpecificationKeys.lists(),
      });
      queryClient.invalidateQueries({
        queryKey: tireSpecificationKeys.actives(),
      });

      toast.success(
        `Especificación técnica ${data.code} actualizada exitosamente`
      );
    },
    onError: (error) => {
      toast.error(
        `Error al actualizar especificación técnica: ${error.message}`
      );
    },
  });
};

// =====================================================
// DELETE MUTATION
// =====================================================

/**
 * Hook para eliminar una especificación técnica
 */
export const useDeleteTireSpecification = () => {
  const queryClient = useQueryClient();

  return useMutation<void, Error, string>({
    mutationFn: (id) => tireSpecificationService.deleteTireSpecification(id),
    onSuccess: (_, id) => {
      // Remover el detalle del cache
      queryClient.removeQueries({
        queryKey: tireSpecificationKeys.detail(id),
      });

      // Invalidar las listas
      queryClient.invalidateQueries({
        queryKey: tireSpecificationKeys.lists(),
      });
      queryClient.invalidateQueries({
        queryKey: tireSpecificationKeys.actives(),
      });

      toast.success('Especificación técnica eliminada exitosamente');
    },
    onError: (error) => {
      toast.error(
        `Error al eliminar especificación técnica: ${error.message}`
      );
    },
  });
};
