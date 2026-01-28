/**
 * Formulario de creación y edición de Proveedores de Llantas
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
  Typography,
  Divider,
} from '@mui/material';
import {
  Close as CloseIcon,
  Save as SaveIcon,
} from '@mui/icons-material';
import {
  useCreateTireSupplier,
  useUpdateTireSupplier,
  useTireSupplier,
} from '../hooks/useTireSuppliers';
import { OfficeSelector } from './OfficeSelector';
import { useOfficeContext } from '../context/OfficeContext';
import type {
  CreateTireSupplierRequest,
  UpdateTireSupplierRequest,
} from '../types/organization.types';

// =====================================================
// VALIDATION SCHEMA
// =====================================================

const tireSupplierSchema = z.object({
  code: z
    .string()
    .min(1, 'El código es requerido')
    .max(10, 'Máximo 10 caracteres')
    .regex(/^[A-Z0-9]+$/, 'Solo letras mayúsculas y números sin espacios'),
  name: z
    .string()
    .min(1, 'El nombre es requerido')
    .max(100, 'Máximo 100 caracteres'),
  nit: z.string().max(20, 'Máximo 20 caracteres').optional(),
  contactName: z.string().max(100, 'Máximo 100 caracteres').optional(),
  phone: z.string().max(20, 'Máximo 20 caracteres').optional(),
  email: z
    .string()
    .email('Email inválido')
    .max(100, 'Máximo 100 caracteres')
    .optional()
    .or(z.literal('')),
  address: z.string().max(255, 'Máximo 255 caracteres').optional(),
  city: z.string().max(50, 'Máximo 50 caracteres').optional(),
  officeId: z.string().uuid('Debe seleccionar una oficina'),
});

type TireSupplierFormData = z.infer<typeof tireSupplierSchema>;

interface TireSupplierFormProps {
  open: boolean;
  supplierId: string | null;
  onClose: () => void;
  onSuccess?: () => void;
}

/**
 * Componente TireSupplierForm
 */
