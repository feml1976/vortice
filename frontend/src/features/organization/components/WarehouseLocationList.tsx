/**
 * Componente de listado de Ubicaciones de Almacén
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
  GridActionsCellItem,
} from '@mui/x-data-grid';
import {
  Add as AddIcon,
  Search as SearchIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  Refresh as RefreshIcon,
  ToggleOn as ToggleOnIcon,
  ToggleOff as ToggleOffIcon,
} from '@mui/icons-material';
import {
  useWarehouseLocations,
  useDeleteWarehouseLocation,
  useToggleWarehouseLocationActive,
} from '../hooks/useWarehouseLocations';
import { useOfficeContext } from '../context/OfficeContext';
import { OfficeSelector } from './OfficeSelector';
import { WarehouseSelector } from './WarehouseSelector';
import type { WarehouseLocation } from '../types/organization.types';
import toast from 'react-hot-toast';

interface WarehouseLocationListProps {
  onEdit?: (id: string) => void;
  onCreate?: () => void;
}

/**
 * Componente WarehouseLocationList
 */
export default function WarehouseLocationList({
  onEdit,
  onCreate,
}: WarehouseLocationListProps) {
  // =====================================================
  // CONTEXT & STATE
  // =====================================================

  const { canManageLocations, isNationalAdmin, currentOffice } = useOfficeContext();
  const canManage = canManageLocations();

  const [searchText, setSearchText] = useState('');
  const [showInactive, setShowInactive] = useState(false);
  const [selectedOfficeId, setSelectedOfficeId] = useState<string>(
    currentOffice?.id || ''
  );
  const [selectedWarehouseId, setSelectedWarehouseId] = useState<string>('');

  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [selectedLocationId, setSelectedLocationId] = useState<string | null>(null);

  // =====================================================
  // DATA FETCHING
  // =====================================================

  const { data: locations, isLoading, isError, error, refetch } = useWarehouseLocations({
    isActive: !showInactive,
  });

  // Mutations
  const deleteMutation = useDeleteWarehouseLocation();
  const toggleMutation = useToggleWarehouseLocationActive();

  // Filtrar por búsqueda y almacén local
  const filteredLocations = locations?.filter((location) => {
    // Filtro de almacén
    if (selectedWarehouseId) {
      if (location.warehouseId !== selectedWarehouseId) return false;
    }

    // Filtro de búsqueda
    if (!searchText) return true;
    const search = searchText.toLowerCase();
    return (
      location.code.toLowerCase().includes(search) ||
      location.description?.toLowerCase().includes(search) ||
      location.warehouseCode?.toLowerCase().includes(search) ||
      location.warehouseName?.toLowerCase().includes(search)
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
   * Maneja el cambio de oficina
   */
  const handleOfficeChange = (officeId: string) => {
    setSelectedOfficeId(officeId);
    setSelectedWarehouseId(''); // Reset warehouse cuando cambia la oficina
  };

  /**
   * Abre el diálogo de confirmación de eliminación
   */
  const handleOpenDeleteDialog = (id: string) => {
    setSelectedLocationId(id);
    setDeleteDialogOpen(true);
  };

  /**
   * Cierra el diálogo de eliminación
   */
  const handleCloseDeleteDialog = () => {
    setDeleteDialogOpen(false);
    setSelectedLocationId(null);
  };

  /**
   * Confirma la eliminación
   */
  const handleConfirmDelete = async () => {
    if (!selectedLocationId) return;

    try {
      await deleteMutation.mutateAsync(selectedLocationId);
      handleCloseDeleteDialog();
    } catch (error) {
      console.error('Delete error:', error);
    }
  };

  /**
   * Activa/Desactiva una ubicación
   */
  const handleToggleActive = async (id: string, currentState: boolean) => {
    try {
      await toggleMutation.mutateAsync(id);
      toast.success(
        `Ubicación ${currentState ? 'desactivada' : 'activada'} exitosamente`
      );
    } catch (error) {
      console.error('Toggle error:', error);
    }
  };

  // =====================================================
  // COLUMN DEFINITIONS
  // =====================================================

  const columns: GridColDef<WarehouseLocation>[] = [
    {
      field: 'code',
      headerName: 'Código',
      width: 120,
      sortable: true,
    },
    {
      field: 'warehouseCode',
      headerName: 'Almacén',
      width: 120,
      sortable: true,
    },
    {
      field: 'warehouseName',
      headerName: 'Nombre Almacén',
      width: 200,
      sortable: true,
    },
    {
      field: 'description',
      headerName: 'Descripción',
      width: 250,
      sortable: true,
      valueGetter: (_value, row) => row.description || 'Sin descripción',
    },
    {
      field: 'isActive',
      headerName: 'Estado',
      width: 120,
      sortable: true,
      renderCell: (params) => (
        <Chip
          label={params.value ? 'Activa' : 'Inactiva'}
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
        const actions = [];

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
          {/* Fila 1: Selectores de oficina y almacén */}
          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
            {isNationalAdmin && (
              <OfficeSelector
                value={selectedOfficeId}
                onChange={handleOfficeChange}
                label="Filtrar por Oficina"
                fullWidth
              />
            )}
            <WarehouseSelector
              value={selectedWarehouseId}
              onChange={setSelectedWarehouseId}
              officeId={selectedOfficeId}
              label="Filtrar por Almacén"
              fullWidth
            />
          </Stack>

          {/* Fila 2: Búsqueda y acciones */}
          <Stack
            direction={{ xs: 'column', sm: 'row' }}
            spacing={2}
            alignItems={{ xs: 'stretch', sm: 'center' }}
            justifyContent="space-between"
          >
            {/* Búsqueda */}
            <TextField
              placeholder="Buscar ubicaciones..."
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
                {showInactive ? 'Ver solo activas' : 'Ver inactivas'}
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
                  Nueva Ubicación
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
            Solo administradores y gerentes de almacén pueden gestionar ubicaciones.
          </Alert>
        )}
      </Paper>

      {/* DataGrid */}
      <Paper sx={{ height: 500, width: '100%' }}>
        <DataGrid
          rows={filteredLocations || []}
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
            noRowsLabel: 'No hay ubicaciones registradas',
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
            ¿Está seguro que desea eliminar esta ubicación?
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
