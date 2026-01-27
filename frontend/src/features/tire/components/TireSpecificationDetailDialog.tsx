/**
 * Modal de detalle de Especificación Técnica (Solo lectura)
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
  Paper,
  Stack,
  IconButton,
} from '@mui/material';
import {
  Close as CloseIcon,
  Edit as EditIcon,
  CheckCircle as ActiveIcon,
  Cancel as InactiveIcon,
} from '@mui/icons-material';
import { useTireSpecification } from '../hooks/useTireSpecifications';
import { format } from 'date-fns';
import { es } from 'date-fns/locale';

interface TireSpecificationDetailDialogProps {
  open: boolean;
  specificationId: string | null;
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
 * Componente TireSpecificationDetailDialog
 */
export default function TireSpecificationDetailDialog({
  open,
  specificationId,
  onClose,
  onEdit,
}: TireSpecificationDetailDialogProps) {
  // =====================================================
  // DATA FETCHING
  // =====================================================

  const { data: specification, isLoading, isError, error } = useTireSpecification(
    specificationId || undefined
  );

  // =====================================================
  // HANDLERS
  // =====================================================

  const handleEdit = () => {
    if (specification) {
      onEdit?.(specification.id);
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
      maxWidth="lg"
      fullWidth
      aria-labelledby="specification-detail-title"
    >
      {/* Header */}
      <DialogTitle id="specification-detail-title">
        <Stack
          direction="row"
          alignItems="center"
          justifyContent="space-between"
        >
          <Box>
            <Typography variant="h6" component="span">
              Detalle de Especificación Técnica
            </Typography>
            {specification && (
              <Chip
                label={specification.code}
                color="primary"
                size="small"
                sx={{ ml: 2 }}
              />
            )}
          </Box>
          <IconButton onClick={onClose} size="small">
            <CloseIcon />
          </IconButton>
        </Stack>
      </DialogTitle>

      <Divider />

      {/* Content */}
      <DialogContent>
        {isLoading && (
          <Box display="flex" justifyContent="center" alignItems="center" py={4}>
            <CircularProgress />
          </Box>
        )}

        {isError && (
          <Alert severity="error">
            Error al cargar la especificación: {error?.message}
          </Alert>
        )}

        {specification && (
          <Box sx={{ py: 2 }}>
            {/* Estado */}
            <Box sx={{ mb: 3 }}>
              <Chip
                icon={specification.isActive ? <ActiveIcon /> : <InactiveIcon />}
                label={specification.isActive ? 'ACTIVO' : 'INACTIVO'}
                color={specification.isActive ? 'success' : 'default'}
                variant="outlined"
              />
            </Box>

            {/* Información General */}
            <Paper variant="outlined" sx={{ p: 2, mb: 3 }}>
              <Typography variant="h6" gutterBottom color="primary">
                Información General
              </Typography>
              <Divider sx={{ mb: 2 }} />
              <Grid container spacing={3}>
                <Grid item xs={12} sm={6} md={3}>
                  <DetailField label="Código" value={specification.code} />
                </Grid>
                <Grid item xs={12} sm={6} md={3}>
                  <DetailField label="Marca" value={specification.brand.name} />
                </Grid>
                <Grid item xs={12} sm={6} md={3}>
                  <DetailField label="Tipo" value={specification.type.name} />
                </Grid>
                <Grid item xs={12} sm={6} md={3}>
                  <DetailField
                    label="Referencia"
                    value={specification.reference.name}
                  />
                </Grid>
                <Grid item xs={12} sm={6} md={3}>
                  <DetailField label="Dimensión" value={specification.dimension} />
                </Grid>
                <Grid item xs={12} sm={6} md={3}>
                  <DetailField
                    label="Peso"
                    value={specification.weightKg}
                    suffix=" kg"
                  />
                </Grid>
              </Grid>
            </Paper>

            {/* Especificaciones de Rendimiento */}
            <Paper variant="outlined" sx={{ p: 2, mb: 3 }}>
              <Typography variant="h6" gutterBottom color="primary">
                Especificaciones de Rendimiento
              </Typography>
              <Divider sx={{ mb: 2 }} />
              <Grid container spacing={3}>
                <Grid item xs={12} sm={6} md={4}>
                  <DetailField
                    label="Kilometraje Esperado"
                    value={specification.expectedMileage?.toLocaleString()}
                    suffix=" km"
                  />
                </Grid>
                <Grid item xs={12} sm={6} md={4}>
                  <DetailField
                    label="Reencauches Esperados"
                    value={specification.expectedRetreads}
                  />
                </Grid>
                <Grid item xs={12} sm={6} md={4}>
                  <DetailField
                    label="Porcentaje de Pérdida"
                    value={specification.expectedLossPercentage}
                    suffix="%"
                  />
                </Grid>
                <Grid item xs={12} sm={6} md={4}>
                  <DetailField
                    label="Total Esperado"
                    value={specification.totalExpected?.toLocaleString()}
                    suffix=" km"
                  />
                </Grid>
                <Grid item xs={12} sm={6} md={4}>
                  <DetailField
                    label="Costo por Hora"
                    value={specification.costPerHour?.toLocaleString('es-CO', {
                      style: 'currency',
                      currency: 'COP',
                    })}
                  />
                </Grid>
              </Grid>

              {/* Rangos de Kilometraje */}
              {(specification.mileageRangeMin ||
                specification.mileageRangeAvg ||
                specification.mileageRangeMax) && (
                <>
                  <Divider sx={{ my: 2 }} />
                  <Typography variant="subtitle2" gutterBottom>
                    Rangos de Kilometraje
                  </Typography>
                  <Grid container spacing={3}>
                    <Grid item xs={12} sm={4}>
                      <DetailField
                        label="Rango Mínimo"
                        value={specification.mileageRangeMin?.toLocaleString()}
                        suffix=" km"
                      />
                    </Grid>
                    <Grid item xs={12} sm={4}>
                      <DetailField
                        label="Rango Promedio"
                        value={specification.mileageRangeAvg?.toLocaleString()}
                        suffix=" km"
                      />
                    </Grid>
                    <Grid item xs={12} sm={4}>
                      <DetailField
                        label="Rango Máximo"
                        value={specification.mileageRangeMax?.toLocaleString()}
                        suffix=" km"
                      />
                    </Grid>
                  </Grid>
                </>
              )}
            </Paper>

            {/* Profundidades Iniciales */}
            <Paper variant="outlined" sx={{ p: 2, mb: 3 }}>
              <Typography variant="h6" gutterBottom color="primary">
                Profundidades Iniciales (mm)
              </Typography>
              <Divider sx={{ mb: 2 }} />
              <Grid container spacing={3}>
                <Grid item xs={12} sm={6} md={3}>
                  <DetailField
                    label="Profundidad Interna"
                    value={specification.initialDepthInternalMm}
                    suffix=" mm"
                  />
                </Grid>
                <Grid item xs={12} sm={6} md={3}>
                  <DetailField
                    label="Profundidad Central"
                    value={specification.initialDepthCentralMm}
                    suffix=" mm"
                  />
                </Grid>
                <Grid item xs={12} sm={6} md={3}>
                  <DetailField
                    label="Profundidad Externa"
                    value={specification.initialDepthExternalMm}
                    suffix=" mm"
                  />
                </Grid>
                <Grid item xs={12} sm={6} md={3}>
                  <Box>
                    <Typography variant="caption" color="text.secondary" display="block">
                      Profundidad Promedio
                    </Typography>
                    <Typography variant="body1" fontWeight="bold" color="primary">
                      {specification.averageInitialDepth} mm
                    </Typography>
                  </Box>
                </Grid>
              </Grid>
            </Paper>

            {/* Información Comercial */}
            {(specification.lastPurchaseQuantity ||
              specification.lastPurchaseUnitPrice ||
              specification.lastPurchaseDate) && (
              <Paper variant="outlined" sx={{ p: 2, mb: 3 }}>
                <Typography variant="h6" gutterBottom color="primary">
                  Última Compra
                </Typography>
                <Divider sx={{ mb: 2 }} />
                <Grid container spacing={3}>
                  <Grid item xs={12} sm={6} md={4}>
                    <DetailField
                      label="Cantidad"
                      value={specification.lastPurchaseQuantity}
                      suffix=" unidades"
                    />
                  </Grid>
                  <Grid item xs={12} sm={6} md={4}>
                    <DetailField
                      label="Precio Unitario"
                      value={specification.lastPurchaseUnitPrice?.toLocaleString(
                        'es-CO',
                        {
                          style: 'currency',
                          currency: 'COP',
                        }
                      )}
                    />
                  </Grid>
                  <Grid item xs={12} sm={6} md={4}>
                    <DetailField
                      label="Fecha de Compra"
                      value={specification.lastPurchaseDate}
                    />
                  </Grid>
                </Grid>
              </Paper>
            )}

            {/* Proveedores */}
            {(specification.mainProvider ||
              specification.secondaryProvider ||
              specification.lastUsedProvider) && (
              <Paper variant="outlined" sx={{ p: 2, mb: 3 }}>
                <Typography variant="h6" gutterBottom color="primary">
                  Proveedores
                </Typography>
                <Divider sx={{ mb: 2 }} />
                <Grid container spacing={3}>
                  {specification.mainProvider && (
                    <Grid item xs={12} sm={4}>
                      <DetailField
                        label="Proveedor Principal"
                        value={specification.mainProvider.name}
                      />
                    </Grid>
                  )}
                  {specification.secondaryProvider && (
                    <Grid item xs={12} sm={4}>
                      <DetailField
                        label="Proveedor Secundario"
                        value={specification.secondaryProvider.name}
                      />
                    </Grid>
                  )}
                  {specification.lastUsedProvider && (
                    <Grid item xs={12} sm={4}>
                      <DetailField
                        label="Último Proveedor Usado"
                        value={specification.lastUsedProvider.name}
                      />
                    </Grid>
                  )}
                </Grid>
              </Paper>
            )}

            {/* Auditoría */}
            <Paper variant="outlined" sx={{ p: 2 }}>
              <Typography variant="h6" gutterBottom color="primary">
                Información de Auditoría
              </Typography>
              <Divider sx={{ mb: 2 }} />
              <Grid container spacing={3}>
                <Grid item xs={12} sm={6}>
                  <DetailField
                    label="Fecha de Creación"
                    value={formatDate(specification.createdAt)}
                  />
                </Grid>
                <Grid item xs={12} sm={6}>
                  <DetailField
                    label="Última Modificación"
                    value={formatDate(specification.updatedAt)}
                  />
                </Grid>
                {specification.deletedAt && (
                  <Grid item xs={12} sm={6}>
                    <DetailField
                      label="Fecha de Eliminación"
                      value={formatDate(specification.deletedAt)}
                    />
                  </Grid>
                )}
              </Grid>
            </Paper>
          </Box>
        )}
      </DialogContent>

      {/* Actions */}
      <DialogActions sx={{ px: 3, py: 2 }}>
        <Button onClick={onClose}>Cerrar</Button>
        {specification && onEdit && (
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
