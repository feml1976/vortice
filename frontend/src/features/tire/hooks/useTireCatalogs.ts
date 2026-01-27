/**
 * Custom hooks para gestión de Catálogos del Módulo de Llantas con React Query
 */

import { useQuery } from '@tanstack/react-query';
import tireCatalogService from '../services/tireCatalogService';
import type {
  TireBrand,
  TireType,
  TireReference,
  TireSupplier,
} from '../types/tire.types';

// =====================================================
// QUERY KEYS
// =====================================================

export const tireCatalogKeys = {
  all: ['tireCatalogs'] as const,
  brands: () => [...tireCatalogKeys.all, 'brands'] as const,
  activeBrands: () => [...tireCatalogKeys.brands(), 'active'] as const,
  allBrands: () => [...tireCatalogKeys.brands(), 'all'] as const,
  types: () => [...tireCatalogKeys.all, 'types'] as const,
  activeTypes: () => [...tireCatalogKeys.types(), 'active'] as const,
  allTypes: () => [...tireCatalogKeys.types(), 'all'] as const,
  references: () => [...tireCatalogKeys.all, 'references'] as const,
  activeReferences: () => [...tireCatalogKeys.references(), 'active'] as const,
  allReferences: () => [...tireCatalogKeys.references(), 'all'] as const,
  suppliers: () => [...tireCatalogKeys.all, 'suppliers'] as const,
  activeSuppliers: () => [...tireCatalogKeys.suppliers(), 'active'] as const,
  allSuppliers: () => [...tireCatalogKeys.suppliers(), 'all'] as const,
};

// =====================================================
// MARCAS DE LLANTAS
// =====================================================

/**
 * Hook para obtener todas las marcas activas
 */
export const useActiveTireBrands = () => {
  return useQuery<TireBrand[], Error>({
    queryKey: tireCatalogKeys.activeBrands(),
    queryFn: () => tireCatalogService.getAllActiveBrands(),
    staleTime: 300000, // 5 minutos (los catálogos cambian poco)
  });
};

/**
 * Hook para obtener todas las marcas (activas e inactivas)
 */
export const useAllTireBrands = () => {
  return useQuery<TireBrand[], Error>({
    queryKey: tireCatalogKeys.allBrands(),
    queryFn: () => tireCatalogService.getAllBrands(),
    staleTime: 300000, // 5 minutos
  });
};

// =====================================================
// TIPOS DE LLANTAS
// =====================================================

/**
 * Hook para obtener todos los tipos activos
 */
export const useActiveTireTypes = () => {
  return useQuery<TireType[], Error>({
    queryKey: tireCatalogKeys.activeTypes(),
    queryFn: () => tireCatalogService.getAllActiveTypes(),
    staleTime: 300000, // 5 minutos
  });
};

/**
 * Hook para obtener todos los tipos (activos e inactivos)
 */
export const useAllTireTypes = () => {
  return useQuery<TireType[], Error>({
    queryKey: tireCatalogKeys.allTypes(),
    queryFn: () => tireCatalogService.getAllTypes(),
    staleTime: 300000, // 5 minutos
  });
};

// =====================================================
// REFERENCIAS DE LLANTAS
// =====================================================

/**
 * Hook para obtener todas las referencias activas
 */
export const useActiveTireReferences = () => {
  return useQuery<TireReference[], Error>({
    queryKey: tireCatalogKeys.activeReferences(),
    queryFn: () => tireCatalogService.getAllActiveReferences(),
    staleTime: 300000, // 5 minutos
  });
};

/**
 * Hook para obtener todas las referencias (activas e inactivas)
 */
export const useAllTireReferences = () => {
  return useQuery<TireReference[], Error>({
    queryKey: tireCatalogKeys.allReferences(),
    queryFn: () => tireCatalogService.getAllReferences(),
    staleTime: 300000, // 5 minutos
  });
};

// =====================================================
// PROVEEDORES DE LLANTAS
// =====================================================

/**
 * Hook para obtener todos los proveedores activos
 */
export const useActiveTireSuppliers = () => {
  return useQuery<TireSupplier[], Error>({
    queryKey: tireCatalogKeys.activeSuppliers(),
    queryFn: () => tireCatalogService.getAllActiveSuppliers(),
    staleTime: 300000, // 5 minutos
  });
};

/**
 * Hook para obtener todos los proveedores (activos e inactivos)
 */
export const useAllTireSuppliers = () => {
  return useQuery<TireSupplier[], Error>({
    queryKey: tireCatalogKeys.allSuppliers(),
    queryFn: () => tireCatalogService.getAllSuppliers(),
    staleTime: 300000, // 5 minutos
  });
};

// =====================================================
// HOOK COMBINADO PARA FORMULARIOS
// =====================================================

/**
 * Hook que carga todos los catálogos necesarios para el formulario
 * de especificación técnica en una sola llamada
 */
export const useTireFormCatalogs = () => {
  const brands = useActiveTireBrands();
  const types = useActiveTireTypes();
  const references = useActiveTireReferences();
  const suppliers = useActiveTireSuppliers();

  return {
    brands: brands.data || [],
    types: types.data || [],
    references: references.data || [],
    suppliers: suppliers.data || [],
    isLoading:
      brands.isLoading ||
      types.isLoading ||
      references.isLoading ||
      suppliers.isLoading,
    isError:
      brands.isError || types.isError || references.isError || suppliers.isError,
    error: brands.error || types.error || references.error || suppliers.error,
  };
};
