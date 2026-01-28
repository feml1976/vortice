/**
 * Formulario de creación y edición de Ubicaciones de Almacén
 */

import { useEffect } from 'react';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import {
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Grid,
  CircularProgress,
  Alert,
  IconButton,
} from '@mui/material';
import {
  Close as CloseIcon,
  Save as SaveIcon,
} from '@mui/icons-material';
import {
  useCreateWarehouseLocation,
  useUpdateWarehouseLocation,
  useWarehouseLocation,
} from '../hooks/useWarehouseLocations';
import { OfficeSelector } from './OfficeSelector';
import { WarehouseSelector } from './WarehouseSelector';
import { useOfficeContext } from '../context/OfficeContext';
import type {
  CreateWarehouseLocationRequest,
  UpdateWarehouseLocationRequest,
} from '../types/organization.types';

// =====================================================
// VALIDATION SCHEMA
// =====================================================

const warehouseLocationSchema = z.object({
  code: z
    .string()
    .min(1, 'El código es requerido')
    .max(20, 'Máximo 20 caracteres')
    .regex(/^[A-Z0-9-]+$/, 'Solo letras mayúsculas, números y guiones'),
  description: z.string().max(255, 'Máximo 255 caracteres').optional(),
  warehouseId: z.string().uuid('Debe seleccionar un almacén'),
  officeId: z.string().uuid('Debe seleccionar una oficina'),
});

type WarehouseLocationFormData = z.infer<typeof warehouseLocationSchema>;

interface WarehouseLocationFormProps {
  open: boolean;
  locationId: string | null;
  onClose: () => void;
  onSuccess?: () => void;
}

/**
 * Componente WarehouseLocationForm
 */
