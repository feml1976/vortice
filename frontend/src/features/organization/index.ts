/**
 * Barrel export para el módulo de Estructura Organizacional Multi-Sede
 */

// Types
export * from './types/organization.types';

// Services
export { default as officeService } from './services/officeService';
export { default as warehouseService } from './services/warehouseService';
export { default as warehouseLocationService } from './services/warehouseLocationService';
export { default as tireSupplierService } from './services/tireSupplierService';

// Hooks
export * from './hooks/useOffices';
export * from './hooks/useWarehouses';
export * from './hooks/useWarehouseLocations';
export * from './hooks/useTireSuppliers';

// Context
export { OfficeProvider, useOfficeContext } from './context/OfficeContext';

// Components - Selectors
export { OfficeSelector } from './components/OfficeSelector';
export { WarehouseSelector } from './components/WarehouseSelector';

// Components - Lists
export { default as OfficeList } from './components/OfficeList';
export { default as WarehouseList } from './components/WarehouseList';
export { default as WarehouseLocationList } from './components/WarehouseLocationList';
export { default as TireSupplierList } from './components/TireSupplierList';

// Components - Forms
export { default as OfficeForm } from './components/OfficeForm';
export { default as WarehouseForm } from './components/WarehouseForm';
export { default as WarehouseLocationForm } from './components/WarehouseLocationForm';
export { default as TireSupplierForm } from './components/TireSupplierForm';

// Components - Detail Dialogs
export { default as OfficeDetailDialog } from './components/OfficeDetailDialog';
export { default as WarehouseDetailDialog } from './components/WarehouseDetailDialog';

// Pages
export { default as OfficePage } from './pages/OfficePage';
export { default as WarehousePage } from './pages/WarehousePage';
export { default as WarehouseLocationPage } from './pages/WarehouseLocationPage';
export { default as TireSupplierPage } from './pages/TireSupplierPage';
