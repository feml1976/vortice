/**
 * Componente de listado de Proveedores de Llantas
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
  useTireSuppliers,
  useDeleteTireSupplier,
  useToggleTireSupplierActive,
} from '../hooks/useTireSuppliers';
import { useOfficeContext } from '../context/OfficeContext';
import { OfficeSelector } from './OfficeSelector';
import type { TireSupplier } from '../types/organization.types';
import toast from 'react-hot-toast';

interface TireSupplierListProps {
  onEdit?: (id: string) => void;
  onCreate?: () => void;
}

/**
 * Componente TireSupplierList
 */
export default function TireSupplierList({
  onEdit,
  onCreate,
}: TireSupplierListProps) {
  // =====================================================
  // CONTEXT & STATE
  // =====================================================

  const { canManageSuppliers, isNationalAdmin, currentOffice } = useOfficeContext();
  const canManage = canManageSuppliers();

  const [searchText, setSearchText] = useState('');
  const [showInactive, setShowInactive] = useState(false);
  const [selectedOfficeId, setSelectedOfficeId] = useState<string>(
    currentOffice?.id || ''
  );

  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [selectedSupplierId, setSelectedSupplierId] = useState<string | null>(null);

  // =====================================================
  // DATA FETCHING
  // =====================================================

  const { data: suppliers, isLoading, isError, error, refetch } = useTireSuppliers({
    isActive: !showInactive,
  });

  // Mutations
  const deleteMutation = useDeleteTireSupplier();
  const toggleMutation = useToggleTireSupplierActive();

  // Filtrar por búsqueda y oficina local
  const filteredSuppliers = suppliers?.filter((supplier) => {
    // Filtro de oficina (solo si es admin nacional)
    if (isNationalAdmin && selectedOfficeId) {
      if (supplier.officeId !== selectedOfficeId) return false;
    }

    // Filtro de búsqueda
    if (!searchText) return true;
    const search = searchText.toLowerCase();
    return (
      supplier.code.toLowerCase().includes(search) ||
      supplier.name.toLowerCase().includes(search) ||
      supplier.nit?.toLowerCase().includes(search) ||
      supplier.contactName?.toLowerCase().includes(search) ||
      supplier.city?.toLowerCase().includes(search)
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
    setSelectedSupplierId(id);
    setDeleteDialogOpen(true);
  };

  /**
   * Cierra el diálogo de eliminación
   */
  const handleCloseDeleteDialog = () => {
    setDeleteDialogOpen(false);
    setSelectedSupplierId(null);
  };

  /**
   * Confirma la eliminación
   */
  const handleConfirmDelete = async () => {
    if (!selectedSupplierId) return;

    try {
      await deleteMutation.mutateAsync(selectedSupplierId);
      handleCloseDeleteDialog();
    } catch (error) {
      console.error('Delete error:', error);
    }
  };

  /**
   * Activa/Desactiva un proveedor
   */
  const handleToggleActive = async (id: string, currentState: boolean) => {
    try {
      await toggleMutation.mutateAsync(id);
      toast.success(
        `Proveedor ${currentState ? 'desactivado' : 'activado'} exitosamente`
      );
    } catch (error) {
      console.error('Toggle error:', error);
    }
  };

  // =====================================================
  // COLUMN DEFINITIONS
  // =====================================================

  const columns: GridColDef<TireSupplier>[] = [
    {
      field: 'code',
      headerName: 'Código',
      width: 100,
      sortable: true,
    },
    {
      field: 'name',
      headerName: 'Nombre',
      width: 200,
      sortable: true,
    },
    {
      field: 'nit',
      headerName: 'NIT',
      width: 130,
      sortable: true,
      valueGetter: (_value, row) => row.nit || 'N/A',
    },
    {
      field: 'contactName',
      headerName: 'Contacto',
      width: 150,
      sortable: true,
      valueGetter: (_value, row) => row.contactName || 'N/A',
    },
    {
      field: 'phone',
      headerName: 'Teléfono',
      width: 130,
      sortable: true,
      valueGetter: (_value, row) => row.phone || 'N/A',
    },
    {
      field: 'city',
      headerName: 'Ciudad',
      width: 120,
      sortable: true,
      valueGetter: (_value, row) => row.city || 'N/A',
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
              placeholder="Buscar proveedores..."
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
                  Nuevo Proveedor
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
            Solo administradores pueden gestionar proveedores.
          </Alert>
        )}
      </Paper>

      {/* DataGrid */}
      <Paper sx={{ height: 500, width: '100%' }}>
        <DataGrid
          rows={filteredSuppliers || []}
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
            noRowsLabel: 'No hay proveedores registrados',
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
            ¿Está seguro que desea eliminar este proveedor?
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
