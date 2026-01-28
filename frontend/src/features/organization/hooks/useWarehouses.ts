/**
 * React Query hooks para gestión de Almacenes
 */

import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import warehouseService from '../services/warehouseService';
import {
  Warehouse,
  CreateWarehouseRequest,
  UpdateWarehouseRequest,
  WarehouseFilters,
  PageRequest,
} from '../types/organization.types';

// Query Keys
export const warehouseKeys = {
  all: ['warehouses'] as const,
  lists: () => [...warehouseKeys.all, 'list'] as const,
  list: (filters: WarehouseFilters) => [...warehouseKeys.lists(), { filters }] as const,
  byOffice: (officeId: string) => [...warehouseKeys.lists(), 'office', officeId] as const,
  details: () => [...warehouseKeys.all, 'detail'] as const,
  detail: (id: string) => [...warehouseKeys.details(), id] as const,
  search: (filters: WarehouseFilters, page: PageRequest) =>
    [...warehouseKeys.all, 'search', { filters, page }] as const,
};

/**
 * Hook para listar todos los almacenes accesibles
 * RLS filtra automáticamente por oficina del usuario
 */
export const useWarehouses = (filters: WarehouseFilters = {}) => {
  return useQuery({
    queryKey: warehouseKeys.list(filters),
    queryFn: () => warehouseService.listAll(),
  });
};

/**
 * Hook para listar almacenes por oficina
 */
export const useWarehousesByOffice = (officeId: string, options?: { enabled?: boolean }) => {
  return useQuery({
    queryKey: warehouseKeys.byOffice(officeId),
    queryFn: () => warehouseService.listByOffice(officeId),
    enabled: options?.enabled ?? !!officeId,
  });
};

/**
 * Hook para obtener un almacén por ID
 */
export const useWarehouse = (id: string, options?: { enabled?: boolean }) => {
  return useQuery({
    queryKey: warehouseKeys.detail(id),
    queryFn: () => warehouseService.getById(id),
    enabled: options?.enabled ?? !!id,
  });
};

/**
 * Hook para obtener un almacén con detalles (incluye totales)
 */
export const useWarehouseWithDetails = (id: string, options?: { enabled?: boolean }) => {
  return useQuery({
    queryKey: [...warehouseKeys.detail(id), 'details'],
    queryFn: () => warehouseService.getWithDetails(id),
    enabled: options?.enabled ?? !!id,
  });
};

/**
 * Hook para buscar almacenes con paginación
 */
export const useWarehousesSearch = (filters: WarehouseFilters, pageRequest: PageRequest) => {
  return useQuery({
    queryKey: warehouseKeys.search(filters, pageRequest),
    queryFn: () => warehouseService.search(filters, pageRequest),
  });
};

/**
 * Hook para crear un nuevo almacén
 */
export const useCreateWarehouse = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateWarehouseRequest) => warehouseService.create(data),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: warehouseKeys.lists() });
      queryClient.invalidateQueries({ queryKey: warehouseKeys.byOffice(data.officeId) });
      toast.success('Almacén creado exitosamente');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Error al crear el almacén');
    },
  });
};

/**
 * Hook para actualizar un almacén
 */
export const useUpdateWarehouse = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: UpdateWarehouseRequest }) =>
      warehouseService.update(id, data),
    onSuccess: (data, { id }) => {
      queryClient.invalidateQueries({ queryKey: warehouseKeys.lists() });
      queryClient.invalidateQueries({ queryKey: warehouseKeys.detail(id) });
      queryClient.invalidateQueries({ queryKey: warehouseKeys.byOffice(data.officeId) });
      toast.success('Almacén actualizado exitosamente');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Error al actualizar el almacén');
    },
  });
};

/**
 * Hook para eliminar un almacén
 */
export const useDeleteWarehouse = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: string) => warehouseService.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: warehouseKeys.lists() });
      toast.success('Almacén eliminado exitosamente');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Error al eliminar el almacén');
    },
  });
};

/**
 * Hook para activar/desactivar un almacén
 */
export const useToggleWarehouseActive = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, active }: { id: string; active: boolean }) =>
      warehouseService.setActive(id, active),
    onSuccess: (data, { id, active }) => {
      queryClient.invalidateQueries({ queryKey: warehouseKeys.lists() });
      queryClient.invalidateQueries({ queryKey: warehouseKeys.detail(id) });
      queryClient.invalidateQueries({ queryKey: warehouseKeys.byOffice(data.officeId) });
      toast.success(`Almacén ${active ? 'activado' : 'desactivado'} exitosamente`);
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Error al cambiar el estado del almacén');
    },
  });
};
