/**
 * Componente de listado de Especificaciones Técnicas de Llantas
 * Utiliza Material-UI DataGrid con paginación, búsqueda y filtros
 */

import { useState, useMemo } from 'react';
import {
  Box,
  Paper,
  TextField,
  Button,
  IconButton,
  Tooltip,
  Chip,
  InputAdornment,
  Stack,
  Alert,
  CircularProgress,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  DialogContentText,
} from '@mui/material';
import {
  DataGrid,
  GridColDef,
  GridPaginationModel,
  GridSortModel,
  GridActionsCellItem,
} from '@mui/x-data-grid';
import {
  Add as AddIcon,
  Search as SearchIcon,
  Visibility as ViewIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  FilterList as FilterIcon,
  Refresh as RefreshIcon,
} from '@mui/icons-material';
import {
  useTireSpecifications,
  useSearchTireSpecifications,
  useDeleteTireSpecification,
} from '../hooks/useTireSpecifications';
import type {
  TireSpecificationSummary,
  PageRequest,
} from '../types/tire.types';
import toast from 'react-hot-toast';

interface TireSpecificationListProps {
  onView?: (id: string) => void;
  onEdit?: (id: string) => void;
  onCreate?: () => void;
}

/**
 * Componente TireSpecificationList
 */
export default function TireSpecificationList({
  onView,
  onEdit,
  onCreate,
}: TireSpecificationListProps) {
  // =====================================================
  // STATE
  // =====================================================

  const [paginationModel, setPaginationModel] = useState<GridPaginationModel>({
    page: 0,
    pageSize: 10,
  });

  const [sortModel, setSortModel] = useState<GridSortModel>([
    { field: 'code', sort: 'asc' },
  ]);

  const [searchText, setSearchText] = useState('');
  const [searchQuery, setSearchQuery] = useState('');

  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [selectedSpecId, setSelectedSpecId] = useState<string | null>(null);

  // =====================================================
  // DATA FETCHING
  // =====================================================

  const pageRequest: PageRequest = useMemo(
    () => ({
      page: paginationModel.page,
      size: paginationModel.pageSize,
      sort: sortModel[0]?.field || 'code',
      direction: sortModel[0]?.sort || 'asc',
    }),
    [paginationModel, sortModel]
  );

  // Usar búsqueda si hay query, sino listar todas
  const isSearching = searchQuery.length > 0;

  const listQuery = useTireSpecifications(pageRequest);
  const searchQueryResult = useSearchTireSpecifications(
    searchQuery,
    pageRequest,
    isSearching
  );

  const activeQuery = isSearching ? searchQueryResult : listQuery;

  const { data, isLoading, isError, error, refetch } = activeQuery;

  // Mutations
  const deleteMutation = useDeleteTireSpecification();

  // =====================================================
  // HANDLERS
  // =====================================================

  /**
   * Maneja el cambio de paginación
   */
  const handlePaginationChange = (model: GridPaginationModel) => {
    setPaginationModel(model);
  };

  /**
   * Maneja el cambio de ordenamiento
   */
  const handleSortChange = (model: GridSortModel) => {
    setSortModel(model);
  };

  /**
   * Maneja la búsqueda
   */
  const handleSearch = () => {
    setSearchQuery(searchText.trim());
    setPaginationModel({ ...paginationModel, page: 0 }); // Reset a página 0
  };

  /**
   * Maneja el clear de búsqueda
   */
  const handleClearSearch = () => {
    setSearchText('');
    setSearchQuery('');
    setPaginationModel({ ...paginationModel, page: 0 });
  };

  /**
   * Maneja el refresh de datos
   */
  const handleRefresh = () => {
    refetch();
    toast.success('Datos actualizados');
  };

  /**
   * Abre el diálogo de confirmación de eliminación
   */
  const handleOpenDeleteDialog = (id: string) => {
    setSelectedSpecId(id);
    setDeleteDialogOpen(true);
  };

  /**
   * Cierra el diálogo de eliminación
   */
  const handleCloseDeleteDialog = () => {
    setDeleteDialogOpen(false);
    setSelectedSpecId(null);
  };

  /**
   * Confirma la eliminación
   */
  const handleConfirmDelete = async () => {
    if (!selectedSpecId) return;

    try {
      await deleteMutation.mutateAsync(selectedSpecId);
      handleCloseDeleteDialog();
    } catch (error) {
      // El error ya fue manejado por el hook
      console.error('Delete error:', error);
    }
  };

  // =====================================================
  // COLUMN DEFINITIONS
  // =====================================================

  const columns: GridColDef<TireSpecificationSummary>[] = [
    {
      field: 'code',
      headerName: 'Código',
      width: 130,
      sortable: true,
    },
    {
      field: 'brandName',
      headerName: 'Marca',
      width: 150,
      sortable: true,
    },
    {
      field: 'typeName',
      headerName: 'Tipo',
      width: 150,
      sortable: true,
    },
    {
      field: 'referenceName',
      headerName: 'Referencia',
      width: 180,
      sortable: true,
    },
    {
      field: 'dimension',
      headerName: 'Dimensión',
      width: 130,
      sortable: true,
      valueGetter: (_value, row) => row.dimension || 'N/A',
    },
    {
      field: 'expectedMileage',
      headerName: 'Kilometraje Esperado',
      width: 180,
      sortable: true,
      type: 'number',
      valueFormatter: (value) => `${(value as number)?.toLocaleString()} km`,
    },
    {
      field: 'expectedRetreads',
      headerName: 'Reencauches',
      width: 130,
      sortable: true,
      type: 'number',
      align: 'center',
      headerAlign: 'center',
    },
    {
      field: 'averageInitialDepth',
      headerName: 'Prof. Promedio',
      width: 140,
      sortable: true,
      type: 'number',
      valueFormatter: (value) => `${(value as number)?.toFixed(2)} mm`,
    },
    {
      field: 'isActive',
      headerName: 'Estado',
      width: 120,
      sortable: true,
      renderCell: (params) => (
        <Chip
          label={params.value ? 'Activo' : 'Inactivo'}
          color={params.value ? 'success' : 'default'}
          size="small"
        />
      ),
    },
    {
      field: 'actions',
      type: 'actions',
      headerName: 'Acciones',
      width: 120,
      getActions: (params) => [
        <GridActionsCellItem
          icon={
            <Tooltip title="Ver detalle">
              <ViewIcon />
            </Tooltip>
          }
          label="Ver"
          onClick={() => onView?.(params.id as string)}
          color="primary"
        />,
        <GridActionsCellItem
          icon={
            <Tooltip title="Editar">
              <EditIcon />
            </Tooltip>
          }
          label="Editar"
          onClick={() => onEdit?.(params.id as string)}
          color="primary"
        />,
        <GridActionsCellItem
          icon={
            <Tooltip title="Eliminar">
              <DeleteIcon color="error" />
            </Tooltip>
          }
          label="Eliminar"
          onClick={() => handleOpenDeleteDialog(params.id as string)}
        />,
      ],
    },
  ];

  // =====================================================
  // RENDER
  // =====================================================

  return (
    <Box sx={{ height: '100%', width: '100%' }}>
      <Paper sx={{ p: 2, mb: 2 }}>
        {/* Toolbar */}
        <Stack
          direction={{ xs: 'column', sm: 'row' }}
          spacing={2}
          alignItems={{ xs: 'stretch', sm: 'center' }}
          justifyContent="space-between"
          sx={{ mb: 2 }}
        >
          {/* Búsqueda */}
          <TextField
            placeholder="Buscar especificaciones..."
            size="small"
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
            onKeyPress={(e) => {
              if (e.key === 'Enter') {
                handleSearch();
              }
            }}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <SearchIcon />
                </InputAdornment>
              ),
            }}
            sx={{ minWidth: { xs: '100%', sm: 300 } }}
          />

          {/* Botones de acciones */}
          <Stack direction="row" spacing={1}>
            {searchQuery && (
              <Button
                variant="outlined"
                size="small"
                onClick={handleClearSearch}
              >
                Limpiar búsqueda
              </Button>
            )}

            <Tooltip title="Actualizar">
              <IconButton onClick={handleRefresh} size="small" color="primary">
                <RefreshIcon />
              </IconButton>
            </Tooltip>

            <Tooltip title="Filtros avanzados">
              <IconButton size="small" color="primary">
                <FilterIcon />
              </IconButton>
            </Tooltip>

            <Button
              variant="contained"
              startIcon={<AddIcon />}
              onClick={onCreate}
              size="small"
            >
              Nueva Especificación
            </Button>
          </Stack>
        </Stack>

        {/* Info de búsqueda */}
        {searchQuery && (
          <Alert severity="info" sx={{ mb: 2 }}>
            Mostrando resultados para: <strong>"{searchQuery}"</strong>
          </Alert>
        )}

        {/* Error */}
        {isError && (
          <Alert severity="error" sx={{ mb: 2 }}>
            Error al cargar datos: {error?.message}
          </Alert>
        )}
      </Paper>

      {/* DataGrid */}
      <Paper sx={{ height: 600, width: '100%' }}>
        <DataGrid
          rows={data?.content || []}
          columns={columns}
          paginationMode="server"
          sortingMode="server"
          paginationModel={paginationModel}
          onPaginationModelChange={handlePaginationChange}
          sortModel={sortModel}
          onSortModelChange={handleSortChange}
          rowCount={data?.pageable.totalElements || 0}
          pageSizeOptions={[5, 10, 25, 50, 100]}
          loading={isLoading}
          disableRowSelectionOnClick
          density="comfortable"
          sx={{
            '& .MuiDataGrid-cell:focus': {
              outline: 'none',
            },
            '& .MuiDataGrid-row:hover': {
              backgroundColor: 'action.hover',
            },
          }}
          slotProps={{
            loadingOverlay: {
              variant: 'skeleton',
              noRowsVariant: 'skeleton',
            },
          }}
          localeText={{
            noRowsLabel: 'No hay especificaciones técnicas',
          }}
        />
      </Paper>

      {/* Delete Confirmation Dialog */}
      <Dialog
        open={deleteDialogOpen}
        onClose={handleCloseDeleteDialog}
        aria-labelledby="delete-dialog-title"
        aria-describedby="delete-dialog-description"
      >
        <DialogTitle id="delete-dialog-title">
          Confirmar Eliminación
        </DialogTitle>
        <DialogContent>
          <DialogContentText id="delete-dialog-description">
            ¿Está seguro que desea eliminar esta especificación técnica?
            <br />
            Esta acción no se puede deshacer.
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button
            onClick={handleCloseDeleteDialog}
            disabled={deleteMutation.isPending}
          >
            Cancelar
          </Button>
          <Button
            onClick={handleConfirmDelete}
            color="error"
            variant="contained"
            disabled={deleteMutation.isPending}
            startIcon={
              deleteMutation.isPending ? (
                <CircularProgress size={16} />
              ) : (
                <DeleteIcon />
              )
            }
          >
            {deleteMutation.isPending ? 'Eliminando...' : 'Eliminar'}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
