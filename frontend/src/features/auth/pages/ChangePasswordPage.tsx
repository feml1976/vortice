/**
 * Página de cambio de contraseña
 */

import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
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
  InputAdornment,
  IconButton,
  CircularProgress,
  Alert,
} from '@mui/material';
import {
  Visibility,
  VisibilityOff,
  Lock as LockIcon,
  Save as SaveIcon,
  ArrowBack as BackIcon,
} from '@mui/icons-material';
import toast from 'react-hot-toast';
import userService from '../services/userService';
import type { ChangePasswordRequest } from '../types/auth.types';

/**
 * Schema de validación para cambio de contraseña
 */
const changePasswordSchema = z.object({
  currentPassword: z.string().min(1, 'La contraseña actual es requerida'),
  newPassword: z
    .string()
    .min(8, 'La nueva contraseña debe tener al menos 8 caracteres')
    .max(100, 'Máximo 100 caracteres')
    .regex(
      /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/,
      'Debe contener al menos una mayúscula, una minúscula y un número'
    ),
  confirmPassword: z.string().min(1, 'Confirme su nueva contraseña'),
}).refine((data) => data.newPassword === data.confirmPassword, {
  message: 'Las contraseñas no coinciden',
  path: ['confirmPassword'],
});

type ChangePasswordFormData = z.infer<typeof changePasswordSchema>;

/**
 * Componente ChangePasswordPage
 */
export default function ChangePasswordPage() {
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [showCurrentPassword, setShowCurrentPassword] = useState(false);
  const [showNewPassword, setShowNewPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  const {
    control,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<ChangePasswordFormData>({
    resolver: zodResolver(changePasswordSchema),
    defaultValues: {
      currentPassword: '',
      newPassword: '',
      confirmPassword: '',
    },
  });

  const onSubmit = async (data: ChangePasswordFormData) => {
    try {
      setIsLoading(true);
      setError(null);

      const changePasswordData: ChangePasswordRequest = {
        currentPassword: data.currentPassword,
        newPassword: data.newPassword,
        confirmPassword: data.confirmPassword,
      };

      await userService.changePassword(changePasswordData);

      toast.success('Contraseña cambiada exitosamente');
      reset();
      navigate('/profile');
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Error al cambiar contraseña';
      setError(errorMessage);
      toast.error(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Container maxWidth="sm">
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
            <LockIcon sx={{ fontSize: 40, color: 'primary.main' }} />
            <Typography variant="h4" component="h1" fontWeight="bold">
              Cambiar Contraseña
            </Typography>
          </Box>
        </Box>

        <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
          Ingresa tu contraseña actual y elige una nueva contraseña segura.
        </Typography>

        {/* Mensaje de error */}
        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}

        {/* Formulario */}
        <form onSubmit={handleSubmit(onSubmit)}>
          {/* Contraseña actual */}
          <Controller
            name="currentPassword"
            control={control}
            render={({ field }) => (
              <TextField
                {...field}
                label="Contraseña Actual"
                type={showCurrentPassword ? 'text' : 'password'}
                fullWidth
                margin="normal"
                error={!!errors.currentPassword}
                helperText={errors.currentPassword?.message}
                disabled={isLoading || isSubmitting}
                autoComplete="current-password"
                InputProps={{
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton
                        onClick={() => setShowCurrentPassword(!showCurrentPassword)}
                        edge="end"
                        disabled={isLoading || isSubmitting}
                      >
                        {showCurrentPassword ? <VisibilityOff /> : <Visibility />}
                      </IconButton>
                    </InputAdornment>
                  ),
                }}
              />
            )}
          />

          {/* Nueva contraseña */}
          <Controller
            name="newPassword"
            control={control}
            render={({ field }) => (
              <TextField
                {...field}
                label="Nueva Contraseña"
                type={showNewPassword ? 'text' : 'password'}
                fullWidth
                margin="normal"
                error={!!errors.newPassword}
                helperText={errors.newPassword?.message}
                disabled={isLoading || isSubmitting}
                autoComplete="new-password"
                InputProps={{
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton
                        onClick={() => setShowNewPassword(!showNewPassword)}
                        edge="end"
                        disabled={isLoading || isSubmitting}
                      >
                        {showNewPassword ? <VisibilityOff /> : <Visibility />}
                      </IconButton>
                    </InputAdornment>
                  ),
                }}
              />
            )}
          />

          {/* Confirmar nueva contraseña */}
          <Controller
            name="confirmPassword"
            control={control}
            render={({ field }) => (
              <TextField
                {...field}
                label="Confirmar Nueva Contraseña"
                type={showConfirmPassword ? 'text' : 'password'}
                fullWidth
                margin="normal"
                error={!!errors.confirmPassword}
                helperText={errors.confirmPassword?.message}
                disabled={isLoading || isSubmitting}
                autoComplete="new-password"
                InputProps={{
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton
                        onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                        edge="end"
                        disabled={isLoading || isSubmitting}
                      >
                        {showConfirmPassword ? <VisibilityOff /> : <Visibility />}
                      </IconButton>
                    </InputAdornment>
                  ),
                }}
              />
            )}
          />

          {/* Botones */}
          <Box sx={{ mt: 3, display: 'flex', gap: 2 }}>
            <Button
              type="submit"
              variant="contained"
              fullWidth
              disabled={isLoading || isSubmitting}
              startIcon={
                isLoading || isSubmitting ? <CircularProgress size={20} /> : <SaveIcon />
              }
            >
              {isLoading || isSubmitting ? 'Guardando...' : 'Cambiar Contraseña'}
            </Button>

            <Button
              variant="outlined"
              fullWidth
              onClick={() => navigate('/profile')}
              disabled={isLoading || isSubmitting}
              startIcon={<BackIcon />}
            >
              Cancelar
            </Button>
          </Box>
        </form>

        {/* Ayuda */}
        <Alert severity="info" sx={{ mt: 3 }}>
          <Typography variant="body2" fontWeight="bold" gutterBottom>
            Requisitos de la contraseña:
          </Typography>
          <Typography variant="body2" component="ul" sx={{ pl: 2, m: 0 }}>
            <li>Mínimo 8 caracteres</li>
            <li>Al menos una letra mayúscula</li>
            <li>Al menos una letra minúscula</li>
            <li>Al menos un número</li>
          </Typography>
        </Alert>
      </Paper>
    </Container>
  );
}
