/**
 * Modal de detalle de Oficina (Solo lectura)
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
import { useOfficeWithDetails } from '../hooks/useOffices';
import { useOfficeContext } from '../context/OfficeContext';
import { format } from 'date-fns';
import { es } from 'date-fns/locale';

interface OfficeDetailDialogProps {
  open: boolean;
  officeId: string | null;
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
 * Componente OfficeDetailDialog
 */
export default function OfficeDetailDialog({
  open,
  officeId,
  onClose,
  onEdit,
}: OfficeDetailDialogProps) {
  const { canManageOffices } = useOfficeContext();
  const canManage = canManageOffices();

  // =====================================================
  // DATA FETCHING
  // =====================================================

  const { data: office, isLoading, isError, error } = useOfficeWithDetails(
    officeId || '',
    { enabled: open && !!officeId }
  );

  // =====================================================
  // HANDLERS
  // =====================================================

  const handleEdit = () => {
    if (office) {
      onEdit?.(office.id);
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
      aria-labelledby="office-detail-title"
    >
      <DialogTitle id="office-detail-title">
        Detalle de Oficina
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

        {office && (
          <Grid container spacing={3}>
            {/* Header con estado */}
            <Grid item xs={12}>
              <Box
                display="flex"
                justifyContent="space-between"
                alignItems="center"
              >
                <Typography variant="h5" component="h2">
                  {office.code} - {office.name}
                </Typography>
                <Chip
                  icon={office.isActive ? <ActiveIcon /> : <InactiveIcon />}
                  label={office.isActive ? 'Activa' : 'Inactiva'}
                  color={office.isActive ? 'success' : 'default'}
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
              <DetailField label="Código" value={office.code} />
            </Grid>

            <Grid item xs={12} sm={4}>
              <DetailField label="Nombre" value={office.name} />
            </Grid>

            <Grid item xs={12} sm={4}>
              <DetailField label="Ciudad" value={office.city} />
            </Grid>

            <Grid item xs={12}>
              <Divider />
            </Grid>

            {/* Totales */}
            <Grid item xs={12}>
              <Typography variant="subtitle2" color="primary" gutterBottom>
                Recursos Asociados
              </Typography>
            </Grid>

            <Grid item xs={12} sm={4}>
              <DetailField
                label="Total Almacenes"
                value={office.totalWarehouses || 0}
              />
            </Grid>

            <Grid item xs={12} sm={4}>
              <DetailField
                label="Total Ubicaciones"
                value={office.totalLocations || 0}
              />
            </Grid>

            <Grid item xs={12} sm={4}>
              <DetailField
                label="Total Proveedores"
                value={office.totalSuppliers || 0}
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
                value={formatDate(office.createdAt)}
              />
            </Grid>

            <Grid item xs={12} sm={6}>
              <DetailField
                label="Última Actualización"
                value={formatDate(office.updatedAt)}
              />
            </Grid>

            <Grid item xs={12} sm={6}>
              <DetailField label="Versión" value={office.version} />
            </Grid>
          </Grid>
        )}
      </DialogContent>

      <DialogActions sx={{ px: 3, py: 2 }}>
        <Button onClick={onClose}>Cerrar</Button>
        {canManage && office && (
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