export default function WarehouseLocationForm({
  open,
  locationId,
  onClose,
  onSuccess,
}: WarehouseLocationFormProps) {
  const isEditing = !!locationId;
  const { currentOffice } = useOfficeContext();

  // =====================================================
  // DATA FETCHING
  // =====================================================

  const { data: location, isLoading: isLoadingLocation } = useWarehouseLocation(
    locationId || '',
    { enabled: isEditing && open }
  );

  const createMutation = useCreateWarehouseLocation();
  const updateMutation = useUpdateWarehouseLocation();

  const isSubmitting = createMutation.isPending || updateMutation.isPending;

  // =====================================================
  // FORM
  // =====================================================

  const {
    control,
    handleSubmit,
    reset,
    watch,
    formState: { errors },
  } = useForm<WarehouseLocationFormData>({
    resolver: zodResolver(warehouseLocationSchema),
    defaultValues: {
      code: '',
      description: '',
      warehouseId: '',
      officeId: currentOffice?.id || '',
    },
  });

  const selectedOfficeId = watch('officeId');

  // Reset form cuando cambia el locationId o se abre el dialog
  useEffect(() => {
    if (open) {
      if (isEditing && location) {
        reset({
          code: location.code,
          description: location.description || '',
          warehouseId: location.warehouseId,
          officeId: currentOffice?.id || '',
        });
      } else {
        reset({
          code: '',
          description: '',
          warehouseId: '',
          officeId: currentOffice?.id || '',
        });
      }
    }
  }, [open, isEditing, location, currentOffice, reset]);

  // =====================================================
  // HANDLERS
  // =====================================================

  /**
   * Maneja el submit del formulario
   */
  const onSubmit = async (data: WarehouseLocationFormData) => {
    try {
      if (isEditing && locationId) {
        const updateData: UpdateWarehouseLocationRequest = {
          description: data.description,
        };
        await updateMutation.mutateAsync({ id: locationId, data: updateData });
      } else {
        const createData: CreateWarehouseLocationRequest = {
          code: data.code,
          description: data.description,
          warehouseId: data.warehouseId,
        };
        await createMutation.mutateAsync(createData);
      }

      onSuccess?.();
      handleClose();
    } catch (error) {
      console.error('Form submit error:', error);
    }
  };

  /**
   * Cierra el diálogo y resetea el formulario
   */
  const handleClose = () => {
    reset();
    onClose();
  };

  // =====================================================
  // RENDER
  // =====================================================

  return (
    <Dialog
      open={open}
      onClose={isSubmitting ? undefined : handleClose}
      maxWidth="sm"
      fullWidth
      aria-labelledby="location-form-title"
    >
      <DialogTitle id="location-form-title">
        {isEditing ? 'Editar Ubicación' : 'Nueva Ubicación'}
        <IconButton
          aria-label="cerrar"
          onClick={handleClose}
          disabled={isSubmitting}
          sx={{
            position: 'absolute',
            right: 8,
            top: 8,
          }}
        >
          <CloseIcon />
        </IconButton>
      </DialogTitle>

      <form onSubmit={handleSubmit(onSubmit)}>
        <DialogContent dividers>
          {isLoadingLocation ? (
            <CircularProgress />
          ) : (
            <Grid container spacing={2}>
              {/* Oficina (hidden field para cascading) */}
              <Grid item xs={12}>
                <Controller
                  name="officeId"
                  control={control}
                  render={({ field }) => (
                    <OfficeSelector
                      value={field.value}
                      onChange={field.onChange}
                      label="Oficina"
                      required
                      disabled={isEditing || isSubmitting}
                    />
                  )}
                />
              </Grid>

              {/* Almacén */}
              <Grid item xs={12}>
                <Controller
                  name="warehouseId"
                  control={control}
                  render={({ field }) => (
                    <WarehouseSelector
                      value={field.value}
                      onChange={field.onChange}
                      officeId={selectedOfficeId}
                      label="Almacén"
                      required
                      disabled={isEditing || isSubmitting}
                      error={!!errors.warehouseId}
                      helperText={
                        errors.warehouseId?.message ||
                        (isEditing ? 'El almacén no puede modificarse' : '')
                      }
                    />
                  )}
                />
              </Grid>

              {/* Código */}
              <Grid item xs={12}>
                <Controller
                  name="code"
                  control={control}
                  render={({ field }) => (
                    <TextField
                      {...field}
                      label="Código"
                      fullWidth
                      required
                      disabled={isEditing || isSubmitting}
                      error={!!errors.code}
                      helperText={
                        errors.code?.message ||
                        (isEditing
                          ? 'El código no puede modificarse'
                          : 'Único dentro del almacén. Ej: A1, EST-02, PASILLO-B')
                      }
                      inputProps={{ style: { textTransform: 'uppercase' } }}
                      onChange={(e) =>
                        field.onChange(e.target.value.toUpperCase())
                      }
                    />
                  )}
                />
              </Grid>

              {/* Descripción */}
              <Grid item xs={12}>
                <Controller
                  name="description"
                  control={control}
                  render={({ field }) => (
                    <TextField
                      {...field}
                      label="Descripción"
                      fullWidth
                      multiline
                      rows={3}
                      disabled={isSubmitting}
                      error={!!errors.description}
                      helperText={
                        errors.description?.message ||
                        'Descripción opcional de la ubicación física'
                      }
                    />
                  )}
                />
              </Grid>

              {/* Información */}
              {!isEditing && (
                <Grid item xs={12}>
                  <Alert severity="info">
                    La nueva ubicación se creará en estado activo.
                  </Alert>
                </Grid>
              )}
            </Grid>
          )}
        </DialogContent>

        <DialogActions sx={{ px: 3, py: 2 }}>
          <Button onClick={handleClose} disabled={isSubmitting}>
            Cancelar
          </Button>
          <Button
            type="submit"
            variant="contained"
            disabled={isSubmitting || isLoadingLocation}
            startIcon={
              isSubmitting ? <CircularProgress size={16} /> : <SaveIcon />
            }
          >
            {isSubmitting
              ? 'Guardando...'
              : isEditing
              ? 'Actualizar'
              : 'Crear'}
          </Button>
        </DialogActions>
      </form>
    </Dialog>
  );
}
