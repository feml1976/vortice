/**
 * Componente de listado de Oficinas
 * Utiliza Material-UI DataGrid con paginación y búsqueda
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
  GridSortModel,
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
  useOffices,
  useDeleteOffice,
  useToggleOfficeActive,
} from '../hooks/useOffices';
import { useOfficeContext } from '../context/OfficeContext';
import type { Office } from '../types/organization.types';
import toast from 'react-hot-toast';

interface OfficeListProps {
  onView?: (id: string) => void;
  onEdit?: (id: string) => void;
  onCreate?: () => void;
}

/**
 * Componente OfficeList
 */
export default function OfficeList({
  onView,
  onEdit,
  onCreate,
}: OfficeListProps) {
  // =====================================================
  // CONTEXT & STATE
  // =====================================================

  const { canManageOffices } = useOfficeContext();
  const canManage = canManageOffices();

  const [searchText, setSearchText] = useState('');
  const [showInactive, setShowInactive] = useState(false);

  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [selectedOfficeId, setSelectedOfficeId] = useState<string | null>(null);

  // =====================================================
  // DATA FETCHING
  // =====================================================

  const { data: offices, isLoading, isError, error, refetch } = useOffices({
    isActive: !showInactive,
  });

  // Mutations
  const deleteMutation = useDeleteOffice();
  const toggleMutation = useToggleOfficeActive();

  // Filtrar por búsqueda local
  const filteredOffices = offices?.filter((office) => {
    if (!searchText) return true;
    const search = searchText.toLowerCase();
    return (
      office.code.toLowerCase().includes(search) ||
      office.name.toLowerCase().includes(search) ||
      office.city.toLowerCase().includes(search)
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
    setSelectedOfficeId(id);
    setDeleteDialogOpen(true);
  };

  /**
   * Cierra el diálogo de eliminación
   */
  const handleCloseDeleteDialog = () => {
    setDeleteDialogOpen(false);
    setSelectedOfficeId(null);
  };

  /**
   * Confirma la eliminación
   */
  const handleConfirmDelete = async () => {
    if (!selectedOfficeId) return;

    try {
      await deleteMutation.mutateAsync(selectedOfficeId);
      handleCloseDeleteDialog();
    } catch (error) {
      // El error ya fue manejado por el hook
      console.error('Delete error:', error);
    }
  };

  /**
   * Activa/Desactiva una oficina
   */
  const handleToggleActive = async (id: string, currentState: boolean) => {
    try {
      await toggleMutation.mutateAsync(id);
      toast.success(
        `Oficina ${currentState ? 'desactivada' : 'activada'} exitosamente`
      );
    } catch (error) {
      // El error ya fue manejado por el hook
      console.error('Toggle error:', error);
    }
  };

  // =====================================================
  // COLUMN DEFINITIONS
  // =====================================================

  const columns: GridColDef<Office>[] = [
    {
      field: 'code',
      headerName: 'Código',
      width: 120,
      sortable: true,
    },
    {
      field: 'name',
      headerName: 'Nombre',
      width: 250,
      sortable: true,
    },
    {
      field: 'city',
      headerName: 'Ciudad',
      width: 150,
      sortable: true,
    },
    {
      field: 'totalWarehouses',
      headerName: 'Almacenes',
      width: 130,
      sortable: true,
      type: 'number',
      align: 'center',
      headerAlign: 'center',
      valueGetter: (_value, row) => row.totalWarehouses || 0,
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
        <Stack
          direction={{ xs: 'column', sm: 'row' }}
          spacing={2}
          alignItems={{ xs: 'stretch', sm: 'center' }}
          justifyContent="space-between"
          sx={{ mb: 2 }}
        >
          {/* Búsqueda */}
          <TextField
            placeholder="Buscar oficinas..."
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
                Nueva Oficina
              </Button>
            )}
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
            Solo administradores nacionales pueden gestionar oficinas.
          </Alert>
        )}
      </Paper>

      {/* DataGrid */}
      <Paper sx={{ height: 500, width: '100%' }}>
        <DataGrid
          rows={filteredOffices || []}
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
            noRowsLabel: 'No hay oficinas registradas',
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
            ¿Está seguro que desea eliminar esta oficina?
            <br />
            Esta acción no se puede deshacer y eliminará también todos los
            almacenes, ubicaciones y proveedores asociados.
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
