/**
 * Modal de detalle de Almacén (Solo lectura)
 */

import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Box,
  Grid,
  Typography,
  Divider,
  Chip,
  CircularProgress,
  Alert,
  IconButton,
} from '@mui/material';
import {
  Close as CloseIcon,
  Edit as EditIcon,
  CheckCircle as ActiveIcon,
  Cancel as InactiveIcon,
} from '@mui/icons-material';
import { useWarehouseWithDetails } from '../hooks/useWarehouses';
import { useOfficeContext } from '../context/OfficeContext';
import { format } from 'date-fns';
import { es } from 'date-fns/locale';

interface WarehouseDetailDialogProps {
  open: boolean;
  warehouseId: string | null;
  onClose: () => void;
  onEdit?: (id: string) => void;
}

/**
 * Componente helper para mostrar un campo de detalle
 */
interface DetailFieldProps {
  label: string;
  value: string | number | null | undefined;
  suffix?: string;
}

function DetailField({ label, value, suffix }: DetailFieldProps) {
  return (
    <Box>
      <Typography variant="caption" color="text.secondary" display="block">
        {label}
      </Typography>
      <Typography variant="body1" fontWeight="medium">
        {value !== null && value !== undefined ? `${value}${suffix || ''}` : 'N/A'}
      </Typography>
    </Box>
  );
}

/**
 * Componente WarehouseDetailDialog
 */
export default function WarehouseDetailDialog({
  open,
  warehouseId,
  onClose,
  onEdit,
}: WarehouseDetailDialogProps) {
  const { canManageWarehouses } = useOfficeContext();
  const canManage = canManageWarehouses();

  // =====================================================
  // DATA FETCHING
  // =====================================================

  const { data: warehouse, isLoading, isError, error } = useWarehouseWithDetails(
    warehouseId || '',
    { enabled: open && !!warehouseId }
  );

  // =====================================================
  // HANDLERS
  // =====================================================

  const handleEdit = () => {
    if (warehouse) {
      onEdit?.(warehouse.id);
      onClose();
    }
  };

  const formatDate = (dateString: string | undefined) => {
    if (!dateString) return 'N/A';
    try {
      return format(new Date(dateString), "dd 'de' MMMM 'de' yyyy, HH:mm", {
        locale: es,
      });
    } catch {
      return 'Fecha inválida';
    }
  };

  // =====================================================
  // RENDER
  // =====================================================

  return (
    <Dialog
      open={open}
      onClose={onClose}
      maxWidth="md"
      fullWidth
      aria-labelledby="warehouse-detail-title"
    >
      <DialogTitle id="warehouse-detail-title">
        Detalle de Almacén
        <IconButton
          aria-label="cerrar"
          onClick={onClose}
          sx={{
            position: 'absolute',
            right: 8,
            top: 8,
          }}
        >
          <CloseIcon />
        </IconButton>
      </DialogTitle>

      <DialogContent dividers>
        {isLoading && (
          <Box display="flex" justifyContent="center" py={4}>
            <CircularProgress />
          </Box>
        )}

        {isError && (
          <Alert severity="error">
            Error al cargar el detalle: {error?.message}
          </Alert>
        )}

        {warehouse && (
          <Grid container spacing={3}>
            {/* Header con estado */}
            <Grid item xs={12}>
              <Box
                display="flex"
                justifyContent="space-between"
                alignItems="center"
              >
                <Typography variant="h5" component="h2">
                  {warehouse.code} - {warehouse.name}
                </Typography>
                <Chip
                  icon={warehouse.isActive ? <ActiveIcon /> : <InactiveIcon />}
                  label={warehouse.isActive ? 'Activo' : 'Inactivo'}
                  color={warehouse.isActive ? 'success' : 'default'}
                />
              </Box>
            </Grid>

            <Grid item xs={12}>
              <Divider />
            </Grid>

            {/* Información Básica */}
            <Grid item xs={12}>
              <Typography variant="subtitle2" color="primary" gutterBottom>
                Información Básica
              </Typography>
            </Grid>

            <Grid item xs={12} sm={4}>
              <DetailField label="Código" value={warehouse.code} />
            </Grid>

            <Grid item xs={12} sm={8}>
              <DetailField label="Nombre" value={warehouse.name} />
            </Grid>

            <Grid item xs={12}>
              <Divider />
            </Grid>

            {/* Oficina */}
            <Grid item xs={12}>
              <Typography variant="subtitle2" color="primary" gutterBottom>
                Oficina Asociada
              </Typography>
            </Grid>

            <Grid item xs={12} sm={4}>
              <DetailField label="Código Oficina" value={warehouse.officeCode} />
            </Grid>

            <Grid item xs={12} sm={8}>
              <DetailField label="Nombre Oficina" value={warehouse.officeName} />
            </Grid>

            <Grid item xs={12}>
              <Divider />
            </Grid>

            {/* Totales */}
            <Grid item xs={12}>
              <Typography variant="subtitle2" color="primary" gutterBottom>
                Ubicaciones
              </Typography>
            </Grid>

            <Grid item xs={12} sm={4}>
              <DetailField
                label="Total Ubicaciones"
                value={warehouse.totalLocations || 0}
              />
            </Grid>

            <Grid item xs={12}>
              <Divider />
            </Grid>

            {/* Auditoría */}
            <Grid item xs={12}>
              <Typography variant="subtitle2" color="primary" gutterBottom>
                Información de Auditoría
              </Typography>
            </Grid>

            <Grid item xs={12} sm={6}>
              <DetailField
                label="Fecha de Creación"
                value={formatDate(warehouse.createdAt)}
              />
            </Grid>

            <Grid item xs={12} sm={6}>
              <DetailField
                label="Última Actualización"
                value={formatDate(warehouse.updatedAt)}
              />
            </Grid>

            <Grid item xs={12} sm={6}>
              <DetailField label="Versión" value={warehouse.version} />
            </Grid>
          </Grid>
        )}
      </DialogContent>

      <DialogActions sx={{ px: 3, py: 2 }}>
        <Button onClick={onClose}>Cerrar</Button>
        {canManage && warehouse && (
          <Button
            variant="contained"
            startIcon={<EditIcon />}
            onClick={handleEdit}
          >
            Editar
          </Button>
        )}
      </DialogActions>
    </Dialog>
  );
}
