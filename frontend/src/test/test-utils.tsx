import { ReactElement } from 'react';
import { render, RenderOptions } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { BrowserRouter } from 'react-router-dom';

// Crear un QueryClient para tests con configuración optimizada
const createTestQueryClient = () =>
  new QueryClient({
    defaultOptions: {
      queries: {
        retry: false,
        gcTime: 0,
      },
      mutations: {
        retry: false,
      },
    },
  });

interface AllProvidersProps {
  children: React.ReactNode;
}

// Wrapper con todos los providers necesarios
const AllProviders = ({ children }: AllProvidersProps) => {
  const queryClient = createTestQueryClient();

  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>{children}</BrowserRouter>
    </QueryClientProvider>
  );
};

// Función de renderizado personalizada que incluye todos los providers
const customRender = (
  ui: ReactElement,
  options?: Omit<RenderOptions, 'wrapper'>
) => render(ui, { wrapper: AllProviders, ...options });

// Mock data para tests
export const mockTireBrand = {
  id: '123e4567-e89b-12d3-a456-426614174000',
  code: 'MCH',
  name: 'Michelin',
  isActive: true,
};

export const mockTireType = {
  id: '123e4567-e89b-12d3-a456-426614174001',
  code: 'RAD',
  name: 'Radial',
  description: 'Llanta tipo radial',
  isActive: true,
};

export const mockTireReference = {
  id: '123e4567-e89b-12d3-a456-426614174002',
  code: '295/80R22.5',
  name: 'Medida estándar',
  specifications: 'Especificaciones técnicas',
  isActive: true,
};

export const mockTireSupplier = {
  id: '123e4567-e89b-12d3-a456-426614174003',
  code: 'PROV01',
  name: 'Proveedor Principal',
  taxId: '1234567890',
  isActive: true,
};

export const mockTireSpecification = {
  id: '123e4567-e89b-12d3-a456-426614174004',
  code: 'FT-000001',
  brand: mockTireBrand,
  type: mockTireType,
  reference: mockTireReference,
  dimension: '295/80R22.5',
  expectedMileage: 150000,
  mileageRangeMin: 100000,
  mileageRangeAvg: 150000,
  mileageRangeMax: 200000,
  expectedRetreads: 2,
  initialDepthInternalMm: 18.0,
  initialDepthCentralMm: 18.0,
  initialDepthExternalMm: 18.0,
  averageInitialDepth: 18.0,
  mainProvider: mockTireSupplier,
  secondaryProvider: null,
  lastUsedProvider: null,
  isActive: true,
  createdAt: '2026-01-27T00:00:00Z',
  updatedAt: '2026-01-27T00:00:00Z',
};

export const mockTireSpecificationList = [
  mockTireSpecification,
  {
    ...mockTireSpecification,
    id: '123e4567-e89b-12d3-a456-426614174005',
    code: 'FT-000002',
    expectedMileage: 180000,
  },
  {
    ...mockTireSpecification,
    id: '123e4567-e89b-12d3-a456-426614174006',
    code: 'FT-000003',
    expectedMileage: 200000,
  },
];

// Re-exportar todo de testing library
export * from '@testing-library/react';
export { default as userEvent } from '@testing-library/user-event';

// Exportar el render personalizado como predeterminado
export { customRender as render };
