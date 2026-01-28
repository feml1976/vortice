/**
 * Formulario de creación y edición de Oficinas
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
  useCreateOffice,
  useUpdateOffice,
  useOffice,
} from '../hooks/useOffices';
import type { CreateOfficeRequest, UpdateOfficeRequest } from '../types/organization.types';

// =====================================================
// VALIDATION SCHEMA
// =====================================================

const officeSchema = z.object({
  code: z
    .string()
    .min(1, 'El código es requerido')
    .max(10, 'Máximo 10 caracteres')
    .regex(/^[A-Z0-9]+$/, 'Solo letras mayúsculas y números sin espacios'),
  name: z
    .string()
    .min(1, 'El nombre es requerido')
    .max(100, 'Máximo 100 caracteres'),
  city: z
    .string()
    .min(1, 'La ciudad es requerida')
    .max(50, 'Máximo 50 caracteres'),
});

type OfficeFormData = z.infer<typeof officeSchema>;

interface OfficeFormProps {
  open: boolean;
  officeId: string | null;
  onClose: () => void;
  onSuccess?: () => void;
}

/**
 * Componente OfficeForm
 */
export default function OfficeForm({
  open,
  officeId,
  onClose,
  onSuccess,
}: OfficeFormProps) {
  const isEditing = !!officeId;

  // =====================================================
  // DATA FETCHING
  // =====================================================

  const { data: office, isLoading: isLoadingOffice } = useOffice(officeId || '', {
    enabled: isEditing && open,
  });

  const createMutation = useCreateOffice();
  const updateMutation = useUpdateOffice();

  const isSubmitting = createMutation.isPending || updateMutation.isPending;

  // =====================================================
  // FORM
  // =====================================================

  const {
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<OfficeFormData>({
    resolver: zodResolver(officeSchema),
    defaultValues: {
      code: '',
      name: '',
      city: '',
    },
  });

  // Reset form cuando cambia el officeId o se abre el dialog
  useEffect(() => {
    if (open) {
      if (isEditing && office) {
        reset({
          code: office.code,
          name: office.name,
          city: office.city,
        });
      } else {
        reset({
          code: '',
          name: '',
          city: '',
        });
      }
    }
  }, [open, isEditing, office, reset]);

  // =====================================================
  // HANDLERS
  // =====================================================

  /**
   * Maneja el submit del formulario
   */
  const onSubmit = async (data: OfficeFormData) => {
    try {
      if (isEditing && officeId) {
        const updateData: UpdateOfficeRequest = {
          name: data.name,
          city: data.city,
        };
        await updateMutation.mutateAsync({ id: officeId, data: updateData });
      } else {
        const createData: CreateOfficeRequest = {
          code: data.code,
          name: data.name,
          city: data.city,
        };
        await createMutation.mutateAsync(createData);
      }

      onSuccess?.();
      handleClose();
    } catch (error) {
      // El error ya fue manejado por el hook
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
      aria-labelledby="office-form-title"
    >
      <DialogTitle id="office-form-title">
        {isEditing ? 'Editar Oficina' : 'Nueva Oficina'}
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
          {isLoadingOffice ? (
            <CircularProgress />
          ) : (
            <Grid container spacing={2}>
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
                          : 'Ej: MAIN, BOG, MED')
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

              {/* Ciudad */}
              <Grid item xs={12}>
                <Controller
                  name="city"
                  control={control}
                  render={({ field }) => (
                    <TextField
                      {...field}
                      label="Ciudad"
                      fullWidth
                      required
                      disabled={isSubmitting}
                      error={!!errors.city}
                      helperText={errors.city?.message}
                    />
                  )}
                />
              </Grid>

              {/* Información */}
              {!isEditing && (
                <Grid item xs={12}>
                  <Alert severity="info">
                    La nueva oficina se creará en estado activo.
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
            disabled={isSubmitting || isLoadingOffice}
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
