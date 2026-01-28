/**
 * Formulario de creación y edición de Almacenes
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
  useCreateWarehouse,
  useUpdateWarehouse,
  useWarehouse,
} from '../hooks/useWarehouses';
import { OfficeSelector } from './OfficeSelector';
import { useOfficeContext } from '../context/OfficeContext';
import type { CreateWarehouseRequest, UpdateWarehouseRequest } from '../types/organization.types';

// =====================================================
// VALIDATION SCHEMA
// =====================================================

const warehouseSchema = z.object({
  code: z
    .string()
    .min(1, 'El código es requerido')
    .max(10, 'Máximo 10 caracteres')
    .regex(/^[A-Z0-9]+$/, 'Solo letras mayúsculas y números sin espacios'),
  name: z
    .string()
    .min(1, 'El nombre es requerido')
    .max(100, 'Máximo 100 caracteres'),
  officeId: z.string().uuid('Debe seleccionar una oficina'),
});

type WarehouseFormData = z.infer<typeof warehouseSchema>;

interface WarehouseFormProps {
  open: boolean;
  warehouseId: string | null;
  onClose: () => void;
  onSuccess?: () => void;
}

/**
 * Componente WarehouseForm
 */
export default function WarehouseForm({
  open,
  warehouseId,
  onClose,
  onSuccess,
}: WarehouseFormProps) {
  const isEditing = !!warehouseId;
  const { currentOffice } = useOfficeContext();

  // =====================================================
  // DATA FETCHING
  // =====================================================

  const { data: warehouse, isLoading: isLoadingWarehouse } = useWarehouse(
    warehouseId || '',
    { enabled: isEditing && open }
  );

  const createMutation = useCreateWarehouse();
  const updateMutation = useUpdateWarehouse();

  const isSubmitting = createMutation.isPending || updateMutation.isPending;

  // =====================================================
  // FORM
  // =====================================================

  const {
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<WarehouseFormData>({
    resolver: zodResolver(warehouseSchema),
    defaultValues: {
      code: '',
      name: '',
      officeId: currentOffice?.id || '',
    },
  });

  // Reset form cuando cambia el warehouseId o se abre el dialog
  useEffect(() => {
    if (open) {
      if (isEditing && warehouse) {
        reset({
          code: warehouse.code,
          name: warehouse.name,
          officeId: warehouse.officeId,
        });
      } else {
        reset({
          code: '',
          name: '',
          officeId: currentOffice?.id || '',
        });
      }
    }
  }, [open, isEditing, warehouse, currentOffice, reset]);

  // =====================================================
  // HANDLERS
  // =====================================================

  /**
   * Maneja el submit del formulario
   */
  const onSubmit = async (data: WarehouseFormData) => {
    try {
      if (isEditing && warehouseId) {
        const updateData: UpdateWarehouseRequest = {
          name: data.name,
        };
        await updateMutation.mutateAsync({ id: warehouseId, data: updateData });
      } else {
        const createData: CreateWarehouseRequest = {
          code: data.code,
          name: data.name,
          officeId: data.officeId,
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
      aria-labelledby="warehouse-form-title"
    >
      <DialogTitle id="warehouse-form-title">
        {isEditing ? 'Editar Almacén' : 'Nuevo Almacén'}
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
          {isLoadingWarehouse ? (
            <CircularProgress />
          ) : (
            <Grid container spacing={2}>
              {/* Oficina */}
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
                      error={!!errors.officeId}
                      helperText={
                        errors.officeId?.message ||
                        (isEditing ? 'La oficina no puede modificarse' : '')
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
                          : 'Único dentro de la oficina. Ej: ALM01, CENTRAL')
                      }
                      inputProps={{ style: { textTransform: 'uppercase' } }}
                      onChange={(e) =>
                        field.onChange(e.target.value.toUpperCase())
                      }
                    />
                  )}
                />
              </Grid>

              {/* Nombre */}
              <Grid item xs={12}>
                <Controller
                  name="name"
                  control={control}
                  render={({ field }) => (
                    <TextField
                      {...field}
                      label="Nombre"
                      fullWidth
                      required
                      disabled={isSubmitting}
                      error={!!errors.name}
                      helperText={errors.name?.message}
                    />
                  )}
                />
              </Grid>

              {/* Información */}
              {!isEditing && (
                <Grid item xs={12}>
                  <Alert severity="info">
                    El nuevo almacén se creará en estado activo.
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
            disabled={isSubmitting || isLoadingWarehouse}
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
