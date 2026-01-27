/**
 * Tipos TypeScript para el módulo de Gestión de Llantas
 */

// =====================================================
// CATÁLOGOS BASE
// =====================================================

export interface TireBrand {
  id: string;
  code: string;
  name: string;
  isActive: boolean;
}

export interface TireType {
  id: string;
  code: string;
  name: string;
  description?: string;
  isActive: boolean;
}

export interface TireReference {
  id: string;
  code: string;
  name: string;
  specifications?: string;
  isActive: boolean;
}

export interface TireSupplier {
  id: string;
  code: string;
  name: string;
  taxId: string;
  phone?: string;
  contactName?: string;
  isActive: boolean;
}

// =====================================================
// ESPECIFICACIÓN TÉCNICA (FICHA TÉCNICA)
// =====================================================

export interface TireSpecification {
  id: string;
  code: string;

  // Relaciones (catálogos)
  brand: TireBrand;
  type: TireType;
  reference: TireReference;
  dimension?: string;

  // Especificaciones de rendimiento
  expectedMileage: number;
  mileageRangeMin?: number;
  mileageRangeAvg?: number;
  mileageRangeMax?: number;
  expectedRetreads: number;
  expectedLossPercentage?: number;
  totalExpected?: number;
  costPerHour?: number;

  // Profundidades iniciales (en mm)
  initialDepthInternalMm: number;
  initialDepthCentralMm: number;
  initialDepthExternalMm: number;
  averageInitialDepth: number; // Campo calculado

  // Información comercial - Última compra
  lastPurchaseQuantity?: number;
  lastPurchaseUnitPrice?: number;
  lastPurchaseDate?: string; // ISO date string

  // Proveedores
  mainProvider?: TireSupplier;
  secondaryProvider?: TireSupplier;
  lastUsedProvider?: TireSupplier;

  // Características físicas
  weightKg?: number;

  // Información adicional
  expectedPerformance?: Record<string, any>;

  // Estado
  isActive: boolean;

  // Auditoría
  createdAt: string;
  createdBy?: number;
  updatedAt: string;
  updatedBy?: number;
  deletedAt?: string;
  deletedBy?: number;
}

export interface TireSpecificationSummary {
  id: string;
  code: string;

  // Información básica de catálogos (solo ID y nombre)
  brandId: string;
  brandName: string;

  typeId: string;
  typeName: string;

  referenceId: string;
  referenceName: string;

  dimension?: string;

  // Especificaciones clave
  expectedMileage: number;
  expectedRetreads: number;

  // Profundidades (solo promedio)
  averageInitialDepth: number;

  // Estado
  isActive: boolean;
}

// =====================================================
// REQUEST TYPES
// =====================================================

export interface CreateTireSpecificationRequest {
  // Relaciones (IDs de catálogos)
  brandId: string;
  typeId: string;
  referenceId: string;
  dimension?: string;

  // Especificaciones de rendimiento
  expectedMileage: number;
  mileageRangeMin?: number;
  mileageRangeAvg?: number;
  mileageRangeMax?: number;
  expectedRetreads: number;
  expectedLossPercentage?: number;
  totalExpected?: number;
  costPerHour?: number;

  // Profundidades iniciales (en mm)
  initialDepthInternalMm: number;
  initialDepthCentralMm: number;
  initialDepthExternalMm: number;

  // Información comercial - Última compra
  lastPurchaseQuantity?: number;
  lastPurchaseUnitPrice?: number;
  lastPurchaseDate?: string;

  // Proveedores (IDs)
  mainProviderId?: string;
  secondaryProviderId?: string;
  lastUsedProviderId?: string;

  // Características físicas
  weightKg?: number;

  // Información adicional (JSON)
  expectedPerformance?: Record<string, any>;
}

export interface UpdateTireSpecificationRequest extends CreateTireSpecificationRequest {
  // Hereda todos los campos de CreateTireSpecificationRequest
}

// =====================================================
// FILTROS Y PAGINACIÓN
// =====================================================

export interface TireSpecificationFilters {
  brandId?: string;
  typeId?: string;
  referenceId?: string;
  isActive?: boolean;
  searchText?: string;
}

export interface PageRequest {
  page: number;
  size: number;
  sort?: string;
  direction?: 'asc' | 'desc';
}

export interface PageResponse<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
    totalElements: number;
    totalPages: number;
  };
}

// =====================================================
// CATÁLOGOS STATE
// =====================================================

export interface TireCatalogs {
  brands: TireBrand[];
  types: TireType[];
  references: TireReference[];
  suppliers: TireSupplier[];
  isLoading: boolean;
  error: string | null;
}
