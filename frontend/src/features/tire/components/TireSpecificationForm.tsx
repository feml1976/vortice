/**
 * Formulario de creación y edición de Especificaciones Técnicas
 */

import { useEffect, useMemo } from 'react';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import {
  Box,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Grid,
  Typography,
  Divider,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  FormHelperText,
  CircularProgress,
  Alert,
  Stack,
  IconButton,
  Paper,
  InputAdornment,
} from '@mui/material';
import {
  Close as CloseIcon,
  Save as SaveIcon,
} from '@mui/icons-material';
import { useTireFormCatalogs } from '../hooks/useTireCatalogs';
import {
  useCreateTireSpecification,
  useUpdateTireSpecification,
  useTireSpecification,
} from '../hooks/useTireSpecifications';
import type { CreateTireSpecificationRequest } from '../types/tire.types';

// =====================================================
// VALIDATION SCHEMA
// =====================================================

const tireSpecificationSchema = z
  .object({
    // Relaciones
    brandId: z.string().uuid('Debe seleccionar una marca válida'),
    typeId: z.string().uuid('Debe seleccionar un tipo válido'),
    referenceId: z.string().uuid('Debe seleccionar una referencia válida'),
    dimension: z.string().max(50, 'Máximo 50 caracteres').optional(),

    // Especificaciones de rendimiento
    expectedMileage: z.coerce
      .number()
      .int('Debe ser un número entero')
      .min(1, 'Debe ser mayor a 0'),
    mileageRangeMin: z.coerce
      .number()
      .int('Debe ser un número entero')
      .min(0, 'Debe ser mayor o igual a 0')
      .optional()
      .or(z.literal('')),
    mileageRangeAvg: z.coerce
      .number()
      .int('Debe ser un número entero')
      .min(0, 'Debe ser mayor o igual a 0')
      .optional()
      .or(z.literal('')),
    mileageRangeMax: z.coerce
      .number()
      .int('Debe ser un número entero')
      .min(0, 'Debe ser mayor o igual a 0')
      .optional()
      .or(z.literal('')),
    expectedRetreads: z.coerce
      .number()
      .int('Debe ser un número entero')
      .min(0, 'Debe ser mayor o igual a 0'),
    expectedLossPercentage: z.coerce
      .number()
      .min(0, 'Debe ser mayor o igual a 0')
      .max(100, 'No puede ser mayor a 100')
      .optional()
      .or(z.literal('')),
    totalExpected: z.coerce
      .number()
      .int('Debe ser un número entero')
      .min(0, 'Debe ser mayor o igual a 0')
      .optional()
      .or(z.literal('')),
    costPerHour: z.coerce
      .number()
      .min(0, 'Debe ser mayor o igual a 0')
      .optional()
      .or(z.literal('')),

    // Profundidades iniciales
    initialDepthInternalMm: z.coerce
      .number()
      .min(0, 'Debe ser mayor o igual a 0')
      .max(99.9, 'Máximo 99.9'),
    initialDepthCentralMm: z.coerce
      .number()
      .min(0, 'Debe ser mayor o igual a 0')
      .max(99.9, 'Máximo 99.9'),
    initialDepthExternalMm: z.coerce
      .number()
      .min(0, 'Debe ser mayor o igual a 0')
      .max(99.9, 'Máximo 99.9'),

    // Información comercial
    lastPurchaseQuantity: z.coerce
      .number()
      .int('Debe ser un número entero')
      .min(1, 'Debe ser mayor a 0')
      .optional()
      .or(z.literal('')),
    lastPurchaseUnitPrice: z.coerce
      .number()
      .min(0, 'Debe ser mayor o igual a 0')
      .optional()
      .or(z.literal('')),
    lastPurchaseDate: z.string().optional(),

    // Proveedores
    mainProviderId: z.string().uuid('ID inválido').optional().or(z.literal('')),
    secondaryProviderId: z.string().uuid('ID inválido').optional().or(z.literal('')),
    lastUsedProviderId: z.string().uuid('ID inválido').optional().or(z.literal('')),

    // Características físicas
    weightKg: z.coerce
      .number()
      .min(0, 'Debe ser mayor o igual a 0')
      .optional()
      .or(z.literal('')),
  })
  .refine(
    (data) => {
      // Validar que los rangos de kilometraje sean coherentes
      const min = data.mileageRangeMin;
      const avg = data.mileageRangeAvg;
      const max = data.mileageRangeMax;

      if (min && avg && min > avg) return false;
      if (avg && max && avg > max) return false;
      if (min && max && min > max) return false;

      return true;
    },
    {
      message: 'Los rangos de kilometraje deben ser coherentes (min ≤ avg ≤ max)',
      path: ['mileageRangeAvg'],
    }
  );

