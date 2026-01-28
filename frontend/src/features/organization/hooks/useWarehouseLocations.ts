/**
 * React Query hooks para gestión de Ubicaciones de Almacén
 */

import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import warehouseLocationService from '../services/warehouseLocationService';
import {
  WarehouseLocation,
  CreateWarehouseLocationRequest,
  UpdateWarehouseLocationRequest,
  WarehouseLocationFilters,
  PageRequest,
} from '../types/organization.types';

// Query Keys
export const warehouseLocationKeys = {
  all: ['warehouse-locations'] as const,
  lists: () => [...warehouseLocationKeys.all, 'list'] as const,
  list: (filters: WarehouseLocationFilters) =>
    [...warehouseLocationKeys.lists(), { filters }] as const,
  byWarehouse: (warehouseId: string) =>
    [...warehouseLocationKeys.lists(), 'warehouse', warehouseId] as const,
  details: () => [...warehouseLocationKeys.all, 'detail'] as const,
  detail: (id: string) => [...warehouseLocationKeys.details(), id] as const,
  search: (filters: WarehouseLocationFilters, page: PageRequest) =>
    [...warehouseLocationKeys.all, 'search', { filters, page }] as const,
};

/**
 * Hook para listar todas las ubicaciones accesibles
 * RLS filtra automáticamente por oficina del usuario
 */
export const useWarehouseLocations = (filters: WarehouseLocationFilters = {}) => {
  return useQuery({
    queryKey: warehouseLocationKeys.list(filters),
    queryFn: () => warehouseLocationService.listAll(),
  });
};

/**
 * Hook para listar ubicaciones por almacén
 */
export const useWarehouseLocationsByWarehouse = (
  warehouseId: string,
  options?: { enabled?: boolean }
) => {
  return useQuery({
    queryKey: warehouseLocationKeys.byWarehouse(warehouseId),
    queryFn: () => warehouseLocationService.listByWarehouse(warehouseId),
    enabled: options?.enabled ?? !!warehouseId,
  });
};

/**
 * Hook para obtener una ubicación por ID
 */
export const useWarehouseLocation = (id: string, options?: { enabled?: boolean }) => {
  return useQuery({
    queryKey: warehouseLocationKeys.detail(id),
    queryFn: () => warehouseLocationService.getById(id),
    enabled: options?.enabled ?? !!id,
  });
};

/**
 * Hook para buscar ubicaciones con paginación
 */
export const useWarehouseLocationsSearch = (
  filters: WarehouseLocationFilters,
  pageRequest: PageRequest
) => {
  return useQuery({
    queryKey: warehouseLocationKeys.search(filters, pageRequest),
    queryFn: () => warehouseLocationService.search(filters, pageRequest),
  });
};

/**
 * Hook para crear una nueva ubicación
 */
export const useCreateWarehouseLocation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateWarehouseLocationRequest) => warehouseLocationService.create(data),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: warehouseLocationKeys.lists() });
      queryClient.invalidateQueries({
        queryKey: warehouseLocationKeys.byWarehouse(data.warehouseId),
      });
      toast.success('Ubicación creada exitosamente');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Error al crear la ubicación');
    },
  });
};

/**
 * Hook para actualizar una ubicación
 */
export const useUpdateWarehouseLocation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: UpdateWarehouseLocationRequest }) =>
      warehouseLocationService.update(id, data),
    onSuccess: (data, { id }) => {
      queryClient.invalidateQueries({ queryKey: warehouseLocationKeys.lists() });
      queryClient.invalidateQueries({ queryKey: warehouseLocationKeys.detail(id) });
      queryClient.invalidateQueries({
        queryKey: warehouseLocationKeys.byWarehouse(data.warehouseId),
      });
      toast.success('Ubicación actualizada exitosamente');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Error al actualizar la ubicación');
    },
  });
};

/**
 * Hook para eliminar una ubicación
 */
export const useDeleteWarehouseLocation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: string) => warehouseLocationService.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: warehouseLocationKeys.lists() });
      toast.success('Ubicación eliminada exitosamente');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Error al eliminar la ubicación');
    },
  });
};

/**
 * Hook para activar/desactivar una ubicación
 */
export const useToggleWarehouseLocationActive = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, active }: { id: string; active: boolean }) =>
      warehouseLocationService.setActive(id, active),
    onSuccess: (data, { id, active }) => {
      queryClient.invalidateQueries({ queryKey: warehouseLocationKeys.lists() });
      queryClient.invalidateQueries({ queryKey: warehouseLocationKeys.detail(id) });
      queryClient.invalidateQueries({
        queryKey: warehouseLocationKeys.byWarehouse(data.warehouseId),
      });
      toast.success(`Ubicación ${active ? 'activada' : 'desactivada'} exitosamente`);
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Error al cambiar el estado de la ubicación');
    },
  });
};
