import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@/test/test-utils';
import userEvent from '@testing-library/user-event';
import TireSpecificationList from '../TireSpecificationList';
import * as tireHooks from '../../hooks/useTireSpecifications';

// Mock de los hooks
vi.mock('../../hooks/useTireSpecifications', () => ({
  useTireSpecifications: vi.fn(),
  useSearchTireSpecifications: vi.fn(),
  useDeleteTireSpecification: vi.fn(),
}));

// Mock de react-router-dom
const mockNavigate = vi.fn();
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

describe('TireSpecificationList', () => {
  const mockOnViewDetails = vi.fn();

  const defaultProps = {
    onViewDetails: mockOnViewDetails,
  };

  const mockTireSpecification = {
    id: '123e4567-e89b-12d3-a456-426614174000',
    code: 'FT-000001',
    brand: { id: '1', code: 'MCH', name: 'Michelin', isActive: true },
    type: { id: '2', code: 'RAD', name: 'Radial', description: 'Llanta tipo radial', isActive: true },
    reference: { id: '3', code: '295/80R22.5', name: 'Medida estándar', specifications: 'Especificaciones técnicas', isActive: true },
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
    mainProvider: { id: '4', code: 'PROV01', name: 'Proveedor Principal', taxId: '1234567890', isActive: true },
    secondaryProvider: null,
    lastUsedProvider: null,
    isActive: true,
    createdAt: '2026-01-27T00:00:00Z',
    updatedAt: '2026-01-27T00:00:00Z',
  };

  const mockPaginatedResponse = {
    content: [mockTireSpecification],
    pageable: {
      pageNumber: 0,
      pageSize: 10,
      totalElements: 1,
      totalPages: 1,
    },
  };

  const defaultUseTireSpecificationsReturn = {
    data: mockPaginatedResponse,
    isLoading: false,
    isError: false,
    error: null,
    refetch: vi.fn(),
  };

  const defaultUseSearchReturn = {
    data: mockPaginatedResponse,
    isLoading: false,
    isError: false,
    error: null,
  };

  const defaultUseDeleteReturn = {
    mutate: vi.fn(),
    isPending: false,
    isError: false,
    error: null,
  };

  beforeEach(() => {
    vi.clearAllMocks();
    mockNavigate.mockClear();
    mockOnViewDetails.mockClear();

    vi.mocked(tireHooks.useTireSpecifications).mockReturnValue(defaultUseTireSpecificationsReturn as any);
    vi.mocked(tireHooks.useSearchTireSpecifications).mockReturnValue(defaultUseSearchReturn as any);
    vi.mocked(tireHooks.useDeleteTireSpecification).mockReturnValue(defaultUseDeleteReturn as any);
  });

  describe('Renderizado inicial', () => {
    it('debe renderizar el campo de búsqueda', async () => {
      render(<TireSpecificationList {...defaultProps} />);

      await waitFor(() => {
        expect(screen.getByPlaceholderText(/buscar especificaciones/i)).toBeInTheDocument();
      });
    });

    it('debe renderizar el botón de nueva especificación', async () => {
      render(<TireSpecificationList {...defaultProps} />);

      await waitFor(() => {
        expect(screen.getByRole('button', { name: /nueva especificación/i })).toBeInTheDocument();
      });
    });

    it('debe renderizar los datos en la tabla', async () => {
      render(<TireSpecificationList {...defaultProps} />);

      await waitFor(() => {
        expect(screen.getByText('FT-000001')).toBeInTheDocument();
      });

      expect(screen.getByText('Michelin')).toBeInTheDocument();
      expect(screen.getByText('Radial')).toBeInTheDocument();
      expect(screen.getByText('295/80R22.5')).toBeInTheDocument();
    });
  });

  describe('Estado de carga', () => {
    it('debe mostrar el componente mientras carga', async () => {
      vi.mocked(tireHooks.useTireSpecifications).mockReturnValue({
        ...defaultUseTireSpecificationsReturn,
        isLoading: true,
        data: undefined,
      } as any);

      render(<TireSpecificationList {...defaultProps} />);

      await waitFor(() => {
        expect(screen.getByPlaceholderText(/buscar especificaciones/i)).toBeInTheDocument();
      });
    });
  });

  describe('Estado de error', () => {
    it('debe mostrar mensaje de error cuando falla la carga', async () => {
      const mockError = new Error('Failed to fetch');

      vi.mocked(tireHooks.useTireSpecifications).mockReturnValue({
        ...defaultUseTireSpecificationsReturn,
        isError: true,
        error: mockError,
        data: undefined,
      } as any);

      render(<TireSpecificationList {...defaultProps} />);

      await waitFor(() => {
        expect(screen.getByText(/error al cargar datos/i)).toBeInTheDocument();
      });
    });
  });

  describe('Navegación', () => {
    it('debe llamar onCreate al hacer clic en "Nueva Especificación"', async () => {
      const user = userEvent.setup();
      const mockOnCreate = vi.fn();

      render(<TireSpecificationList {...defaultProps} onCreate={mockOnCreate} />);

      await waitFor(() => {
        expect(screen.getByRole('button', { name: /nueva especificación/i })).toBeInTheDocument();
      });

      const newButton = screen.getByRole('button', { name: /nueva especificación/i });
      await user.click(newButton);

      await waitFor(() => {
        expect(mockOnCreate).toHaveBeenCalled();
      });
    });
  });

  describe('Sin datos', () => {
    it('debe mostrar mensaje cuando no hay fichas técnicas', async () => {
      vi.mocked(tireHooks.useTireSpecifications).mockReturnValue({
        ...defaultUseTireSpecificationsReturn,
        data: {
          ...mockPaginatedResponse,
          content: [],
          pageable: {
            ...mockPaginatedResponse.pageable,
            totalElements: 0,
          },
        },
      } as any);

      render(<TireSpecificationList {...defaultProps} />);

      await waitFor(() => {
        expect(screen.getByText(/no se encontraron fichas técnicas/i)).toBeInTheDocument();
      });
    });
  });

  describe('Paginación y datos', () => {
    it('debe mostrar múltiples registros', async () => {
      const multipleSpecsResponse = {
        ...mockPaginatedResponse,
        content: [
          mockTireSpecification,
          { ...mockTireSpecification, id: '2', code: 'FT-000002' },
          { ...mockTireSpecification, id: '3', code: 'FT-000003' },
        ],
        pageable: {
          ...mockPaginatedResponse.pageable,
          totalElements: 3,
        },
      };

      vi.mocked(tireHooks.useTireSpecifications).mockReturnValue({
        ...defaultUseTireSpecificationsReturn,
        data: multipleSpecsResponse,
      } as any);

      render(<TireSpecificationList {...defaultProps} />);

      await waitFor(() => {
        expect(screen.getByText('FT-000001')).toBeInTheDocument();
      });

      expect(screen.getByText('FT-000002')).toBeInTheDocument();
      expect(screen.getByText('FT-000003')).toBeInTheDocument();
    });

    it('debe mostrar el total correcto de registros en la paginación', async () => {
      const paginatedResponse = {
        ...mockPaginatedResponse,
        pageable: {
          ...mockPaginatedResponse.pageable,
          totalElements: 42,
        },
      };

      vi.mocked(tireHooks.useTireSpecifications).mockReturnValue({
        ...defaultUseTireSpecificationsReturn,
        data: paginatedResponse,
      } as any);

      render(<TireSpecificationList {...defaultProps} />);

      await waitFor(() => {
        expect(screen.getByText(/1–10 of 42/i)).toBeInTheDocument();
      });
    });
  });
});