export default function TireSupplierForm({
  open,
  supplierId,
  onClose,
  onSuccess,
}: TireSupplierFormProps) {
  const isEditing = !!supplierId;
  const { currentOffice } = useOfficeContext();

  // =====================================================
  // DATA FETCHING
  // =====================================================

  const { data: supplier, isLoading: isLoadingSupplier } = useTireSupplier(
    supplierId || '',
    { enabled: isEditing && open }
  );

  const createMutation = useCreateTireSupplier();
  const updateMutation = useUpdateTireSupplier();

  const isSubmitting = createMutation.isPending || updateMutation.isPending;

  // =====================================================
  // FORM
  // =====================================================

  const {
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<TireSupplierFormData>({
    resolver: zodResolver(tireSupplierSchema),
    defaultValues: {
      code: '',
      name: '',
      nit: '',
      contactName: '',
      phone: '',
      email: '',
      address: '',
      city: '',
      officeId: currentOffice?.id || '',
    },
  });

  // Reset form cuando cambia el supplierId o se abre el dialog
  useEffect(() => {
    if (open) {
      if (isEditing && supplier) {
        reset({
          code: supplier.code,
          name: supplier.name,
          nit: supplier.nit || '',
          contactName: supplier.contactName || '',
          phone: supplier.phone || '',
          email: supplier.email || '',
          address: supplier.address || '',
          city: supplier.city || '',
          officeId: supplier.officeId,
        });
      } else {
        reset({
          code: '',
          name: '',
          nit: '',
          contactName: '',
          phone: '',
          email: '',
          address: '',
          city: '',
          officeId: currentOffice?.id || '',
        });
      }
    }
  }, [open, isEditing, supplier, currentOffice, reset]);

  // =====================================================
  // HANDLERS
  // =====================================================

  /**
   * Maneja el submit del formulario
   */
  const onSubmit = async (data: TireSupplierFormData) => {
    try {
      if (isEditing && supplierId) {
        const updateData: UpdateTireSupplierRequest = {
          name: data.name,
          nit: data.nit || undefined,
          contactName: data.contactName || undefined,
          phone: data.phone || undefined,
          email: data.email || undefined,
          address: data.address || undefined,
          city: data.city || undefined,
        };
        await updateMutation.mutateAsync({ id: supplierId, data: updateData });
      } else {
        const createData: CreateTireSupplierRequest = {
          code: data.code,
          name: data.name,
          nit: data.nit || undefined,
          contactName: data.contactName || undefined,
          phone: data.phone || undefined,
          email: data.email || undefined,
          address: data.address || undefined,
          city: data.city || undefined,
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
      maxWidth="md"
      fullWidth
      aria-labelledby="supplier-form-title"
    >
      <DialogTitle id="supplier-form-title">
        {isEditing ? 'Editar Proveedor' : 'Nuevo Proveedor'}
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
          {isLoadingSupplier ? (
            <CircularProgress />
          ) : (
            <Grid container spacing={3}>
              {/* Sección: Información Básica */}
              <Grid item xs={12}>
                <Typography variant="subtitle2" color="primary" gutterBottom>
                  Información Básica
                </Typography>
                <Divider sx={{ mb: 2 }} />
              </Grid>

              {/* Oficina */}
              <Grid item xs={12} sm={6}>
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
              <Grid item xs={12} sm={6}>
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
                          : 'Único dentro de la oficina. Ej: PROV01, MICH')
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
              <Grid item xs={12} sm={8}>
                <Controller
                  name="name"
                  control={control}
                  render={({ field }) => (
                    <TextField
                      {...field}
                      label="Nombre Comercial"
                      fullWidth
                      required
                      disabled={isSubmitting}
                      error={!!errors.name}
                      helperText={errors.name?.message}
                    />
                  )}
                />
              </Grid>

              {/* NIT */}
              <Grid item xs={12} sm={4}>
                <Controller
                  name="nit"
                  control={control}
                  render={({ field }) => (
                    <TextField
                      {...field}
                      label="NIT"
                      fullWidth
                      disabled={isSubmitting}
                      error={!!errors.nit}
                      helperText={errors.nit?.message}
                    />
                  )}
                />
              </Grid>

              {/* Sección: Información de Contacto */}
              <Grid item xs={12}>
                <Typography variant="subtitle2" color="primary" gutterBottom>
                  Información de Contacto
                </Typography>
                <Divider sx={{ mb: 2 }} />
              </Grid>

              {/* Nombre de Contacto */}
              <Grid item xs={12} sm={6}>
                <Controller
                  name="contactName"
                  control={control}
                  render={({ field }) => (
                    <TextField
                      {...field}
                      label="Nombre de Contacto"
                      fullWidth
                      disabled={isSubmitting}
                      error={!!errors.contactName}
                      helperText={errors.contactName?.message}
                    />
                  )}
                />
              </Grid>

              {/* Teléfono */}
              <Grid item xs={12} sm={6}>
                <Controller
                  name="phone"
                  control={control}
                  render={({ field }) => (
                    <TextField
                      {...field}
                      label="Teléfono"
                      fullWidth
                      disabled={isSubmitting}
                      error={!!errors.phone}
                      helperText={errors.phone?.message}
                    />
                  )}
                />
              </Grid>

              {/* Email */}
              <Grid item xs={12}>
                <Controller
                  name="email"
                  control={control}
                  render={({ field }) => (
                    <TextField
                      {...field}
                      label="Email"
                      type="email"
                      fullWidth
                      disabled={isSubmitting}
                      error={!!errors.email}
                      helperText={errors.email?.message}
                    />
                  )}
                />
              </Grid>

              {/* Dirección */}
              <Grid item xs={12} sm={8}>
                <Controller
                  name="address"
                  control={control}
                  render={({ field }) => (
                    <TextField
                      {...field}
                      label="Dirección"
                      fullWidth
                      disabled={isSubmitting}
                      error={!!errors.address}
                      helperText={errors.address?.message}
                    />
                  )}
                />
              </Grid>

              {/* Ciudad */}
              <Grid item xs={12} sm={4}>
                <Controller
                  name="city"
                  control={control}
                  render={({ field }) => (
                    <TextField
                      {...field}
                      label="Ciudad"
                      fullWidth
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
                    El nuevo proveedor se creará en estado activo.
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
            disabled={isSubmitting || isLoadingSupplier}
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
