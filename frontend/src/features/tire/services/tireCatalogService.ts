/**
 * Servicio para gestión de Catálogos del Módulo de Llantas
 */

import httpClient from '@/shared/services/httpClient';
import type {
  TireBrand,
  TireType,
  TireReference,
  TireSupplier,
} from '../types/tire.types';

const API_URL = '/v1/tire-catalogs';

// =====================================================
// MARCAS DE LLANTAS
// =====================================================

/**
 * Obtiene todas las marcas activas
 */
export const getAllActiveBrands = async (): Promise<TireBrand[]> => {
  const response = await httpClient.get<TireBrand[]>(`${API_URL}/brands`);
  return response.data;
};

/**
 * Obtiene todas las marcas (activas e inactivas)
 */
export const getAllBrands = async (): Promise<TireBrand[]> => {
  const response = await httpClient.get<TireBrand[]>(`${API_URL}/brands/all`);
  return response.data;
};

// =====================================================
// TIPOS DE LLANTAS
// =====================================================

/**
 * Obtiene todos los tipos activos
 */
export const getAllActiveTypes = async (): Promise<TireType[]> => {
  const response = await httpClient.get<TireType[]>(`${API_URL}/types`);
  return response.data;
};

/**
 * Obtiene todos los tipos (activos e inactivos)
 */
export const getAllTypes = async (): Promise<TireType[]> => {
  const response = await httpClient.get<TireType[]>(`${API_URL}/types/all`);
  return response.data;
};

// =====================================================
// REFERENCIAS DE LLANTAS
// =====================================================

/**
 * Obtiene todas las referencias activas
 */
export const getAllActiveReferences = async (): Promise<TireReference[]> => {
  const response = await httpClient.get<TireReference[]>(`${API_URL}/references`);
  return response.data;
};

/**
 * Obtiene todas las referencias (activas e inactivas)
 */
export const getAllReferences = async (): Promise<TireReference[]> => {
  const response = await httpClient.get<TireReference[]>(
    `${API_URL}/references/all`
  );
  return response.data;
};

// =====================================================
// PROVEEDORES DE LLANTAS
// =====================================================

/**
 * Obtiene todos los proveedores activos
 */
export const getAllActiveSuppliers = async (): Promise<TireSupplier[]> => {
  const response = await httpClient.get<TireSupplier[]>(`${API_URL}/suppliers`);
  return response.data;
};

/**
 * Obtiene todos los proveedores (activos e inactivos)
 */
export const getAllSuppliers = async (): Promise<TireSupplier[]> => {
  const response = await httpClient.get<TireSupplier[]>(
    `${API_URL}/suppliers/all`
  );
  return response.data;
};

const tireCatalogService = {
  getAllActiveBrands,
  getAllBrands,
  getAllActiveTypes,
  getAllTypes,
  getAllActiveReferences,
  getAllReferences,
  getAllActiveSuppliers,
  getAllSuppliers,
};

export default tireCatalogService;
