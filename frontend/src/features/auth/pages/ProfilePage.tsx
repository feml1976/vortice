/**
 * Página de perfil de usuario
 */

import { useState, useEffect } from 'react';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import {
  Box,
  Button,
  Container,
  TextField,
  Typography,
  Paper,
  Avatar,
  Grid,
  CircularProgress,
  Alert,
  Chip,
} from '@mui/material';
import {
  Person as PersonIcon,
  Save as SaveIcon,
  Edit as EditIcon,
  Cancel as CancelIcon,
} from '@mui/icons-material';
import toast from 'react-hot-toast';
import { useAuthStore } from '../store/authStore';
import userService from '../services/userService';
import type { UpdateProfileRequest } from '../types/auth.types';

/**
 * Schema de validación para actualización de perfil
 */
const updateProfileSchema = z.object({
  email: z
    .string()
    .min(1, 'El email es requerido')
    .email('Email inválido')
    .max(100, 'Máximo 100 caracteres'),
  firstName: z
    .string()
    .min(1, 'El nombre es requerido')
    .max(100, 'Máximo 100 caracteres'),
  lastName: z
    .string()
    .min(1, 'El apellido es requerido')
    .max(100, 'Máximo 100 caracteres'),
});

type UpdateProfileFormData = z.infer<typeof updateProfileSchema>;

/**
 * Componente ProfilePage
 */
export default function ProfilePage() {
  const { user, setUser } = useAuthStore();
  const [isEditing, setIsEditing] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const {
    control,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting, isDirty },
  } = useForm<UpdateProfileFormData>({
    resolver: zodResolver(updateProfileSchema),
    defaultValues: {
      email: user?.email || '',
      firstName: user?.firstName || '',
      lastName: user?.lastName || '',
    },
  });

  useEffect(() => {
    if (user) {
      reset({
        email: user.email,
        firstName: user.firstName,
        lastName: user.lastName,
      });
    }
  }, [user, reset]);

  const handleEdit = () => {
    setIsEditing(true);
    setError(null);
  };

  const handleCancel = () => {
    setIsEditing(false);
    setError(null);
    reset();
  };

  const onSubmit = async (data: UpdateProfileFormData) => {
    try {
      setIsLoading(true);
      setError(null);

      const updateData: UpdateProfileRequest = {
        email: data.email.trim(),
        firstName: data.firstName.trim(),
        lastName: data.lastName.trim(),
      };

      const updatedUser = await userService.updateCurrentUserProfile(updateData);

      setUser(updatedUser);
      setIsEditing(false);
      toast.success('Perfil actualizado exitosamente');
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Error al actualizar perfil';
      setError(errorMessage);
      toast.error(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  if (!user) {
    return (
      <Container maxWidth="md">
        <Box sx={{ textAlign: 'center', py: 4 }}>
          <CircularProgress />
        </Box>
      </Container>
    );
  }

  return (
    <Container maxWidth="md">
      <Paper elevation={3} sx={{ p: 4, mt: 4 }}>
        {/* Header */}
        <Box
          sx={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            mb: 4,
            flexWrap: 'wrap',
            gap: 2,
          }}
        >
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
            <PersonIcon sx={{ fontSize: 40, color: 'primary.main' }} />
            <Typography variant="h4" component="h1" fontWeight="bold">
              Mi Perfil
            </Typography>
          </Box>

          {!isEditing && (
            <Button
              variant="contained"
              startIcon={<EditIcon />}
              onClick={handleEdit}
            >
              Editar Perfil
            </Button>
          )}
        </Box>

        {/* Avatar y usuario */}
        <Box sx={{ display: 'flex', justifyContent: 'center', mb: 4 }}>
          <Avatar
            sx={{
              width: 120,
              height: 120,
              bgcolor: 'primary.main',
              fontSize: '3rem',
            }}
          >
            {user.firstName?.charAt(0)}
            {user.lastName?.charAt(0)}
          </Avatar>
        </Box>

        {/* Información estática */}
        <Grid container spacing={2} sx={{ mb: 3 }}>
          <Grid item xs={12} sm={6}>
            <Typography variant="body2" color="text.secondary">
              Nombre de Usuario
            </Typography>
            <Typography variant="body1" fontWeight="bold">
              {user.username}
            </Typography>
          </Grid>
          <Grid item xs={12} sm={6}>
            <Typography variant="body2" color="text.secondary">
              Roles
            </Typography>
            <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap', mt: 0.5 }}>
              {user.roles?.map((role) => (
                <Chip
                  key={role}
                  label={role}
                  color="primary"
                  size="small"
                  variant="outlined"
                />
              ))}
            </Box>
          </Grid>
          <Grid item xs={12} sm={6}>
            <Typography variant="body2" color="text.secondary">
              Estado
            </Typography>
            <Chip
              label={user.isActive ? 'Activo' : 'Inactivo'}
              color={user.isActive ? 'success' : 'error'}
              size="small"
              sx={{ mt: 0.5 }}
            />
          </Grid>
        </Grid>

        {/* Mensaje de error */}
        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}

        {/* Formulario editable */}
        <form onSubmit={handleSubmit(onSubmit)}>
          <Grid container spacing={2}>
            <Grid item xs={12} sm={6}>
              <Controller
                name="firstName"
                control={control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    label="Nombre"
                    fullWidth
                    error={!!errors.firstName}
                    helperText={errors.firstName?.message}
                    disabled={!isEditing || isLoading}
                    autoComplete="given-name"
                  />
                )}
              />
            </Grid>

            <Grid item xs={12} sm={6}>
              <Controller
                name="lastName"
                control={control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    label="Apellido"
                    fullWidth
                    error={!!errors.lastName}
                    helperText={errors.lastName?.message}
                    disabled={!isEditing || isLoading}
                    autoComplete="family-name"
                  />
                )}
              />
            </Grid>

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
                    error={!!errors.email}
                    helperText={errors.email?.message}
                    disabled={!isEditing || isLoading}
                    autoComplete="email"
                  />
                )}
              />
            </Grid>
          </Grid>

          {/* Botones de acción (solo en modo edición) */}
          {isEditing && (
            <Box sx={{ display: 'flex', gap: 2, mt: 3 }}>
              <Button
                type="submit"
                variant="contained"
                fullWidth
                disabled={isLoading || isSubmitting || !isDirty}
                startIcon={
                  isLoading || isSubmitting ? <CircularProgress size={20} /> : <SaveIcon />
                }
              >
                {isLoading || isSubmitting ? 'Guardando...' : 'Guardar Cambios'}
              </Button>

              <Button
                variant="outlined"
                fullWidth
                onClick={handleCancel}
                disabled={isLoading || isSubmitting}
                startIcon={<CancelIcon />}
              >
                Cancelar
              </Button>
            </Box>
          )}
        </form>
      </Paper>
    </Container>
  );
}
