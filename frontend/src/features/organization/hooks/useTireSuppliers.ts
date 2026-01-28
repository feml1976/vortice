/**
 * React Query hooks para gestión de Proveedores de Llantas
 */

import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import tireSupplierService from '../services/tireSupplierService';
import {
  TireSupplier,
  CreateTireSupplierRequest,
  UpdateTireSupplierRequest,
  TireSupplierFilters,
  PageRequest,
} from '../types/organization.types';

// Query Keys
export const tireSupplierKeys = {
  all: ['tire-suppliers'] as const,
  lists: () => [...tireSupplierKeys.all, 'list'] as const,
  list: (filters: TireSupplierFilters) => [...tireSupplierKeys.lists(), { filters }] as const,
  byOffice: (officeId: string) => [...tireSupplierKeys.lists(), 'office', officeId] as const,
  details: () => [...tireSupplierKeys.all, 'detail'] as const,
  detail: (id: string) => [...tireSupplierKeys.details(), id] as const,
  search: (filters: TireSupplierFilters, page: PageRequest) =>
    [...tireSupplierKeys.all, 'search', { filters, page }] as const,
};

/**
 * Hook para listar todos los proveedores accesibles
 * RLS filtra automáticamente por oficina del usuario
 */
export const useTireSuppliers = (filters: TireSupplierFilters = {}) => {
  return useQuery({
    queryKey: tireSupplierKeys.list(filters),
    queryFn: () => tireSupplierService.listAll(),
  });
};

/**
 * Hook para listar proveedores por oficina
 */
export const useTireSuppliersByOffice = (officeId: string, options?: { enabled?: boolean }) => {
  return useQuery({
    queryKey: tireSupplierKeys.byOffice(officeId),
    queryFn: () => tireSupplierService.listByOffice(officeId),
    enabled: options?.enabled ?? !!officeId,
  });
};

/**
 * Hook para obtener un proveedor por ID
 */
export const useTireSupplier = (id: string, options?: { enabled?: boolean }) => {
  return useQuery({
    queryKey: tireSupplierKeys.detail(id),
    queryFn: () => tireSupplierService.getById(id),
    enabled: options?.enabled ?? !!id,
  });
};

/**
 * Hook para buscar proveedores con paginación
 */
export const useTireSuppliersSearch = (
  filters: TireSupplierFilters,
  pageRequest: PageRequest
) => {
  return useQuery({
    queryKey: tireSupplierKeys.search(filters, pageRequest),
    queryFn: () => tireSupplierService.search(filters, pageRequest),
  });
};

/**
 * Hook para crear un nuevo proveedor
 */
export const useCreateTireSupplier = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateTireSupplierRequest) => tireSupplierService.create(data),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: tireSupplierKeys.lists() });
      queryClient.invalidateQueries({ queryKey: tireSupplierKeys.byOffice(data.officeId) });
      toast.success('Proveedor creado exitosamente');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Error al crear el proveedor');
    },
  });
};

/**
 * Hook para actualizar un proveedor
 */
export const useUpdateTireSupplier = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: UpdateTireSupplierRequest }) =>
      tireSupplierService.update(id, data),
    onSuccess: (data, { id }) => {
      queryClient.invalidateQueries({ queryKey: tireSupplierKeys.lists() });
      queryClient.invalidateQueries({ queryKey: tireSupplierKeys.detail(id) });
      queryClient.invalidateQueries({ queryKey: tireSupplierKeys.byOffice(data.officeId) });
      toast.success('Proveedor actualizado exitosamente');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Error al actualizar el proveedor');
    },
  });
};

/**
 * Hook para eliminar un proveedor
 */
export const useDeleteTireSupplier = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: string) => tireSupplierService.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: tireSupplierKeys.lists() });
      toast.success('Proveedor eliminado exitosamente');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Error al eliminar el proveedor');
    },
  });
};

/**
 * Hook para activar/desactivar un proveedor
 */
export const useToggleTireSupplierActive = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, active }: { id: string; active: boolean }) =>
      tireSupplierService.setActive(id, active),
    onSuccess: (data, { id, active }) => {
      queryClient.invalidateQueries({ queryKey: tireSupplierKeys.lists() });
      queryClient.invalidateQueries({ queryKey: tireSupplierKeys.detail(id) });
      queryClient.invalidateQueries({ queryKey: tireSupplierKeys.byOffice(data.officeId) });
      toast.success(`Proveedor ${active ? 'activado' : 'desactivado'} exitosamente`);
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Error al cambiar el estado del proveedor');
    },
  });
};