type TireSpecificationFormData = z.infer<typeof tireSpecificationSchema>;

// =====================================================
// COMPONENT PROPS
// =====================================================

interface TireSpecificationFormProps {
  open: boolean;
  specificationId?: string | null;
  onClose: () => void;
  onSuccess?: () => void;
}

/**
 * Componente TireSpecificationForm
 */
export default function TireSpecificationForm({
  open,
  specificationId,
  onClose,
  onSuccess,
}: TireSpecificationFormProps) {
  // =====================================================
  // MODE
  // =====================================================

  const isEditMode = !!specificationId;

  // =====================================================
  // DATA FETCHING
  // =====================================================

  // Catálogos
  const {
    brands,
    types,
    references,
    suppliers,
    isLoading: catalogsLoading,
    isError: catalogsError,
  } = useTireFormCatalogs();

  // Especificación para edición
  const {
    data: specification,
    isLoading: specLoading,
    isError: specError,
  } = useTireSpecification(isEditMode ? specificationId || undefined : undefined);

  // Mutations
  const createMutation = useCreateTireSpecification();
  const updateMutation = useUpdateTireSpecification();

  // =====================================================
  // FORM
  // =====================================================

  const {
    control,
    handleSubmit,
    reset,
    watch,
    formState: { errors },
  } = useForm<TireSpecificationFormData>({
    resolver: zodResolver(tireSpecificationSchema),
    defaultValues: {
      brandId: '',
      typeId: '',
      referenceId: '',
      dimension: '',
      expectedMileage: 0,
      mileageRangeMin: '' as any,
      mileageRangeAvg: '' as any,
      mileageRangeMax: '' as any,
      expectedRetreads: 0,
      expectedLossPercentage: '' as any,
      totalExpected: '' as any,
      costPerHour: '' as any,
      initialDepthInternalMm: 0,
      initialDepthCentralMm: 0,
      initialDepthExternalMm: 0,
      lastPurchaseQuantity: '' as any,
      lastPurchaseUnitPrice: '' as any,
      lastPurchaseDate: '',
      mainProviderId: '',
      secondaryProviderId: '',
      lastUsedProviderId: '',
      weightKg: '' as any,
    },
  });

  // Watch profundidades para calcular promedio
  const internalDepth = watch('initialDepthInternalMm');
  const centralDepth = watch('initialDepthCentralMm');
  const externalDepth = watch('initialDepthExternalMm');

  const averageDepth = useMemo(() => {
    const internal = Number(internalDepth) || 0;
    const central = Number(centralDepth) || 0;
    const external = Number(externalDepth) || 0;

    if (internal === 0 && central === 0 && external === 0) return 0;

    return ((internal + central + external) / 3).toFixed(2);
  }, [internalDepth, centralDepth, externalDepth]);

  // =====================================================
  // EFFECTS
  // =====================================================

  /**
   * Cargar datos al editar
   */
  useEffect(() => {
    if (isEditMode && specification) {
      reset({
        brandId: specification.brand.id,
        typeId: specification.type.id,
        referenceId: specification.reference.id,
        dimension: specification.dimension || '',
        expectedMileage: specification.expectedMileage,
        mileageRangeMin: specification.mileageRangeMin || ('' as any),
        mileageRangeAvg: specification.mileageRangeAvg || ('' as any),
        mileageRangeMax: specification.mileageRangeMax || ('' as any),
        expectedRetreads: specification.expectedRetreads,
        expectedLossPercentage: specification.expectedLossPercentage || ('' as any),
        totalExpected: specification.totalExpected || ('' as any),
        costPerHour: specification.costPerHour || ('' as any),
        initialDepthInternalMm: specification.initialDepthInternalMm,
        initialDepthCentralMm: specification.initialDepthCentralMm,
        initialDepthExternalMm: specification.initialDepthExternalMm,
        lastPurchaseQuantity: specification.lastPurchaseQuantity || ('' as any),
        lastPurchaseUnitPrice: specification.lastPurchaseUnitPrice || ('' as any),
        lastPurchaseDate: specification.lastPurchaseDate || '',
        mainProviderId: specification.mainProvider?.id || '',
        secondaryProviderId: specification.secondaryProvider?.id || '',
        lastUsedProviderId: specification.lastUsedProvider?.id || '',
        weightKg: specification.weightKg || ('' as any),
      });
    }
  }, [isEditMode, specification, reset]);

  /**
   * Reset form al cerrar
   */
  useEffect(() => {
    if (!open) {
      reset();
    }
  }, [open, reset]);

  // =====================================================
  // HANDLERS
  // =====================================================

  /**
   * Maneja el envío del formulario
   */
  const onSubmit = async (data: TireSpecificationFormData) => {
    try {
      // Convertir valores vacíos a undefined
      const requestData: CreateTireSpecificationRequest = {
        brandId: data.brandId,
        typeId: data.typeId,
        referenceId: data.referenceId,
        dimension: data.dimension || undefined,
        expectedMileage: data.expectedMileage,
        mileageRangeMin: data.mileageRangeMin ? Number(data.mileageRangeMin) : undefined,
        mileageRangeAvg: data.mileageRangeAvg ? Number(data.mileageRangeAvg) : undefined,
        mileageRangeMax: data.mileageRangeMax ? Number(data.mileageRangeMax) : undefined,
        expectedRetreads: data.expectedRetreads,
        expectedLossPercentage: data.expectedLossPercentage
          ? Number(data.expectedLossPercentage)
          : undefined,
        totalExpected: data.totalExpected ? Number(data.totalExpected) : undefined,
        costPerHour: data.costPerHour ? Number(data.costPerHour) : undefined,
        initialDepthInternalMm: data.initialDepthInternalMm,
        initialDepthCentralMm: data.initialDepthCentralMm,
        initialDepthExternalMm: data.initialDepthExternalMm,
        lastPurchaseQuantity: data.lastPurchaseQuantity
          ? Number(data.lastPurchaseQuantity)
          : undefined,
        lastPurchaseUnitPrice: data.lastPurchaseUnitPrice
          ? Number(data.lastPurchaseUnitPrice)
          : undefined,
        lastPurchaseDate: data.lastPurchaseDate || undefined,
        mainProviderId: data.mainProviderId || undefined,
        secondaryProviderId: data.secondaryProviderId || undefined,
        lastUsedProviderId: data.lastUsedProviderId || undefined,
        weightKg: data.weightKg ? Number(data.weightKg) : undefined,
      };

      if (isEditMode && specificationId) {
        await updateMutation.mutateAsync({
          id: specificationId,
          data: requestData,
        });
      } else {
        await createMutation.mutateAsync(requestData);
      }

      onSuccess?.();
      onClose();
    } catch (error) {
      console.error('Form submission error:', error);
    }
  };

  // =====================================================
  // LOADING & ERROR STATES
  // =====================================================

  const isLoading = catalogsLoading || (isEditMode && specLoading);
  const hasError = catalogsError || (isEditMode && specError);
  const isMutating = createMutation.isPending || updateMutation.isPending;

  // =====================================================
  // RENDER
  // =====================================================

  return (
    <Dialog
      open={open}
      onClose={onClose}
      maxWidth="lg"
      fullWidth
      aria-labelledby="specification-form-title"
    >
      {/* Header */}
      <DialogTitle id="specification-form-title">
        <Stack direction="row" alignItems="center" justifyContent="space-between">
          <Typography variant="h6">
            {isEditMode
              ? 'Editar Especificación Técnica'
              : 'Nueva Especificación Técnica'}
          </Typography>
          <IconButton onClick={onClose} size="small" disabled={isMutating}>
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

        {hasError && (
          <Alert severity="error" sx={{ mb: 2 }}>
            Error al cargar los datos del formulario
          </Alert>
        )}

        {!isLoading && !hasError && (
          <Box component="form" noValidate sx={{ py: 2 }}>
            {/* Información General */}
            <Paper variant="outlined" sx={{ p: 2, mb: 3 }}>
              <Typography variant="h6" gutterBottom color="primary">
                Información General
              </Typography>
              <Divider sx={{ mb: 2 }} />
              <Grid container spacing={2}>
                {/* Marca */}
                <Grid item xs={12} sm={6} md={3}>
                  <Controller
                    name="brandId"
                    control={control}
                    render={({ field }) => (
                      <FormControl fullWidth error={!!errors.brandId}>
                        <InputLabel>Marca *</InputLabel>
                        <Select {...field} label="Marca *">
                          <MenuItem value="">
                            <em>Seleccione</em>
                          </MenuItem>
                          {brands.map((brand) => (
                            <MenuItem key={brand.id} value={brand.id}>
                              {brand.name}
                            </MenuItem>
                          ))}
                        </Select>
                        {errors.brandId && (
                          <FormHelperText>{errors.brandId.message}</FormHelperText>
                        )}
                      </FormControl>
                    )}
                  />
                </Grid>

                {/* Tipo */}
                <Grid item xs={12} sm={6} md={3}>
                  <Controller
                    name="typeId"
                    control={control}
                    render={({ field }) => (
                      <FormControl fullWidth error={!!errors.typeId}>
                        <InputLabel>Tipo *</InputLabel>
                        <Select {...field} label="Tipo *">
                          <MenuItem value="">
                            <em>Seleccione</em>
                          </MenuItem>
                          {types.map((type) => (
                            <MenuItem key={type.id} value={type.id}>
                              {type.name}
                            </MenuItem>
                          ))}
                        </Select>
                        {errors.typeId && (
                          <FormHelperText>{errors.typeId.message}</FormHelperText>
                        )}
                      </FormControl>
                    )}
                  />
                </Grid>

                {/* Referencia */}
                <Grid item xs={12} sm={6} md={3}>
                  <Controller
                    name="referenceId"
                    control={control}
                    render={({ field }) => (
                      <FormControl fullWidth error={!!errors.referenceId}>
                        <InputLabel>Referencia *</InputLabel>
                        <Select {...field} label="Referencia *">
                          <MenuItem value="">
                            <em>Seleccione</em>
                          </MenuItem>
                          {references.map((ref) => (
                            <MenuItem key={ref.id} value={ref.id}>
                              {ref.name}
                            </MenuItem>
                          ))}
                        </Select>
                        {errors.referenceId && (
                          <FormHelperText>{errors.referenceId.message}</FormHelperText>
                        )}
                      </FormControl>
                    )}
                  />
                </Grid>

                {/* Dimensión */}
                <Grid item xs={12} sm={6} md={3}>
                  <Controller
                    name="dimension"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        fullWidth
                        label="Dimensión"
                        placeholder="Ej: 295/80R22.5"
                        error={!!errors.dimension}
                        helperText={errors.dimension?.message}
                      />
                    )}
                  />
                </Grid>

                {/* Peso */}
                <Grid item xs={12} sm={6} md={3}>
                  <Controller
                    name="weightKg"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        fullWidth
                        type="number"
                        label="Peso"
                        InputProps={{
                          endAdornment: (
                            <InputAdornment position="end">kg</InputAdornment>
                          ),
                        }}
                        error={!!errors.weightKg}
                        helperText={errors.weightKg?.message}
                      />
                    )}
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
              <Grid container spacing={2}>
                {/* Kilometraje Esperado */}
                <Grid item xs={12} sm={6} md={3}>
                  <Controller
                    name="expectedMileage"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        fullWidth
                        type="number"
                        label="Kilometraje Esperado *"
                        InputProps={{
                          endAdornment: (
                            <InputAdornment position="end">km</InputAdornment>
                          ),
                        }}
                        error={!!errors.expectedMileage}
                        helperText={errors.expectedMileage?.message}
                      />
                    )}
                  />
                </Grid>

                {/* Reencauches Esperados */}
                <Grid item xs={12} sm={6} md={3}>
                  <Controller
                    name="expectedRetreads"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        fullWidth
                        type="number"
                        label="Reencauches Esperados *"
                        error={!!errors.expectedRetreads}
                        helperText={errors.expectedRetreads?.message}
                      />
                    )}
                  />
                </Grid>

                {/* Porcentaje de Pérdida */}
                <Grid item xs={12} sm={6} md={3}>
                  <Controller
                    name="expectedLossPercentage"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        fullWidth
                        type="number"
                        label="% de Pérdida Esperado"
                        InputProps={{
                          endAdornment: (
                            <InputAdornment position="end">%</InputAdornment>
                          ),
                        }}
                        error={!!errors.expectedLossPercentage}
                        helperText={errors.expectedLossPercentage?.message}
                      />
                    )}
                  />
                </Grid>

                {/* Total Esperado */}
                <Grid item xs={12} sm={6} md={3}>
                  <Controller
                    name="totalExpected"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        fullWidth
                        type="number"
                        label="Total Esperado"
                        InputProps={{
                          endAdornment: (
                            <InputAdornment position="end">km</InputAdornment>
                          ),
                        }}
                        error={!!errors.totalExpected}
                        helperText={errors.totalExpected?.message}
                      />
                    )}
                  />
                </Grid>

                {/* Costo por Hora */}
                <Grid item xs={12} sm={6} md={3}>
                  <Controller
                    name="costPerHour"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        fullWidth
                        type="number"
                        label="Costo por Hora"
                        InputProps={{
                          startAdornment: (
                            <InputAdornment position="start">$</InputAdornment>
                          ),
                        }}
                        error={!!errors.costPerHour}
                        helperText={errors.costPerHour?.message}
                      />
                    )}
                  />
                </Grid>
              </Grid>

              {/* Rangos de Kilometraje */}
              <Divider sx={{ my: 2 }} />
              <Typography variant="subtitle2" gutterBottom>
                Rangos de Kilometraje (Opcional)
              </Typography>
              <Grid container spacing={2}>
                <Grid item xs={12} sm={4}>
                  <Controller
                    name="mileageRangeMin"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        fullWidth
                        type="number"
                        label="Rango Mínimo"
                        InputProps={{
                          endAdornment: (
                            <InputAdornment position="end">km</InputAdornment>
                          ),
                        }}
                        error={!!errors.mileageRangeMin}
                        helperText={errors.mileageRangeMin?.message}
                      />
                    )}
                  />
                </Grid>
                <Grid item xs={12} sm={4}>
                  <Controller
                    name="mileageRangeAvg"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        fullWidth
                        type="number"
                        label="Rango Promedio"
                        InputProps={{
                          endAdornment: (
                            <InputAdornment position="end">km</InputAdornment>
                          ),
                        }}
                        error={!!errors.mileageRangeAvg}
                        helperText={errors.mileageRangeAvg?.message}
                      />
                    )}
                  />
                </Grid>
                <Grid item xs={12} sm={4}>
                  <Controller
                    name="mileageRangeMax"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        fullWidth
                        type="number"
                        label="Rango Máximo"
                        InputProps={{
                          endAdornment: (
                            <InputAdornment position="end">km</InputAdornment>
                          ),
                        }}
                        error={!!errors.mileageRangeMax}
                        helperText={errors.mileageRangeMax?.message}
                      />
                    )}
                  />
                </Grid>
              </Grid>
            </Paper>

            {/* Profundidades Iniciales */}
            <Paper variant="outlined" sx={{ p: 2, mb: 3 }}>
              <Typography variant="h6" gutterBottom color="primary">
                Profundidades Iniciales (mm)
              </Typography>
              <Divider sx={{ mb: 2 }} />
              <Grid container spacing={2}>
                <Grid item xs={12} sm={6} md={3}>
                  <Controller
                    name="initialDepthInternalMm"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        fullWidth
                        type="number"
                        label="Profundidad Interna *"
                        InputProps={{
                          endAdornment: (
                            <InputAdornment position="end">mm</InputAdornment>
                          ),
                        }}
                        error={!!errors.initialDepthInternalMm}
                        helperText={errors.initialDepthInternalMm?.message}
                      />
                    )}
                  />
                </Grid>
                <Grid item xs={12} sm={6} md={3}>
                  <Controller
                    name="initialDepthCentralMm"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        fullWidth
                        type="number"
                        label="Profundidad Central *"
                        InputProps={{
                          endAdornment: (
                            <InputAdornment position="end">mm</InputAdornment>
                          ),
                        }}
                        error={!!errors.initialDepthCentralMm}
                        helperText={errors.initialDepthCentralMm?.message}
                      />
                    )}
                  />
                </Grid>
                <Grid item xs={12} sm={6} md={3}>
                  <Controller
                    name="initialDepthExternalMm"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        fullWidth
                        type="number"
                        label="Profundidad Externa *"
                        InputProps={{
                          endAdornment: (
                            <InputAdornment position="end">mm</InputAdornment>
                          ),
                        }}
                        error={!!errors.initialDepthExternalMm}
                        helperText={errors.initialDepthExternalMm?.message}
                      />
                    )}
                  />
                </Grid>
                <Grid item xs={12} sm={6} md={3}>
                  <Box>
                    <Typography variant="caption" color="text.secondary" display="block">
                      Profundidad Promedio (Calculado)
                    </Typography>
                    <Typography variant="h6" color="primary" sx={{ mt: 1 }}>
                      {averageDepth} mm
                    </Typography>
                  </Box>
                </Grid>
              </Grid>
            </Paper>

            {/* Información Comercial */}
            <Paper variant="outlined" sx={{ p: 2, mb: 3 }}>
              <Typography variant="h6" gutterBottom color="primary">
                Última Compra (Opcional)
              </Typography>
              <Divider sx={{ mb: 2 }} />
              <Grid container spacing={2}>
                <Grid item xs={12} sm={6} md={4}>
                  <Controller
                    name="lastPurchaseQuantity"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        fullWidth
                        type="number"
                        label="Cantidad"
                        InputProps={{
                          endAdornment: (
                            <InputAdornment position="end">unidades</InputAdornment>
                          ),
                        }}
                        error={!!errors.lastPurchaseQuantity}
                        helperText={errors.lastPurchaseQuantity?.message}
                      />
                    )}
                  />
                </Grid>
                <Grid item xs={12} sm={6} md={4}>
                  <Controller
                    name="lastPurchaseUnitPrice"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        fullWidth
                        type="number"
                        label="Precio Unitario"
                        InputProps={{
                          startAdornment: (
                            <InputAdornment position="start">$</InputAdornment>
                          ),
                        }}
                        error={!!errors.lastPurchaseUnitPrice}
                        helperText={errors.lastPurchaseUnitPrice?.message}
                      />
                    )}
                  />
                </Grid>
                <Grid item xs={12} sm={6} md={4}>
                  <Controller
                    name="lastPurchaseDate"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        fullWidth
                        type="date"
                        label="Fecha de Compra"
                        InputLabelProps={{ shrink: true }}
                        error={!!errors.lastPurchaseDate}
                        helperText={errors.lastPurchaseDate?.message}
                      />
                    )}
                  />
                </Grid>
              </Grid>
            </Paper>

            {/* Proveedores */}
            <Paper variant="outlined" sx={{ p: 2 }}>
              <Typography variant="h6" gutterBottom color="primary">
                Proveedores (Opcional)
              </Typography>
              <Divider sx={{ mb: 2 }} />
              <Grid container spacing={2}>
                <Grid item xs={12} sm={4}>
                  <Controller
                    name="mainProviderId"
                    control={control}
                    render={({ field }) => (
                      <FormControl fullWidth error={!!errors.mainProviderId}>
                        <InputLabel>Proveedor Principal</InputLabel>
                        <Select {...field} label="Proveedor Principal">
                          <MenuItem value="">
                            <em>Ninguno</em>
                          </MenuItem>
                          {suppliers.map((supplier) => (
                            <MenuItem key={supplier.id} value={supplier.id}>
                              {supplier.name}
                            </MenuItem>
                          ))}
                        </Select>
                        {errors.mainProviderId && (
                          <FormHelperText>
                            {errors.mainProviderId.message}
                          </FormHelperText>
                        )}
                      </FormControl>
                    )}
                  />
                </Grid>
                <Grid item xs={12} sm={4}>
                  <Controller
                    name="secondaryProviderId"
                    control={control}
                    render={({ field }) => (
                      <FormControl fullWidth error={!!errors.secondaryProviderId}>
                        <InputLabel>Proveedor Secundario</InputLabel>
                        <Select {...field} label="Proveedor Secundario">
                          <MenuItem value="">
                            <em>Ninguno</em>
                          </MenuItem>
                          {suppliers.map((supplier) => (
                            <MenuItem key={supplier.id} value={supplier.id}>
                              {supplier.name}
                            </MenuItem>
                          ))}
                        </Select>
                        {errors.secondaryProviderId && (
                          <FormHelperText>
                            {errors.secondaryProviderId.message}
                          </FormHelperText>
                        )}
                      </FormControl>
                    )}
                  />
                </Grid>
                <Grid item xs={12} sm={4}>
                  <Controller
                    name="lastUsedProviderId"
                    control={control}
                    render={({ field }) => (
                      <FormControl fullWidth error={!!errors.lastUsedProviderId}>
                        <InputLabel>Último Proveedor Usado</InputLabel>
                        <Select {...field} label="Último Proveedor Usado">
                          <MenuItem value="">
                            <em>Ninguno</em>
                          </MenuItem>
                          {suppliers.map((supplier) => (
                            <MenuItem key={supplier.id} value={supplier.id}>
                              {supplier.name}
                            </MenuItem>
                          ))}
                        </Select>
                        {errors.lastUsedProviderId && (
                          <FormHelperText>
                            {errors.lastUsedProviderId.message}
                          </FormHelperText>
                        )}
                      </FormControl>
                    )}
                  />
                </Grid>
              </Grid>
            </Paper>
          </Box>
        )}
      </DialogContent>

      {/* Actions */}
      <DialogActions sx={{ px: 3, py: 2 }}>
        <Button onClick={onClose} disabled={isMutating}>
          Cancelar
        </Button>
        <Button
          variant="contained"
          startIcon={
            isMutating ? <CircularProgress size={16} /> : <SaveIcon />
          }
          onClick={handleSubmit(onSubmit)}
          disabled={isLoading || hasError || isMutating}
        >
          {isMutating ? 'Guardando...' : isEditMode ? 'Actualizar' : 'Crear'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
