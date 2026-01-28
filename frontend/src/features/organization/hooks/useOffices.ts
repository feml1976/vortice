/**
 * React Query hooks para gestión de Oficinas
 */

import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import officeService from '../services/officeService';
import {
  Office,
  CreateOfficeRequest,
  UpdateOfficeRequest,
  OfficeFilters,
  PageRequest,
} from '../types/organization.types';

// Query Keys
export const officeKeys = {
  all: ['offices'] as const,
  lists: () => [...officeKeys.all, 'list'] as const,
  list: (filters: OfficeFilters) => [...officeKeys.lists(), { filters }] as const,
  details: () => [...officeKeys.all, 'detail'] as const,
  detail: (id: string) => [...officeKeys.details(), id] as const,
  search: (filters: OfficeFilters, page: PageRequest) =>
    [...officeKeys.all, 'search', { filters, page }] as const,
};

/**
 * Hook para listar todas las oficinas accesibles
 */
export const useOffices = (filters: OfficeFilters = {}) => {
  return useQuery({
    queryKey: officeKeys.list(filters),
    queryFn: () => officeService.listAll(),
  });
};

/**
 * Hook para obtener una oficina por ID
 */
export const useOffice = (id: string, options?: { enabled?: boolean }) => {
  return useQuery({
    queryKey: officeKeys.detail(id),
    queryFn: () => officeService.getById(id),
    enabled: options?.enabled ?? !!id,
  });
};

/**
 * Hook para obtener una oficina con detalles (incluye totales)
 */
export const useOfficeWithDetails = (id: string, options?: { enabled?: boolean }) => {
  return useQuery({
    queryKey: [...officeKeys.detail(id), 'details'],
    queryFn: () => officeService.getWithDetails(id),
    enabled: options?.enabled ?? !!id,
  });
};

/**
 * Hook para buscar oficinas con paginación
 */
export const useOfficesSearch = (filters: OfficeFilters, pageRequest: PageRequest) => {
  return useQuery({
    queryKey: officeKeys.search(filters, pageRequest),
    queryFn: () => officeService.search(filters, pageRequest),
  });
};

/**
 * Hook para crear una nueva oficina
 */
export const useCreateOffice = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateOfficeRequest) => officeService.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: officeKeys.lists() });
      toast.success('Oficina creada exitosamente');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Error al crear la oficina');
    },
  });
};

/**
 * Hook para actualizar una oficina
 */
export const useUpdateOffice = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: UpdateOfficeRequest }) =>
      officeService.update(id, data),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: officeKeys.lists() });
      queryClient.invalidateQueries({ queryKey: officeKeys.detail(id) });
      toast.success('Oficina actualizada exitosamente');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Error al actualizar la oficina');
    },
  });
};

/**
 * Hook para eliminar una oficina
 */
export const useDeleteOffice = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: string) => officeService.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: officeKeys.lists() });
      toast.success('Oficina eliminada exitosamente');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Error al eliminar la oficina');
    },
  });
};

/**
 * Hook para activar/desactivar una oficina
 */
export const useToggleOfficeActive = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, active }: { id: string; active: boolean }) =>
      officeService.setActive(id, active),
    onSuccess: (_, { id, active }) => {
      queryClient.invalidateQueries({ queryKey: officeKeys.lists() });
      queryClient.invalidateQueries({ queryKey: officeKeys.detail(id) });
      toast.success(`Oficina ${active ? 'activada' : 'desactivada'} exitosamente`);
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Error al cambiar el estado de la oficina');
    },
  });
};
