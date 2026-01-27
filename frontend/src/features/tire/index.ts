/**
 * Punto de entrada del módulo de Llantas (Tire)
 */

// Components
export * from './components';

// Pages
export * from './pages';

// Hooks
export * from './hooks/useTireCatalogs';
export * from './hooks/useTireSpecifications';

// Services
export { default as tireCatalogService } from './services/tireCatalogService';
export { default as tireSpecificationService } from './services/tireSpecificationService';

// Types
export type * from './types/tire.types';
