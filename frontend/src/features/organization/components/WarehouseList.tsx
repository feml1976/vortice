/**
 * Componente de listado de Almacenes
 * Utiliza Material-UI DataGrid con paginación y filtros
 */

import { useState } from 'react';
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
  GridActionsCellItem,
} from '@mui/x-data-grid';
import {
  Add as AddIcon,
  Search as SearchIcon,
  Visibility as ViewIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  Refresh as RefreshIcon,
  ToggleOn as ToggleOnIcon,
  ToggleOff as ToggleOffIcon,
} from '@mui/icons-material';
import {
  useWarehouses,
  useDeleteWarehouse,
  useToggleWarehouseActive,
} from '../hooks/useWarehouses';
import { useOfficeContext } from '../context/OfficeContext';
import { OfficeSelector } from './OfficeSelector';
import type { Warehouse } from '../types/organization.types';
import toast from 'react-hot-toast';

interface WarehouseListProps {
  onView?: (id: string) => void;
  onEdit?: (id: string) => void;
  onCreate?: () => void;
}

/**
 * Componente WarehouseList
 */
export default function WarehouseList({
  onView,
  onEdit,
  onCreate,
}: WarehouseListProps) {
  // =====================================================
  // CONTEXT & STATE
  // =====================================================

  const { canManageWarehouses, isNationalAdmin, currentOffice } = useOfficeContext();
  const canManage = canManageWarehouses();

  const [searchText, setSearchText] = useState('');
  const [showInactive, setShowInactive] = useState(false);
  const [selectedOfficeId, setSelectedOfficeId] = useState<string>(
    currentOffice?.id || ''
  );

  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [selectedWarehouseId, setSelectedWarehouseId] = useState<string | null>(null);

  // =====================================================
  // DATA FETCHING
  // =====================================================

  const { data: warehouses, isLoading, isError, error, refetch } = useWarehouses({
    isActive: !showInactive,
  });

  // Mutations
  const deleteMutation = useDeleteWarehouse();
  const toggleMutation = useToggleWarehouseActive();

  // Filtrar por búsqueda y oficina local
  const filteredWarehouses = warehouses?.filter((warehouse) => {
    // Filtro de oficina (solo si es admin nacional)
    if (isNationalAdmin && selectedOfficeId) {
      if (warehouse.officeId !== selectedOfficeId) return false;
    }

    // Filtro de búsqueda
    if (!searchText) return true;
    const search = searchText.toLowerCase();
    return (
      warehouse.code.toLowerCase().includes(search) ||
      warehouse.name.toLowerCase().includes(search) ||
      warehouse.officeCode?.toLowerCase().includes(search) ||
      warehouse.officeName?.toLowerCase().includes(search)
    );
  });

  // =====================================================
  // HANDLERS
  // =====================================================

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
    setSelectedWarehouseId(id);
    setDeleteDialogOpen(true);
  };

  /**
   * Cierra el diálogo de eliminación
   */
  const handleCloseDeleteDialog = () => {
    setDeleteDialogOpen(false);
    setSelectedWarehouseId(null);
  };

  /**
   * Confirma la eliminación
   */
  const handleConfirmDelete = async () => {
    if (!selectedWarehouseId) return;

    try {
      await deleteMutation.mutateAsync(selectedWarehouseId);
      handleCloseDeleteDialog();
    } catch (error) {
      // El error ya fue manejado por el hook
      console.error('Delete error:', error);
    }
  };

  /**
   * Activa/Desactiva un almacén
   */
  const handleToggleActive = async (id: string, currentState: boolean) => {
    try {
      await toggleMutation.mutateAsync(id);
      toast.success(
        `Almacén ${currentState ? 'desactivado' : 'activado'} exitosamente`
      );
    } catch (error) {
      // El error ya fue manejado por el hook
      console.error('Toggle error:', error);
    }
  };

  // =====================================================
  // COLUMN DEFINITIONS
  // =====================================================

  const columns: GridColDef<Warehouse>[] = [
    {
      field: 'code',
      headerName: 'Código',
      width: 120,
      sortable: true,
    },
    {
      field: 'name',
      headerName: 'Nombre',
      width: 200,
      sortable: true,
    },
    ...(isNationalAdmin
      ? [
          {
            field: 'officeCode',
            headerName: 'Oficina',
            width: 100,
            sortable: true,
          } as GridColDef<Warehouse>,
          {
            field: 'officeName',
            headerName: 'Nombre Oficina',
            width: 180,
            sortable: true,
          } as GridColDef<Warehouse>,
        ]
      : []),
    {
      field: 'totalLocations',
      headerName: 'Ubicaciones',
      width: 130,
      sortable: true,
      type: 'number',
      align: 'center',
      headerAlign: 'center',
      valueGetter: (_value, row) => row.totalLocations || 0,
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
      width: 150,
      getActions: (params) => {
        const actions = [
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
        ];

        if (canManage) {
          actions.push(
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
                <Tooltip title={params.row.isActive ? 'Desactivar' : 'Activar'}>
                  {params.row.isActive ? <ToggleOffIcon /> : <ToggleOnIcon />}
                </Tooltip>
              }
              label="Toggle"
              onClick={() =>
                handleToggleActive(params.id as string, params.row.isActive)
              }
              color={params.row.isActive ? 'warning' : 'success'}
            />,
            <GridActionsCellItem
              icon={
                <Tooltip title="Eliminar">
                  <DeleteIcon color="error" />
                </Tooltip>
              }
              label="Eliminar"
              onClick={() => handleOpenDeleteDialog(params.id as string)}
            />
          );
        }

        return actions;
      },
    },
  ];

  // =====================================================
  // RENDER
  // =====================================================

  return (
    <Box sx={{ height: '100%', width: '100%' }}>
      <Paper sx={{ p: 2, mb: 2 }}>
        {/* Toolbar */}
        <Stack spacing={2} sx={{ mb: 2 }}>
          {/* Fila 1: Selector de oficina (solo para admin nacional) */}
          {isNationalAdmin && (
            <OfficeSelector
              value={selectedOfficeId}
              onChange={setSelectedOfficeId}
              label="Filtrar por Oficina"
              fullWidth
            />
          )}

          {/* Fila 2: Búsqueda y acciones */}
          <Stack
            direction={{ xs: 'column', sm: 'row' }}
            spacing={2}
            alignItems={{ xs: 'stretch', sm: 'center' }}
            justifyContent="space-between"
          >
            {/* Búsqueda */}
            <TextField
              placeholder="Buscar almacenes..."
              size="small"
              value={searchText}
              onChange={(e) => setSearchText(e.target.value)}
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
              <Button
                variant={showInactive ? 'contained' : 'outlined'}
                size="small"
                onClick={() => setShowInactive(!showInactive)}
              >
                {showInactive ? 'Ver solo activos' : 'Ver inactivos'}
              </Button>

              <Tooltip title="Actualizar">
                <IconButton onClick={handleRefresh} size="small" color="primary">
                  <RefreshIcon />
                </IconButton>
              </Tooltip>

              {canManage && (
                <Button
                  variant="contained"
                  startIcon={<AddIcon />}
                  onClick={onCreate}
                  size="small"
                >
                  Nuevo Almacén
                </Button>
              )}
            </Stack>
          </Stack>
        </Stack>

        {/* Error */}
        {isError && (
          <Alert severity="error" sx={{ mb: 2 }}>
            Error al cargar datos: {error?.message}
          </Alert>
        )}

        {/* Info */}
        {!canManage && (
          <Alert severity="info" sx={{ mb: 2 }}>
            Solo administradores pueden gestionar almacenes.
          </Alert>
        )}
      </Paper>

      {/* DataGrid */}
      <Paper sx={{ height: 500, width: '100%' }}>
        <DataGrid
          rows={filteredWarehouses || []}
          columns={columns}
          paginationModel={{ page: 0, pageSize: 10 }}
          pageSizeOptions={[5, 10, 25, 50]}
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
            noRowsLabel: 'No hay almacenes registrados',
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
            ¿Está seguro que desea eliminar este almacén?
            <br />
            Esta acción no se puede deshacer y eliminará también todas las
            ubicaciones asociadas.
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
