/**
 * Tipos TypeScript para el módulo de Estructura Organizacional Multi-Sede
 */

// =====================================================
// ENTIDADES PRINCIPALES
// =====================================================

/**
 * Oficina/Sede - Top level de la jerarquía organizacional
 */
export interface Office {
  id: string;
  code: string;
  name: string;
  city: string;
  address?: string;
  phone?: string;
  isActive: boolean;

  // Información adicional (opcional, para vista con detalles)
  totalWarehouses?: number;
  totalSuppliers?: number;
  totalUsers?: number;

  // Auditoría
  createdAt: string;
  createdBy: number;
  updatedAt?: string;
  updatedBy?: number;
  version: number;
}

/**
 * Almacén - Pertenece a una oficina
 */
export interface Warehouse {
  id: string;
  code: string;
  name: string;
  description?: string;
  officeId: string;
  isActive: boolean;

  // Información de oficina (opcional)
  officeCode?: string;
  officeName?: string;

  // Información adicional (opcional)
  totalLocations?: number;

  // Auditoría
  createdAt: string;
  createdBy: number;
  updatedAt?: string;
  updatedBy?: number;
  version: number;
}

/**
 * Ubicación de Almacén - Pertenece a un almacén
 */
export interface WarehouseLocation {
  id: string;
  code: string;
  name: string;
  description?: string;
  warehouseId: string;
  isActive: boolean;

  // Información de almacén (opcional)
  warehouseCode?: string;
  warehouseName?: string;

  // Información de oficina (opcional)
  officeId?: string;
  officeCode?: string;
  officeName?: string;

  // Auditoría
  createdAt: string;
  createdBy: number;
  updatedAt?: string;
  updatedBy?: number;
  version: number;
}

/**
 * Proveedor de Llantas - Específico por oficina
 */
export interface TireSupplier {
  id: string;
  code: string;
  name: string;
  taxId: string;
  officeId: string;
  contactName?: string;
  email?: string;
  phone?: string;
  address?: string;
  isActive: boolean;

  // Información de oficina (opcional)
  officeCode?: string;
  officeName?: string;

  // Auditoría
  createdAt: string;
  createdBy: number;
  updatedAt?: string;
  updatedBy?: number;
  version: number;
}

// =====================================================
// REQUEST TYPES
// =====================================================

export interface CreateOfficeRequest {
  code: string;
  name: string;
  city: string;
  address?: string;
  phone?: string;
}

export interface UpdateOfficeRequest {
  name: string;
  city: string;
  address?: string;
  phone?: string;
}

export interface CreateWarehouseRequest {
  code: string;
  name: string;
  officeId: string;
  description?: string;
}

export interface UpdateWarehouseRequest {
  name: string;
  description?: string;
}

export interface CreateWarehouseLocationRequest {
  code: string;
  name: string;
  warehouseId: string;
  description?: string;
}

export interface UpdateWarehouseLocationRequest {
  name: string;
  description?: string;
}

export interface CreateTireSupplierRequest {
  code: string;
  name: string;
  taxId: string;
  officeId: string;
  contactName?: string;
  email?: string;
  phone?: string;
  address?: string;
}

export interface UpdateTireSupplierRequest {
  name: string;
  taxId: string;
  contactName?: string;
  email?: string;
  phone?: string;
  address?: string;
}

// =====================================================
// FILTROS Y PAGINACIÓN
// =====================================================

export interface OfficeFilters {
  isActive?: boolean;
  searchText?: string; // Busca en nombre o ciudad
}

export interface WarehouseFilters {
  officeId?: string;
  isActive?: boolean;
  searchText?: string; // Busca en código o nombre
}

export interface WarehouseLocationFilters {
  warehouseId?: string;
  isActive?: boolean;
  searchText?: string; // Busca en código o nombre
}

export interface TireSupplierFilters {
  officeId?: string;
  isActive?: boolean;
  searchText?: string; // Busca en nombre, código o NIT
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
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

// =====================================================
// CONTEXT TYPES
// =====================================================

/**
 * Contexto de la oficina actual del usuario
 * Este contexto es crítico para el funcionamiento del sistema multi-sede
 */
export interface OfficeContext {
  currentOffice: Office | null;
  isNationalAdmin: boolean;
  isOfficeAdmin: boolean;
  isWarehouseManager: boolean;
  hasAccessToOffice: (officeId: string) => boolean;
  canManageOffices: () => boolean;
  canManageWarehouses: () => boolean;
  canManageLocations: () => boolean;
  canManageSuppliers: () => boolean;
}

// =====================================================
// VISTA COMPACTA (PARA SELECTORES)
// =====================================================

export interface OfficeOption {
  id: string;
  code: string;
  name: string;
}

export interface WarehouseOption {
  id: string;
  code: string;
  name: string;
  officeId: string;
}

export interface WarehouseLocationOption {
  id: string;
  code: string;
  name: string;
  warehouseId: string;
}

export interface TireSupplierOption {
  id: string;
  code: string;
  name: string;
  officeId: string;
}
