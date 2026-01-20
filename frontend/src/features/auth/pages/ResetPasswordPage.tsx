/**
 * Página de reset de contraseña con token
 */

import { useState, useEffect } from 'react';
import { useNavigate, useSearchParams, Link as RouterLink } from 'react-router-dom';
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
  Link,
  CircularProgress,
  Alert,
} from '@mui/material';
import {
  Visibility,
  VisibilityOff,
  LockReset as LockResetIcon,
  Save as SaveIcon,
  Build as BuildIcon,
} from '@mui/icons-material';
import toast from 'react-hot-toast';
import authService from '../services/authService';
import type { ResetPasswordRequest } from '../types/auth.types';

/**
 * Schema de validación
 */
const resetPasswordSchema = z.object({
  resetToken: z.string().min(1, 'El token de reset es requerido'),
  newPassword: z
    .string()
    .min(8, 'La contraseña debe tener al menos 8 caracteres')
    .max(100, 'Máximo 100 caracteres')
    .regex(
      /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/,
      'Debe contener al menos una mayúscula, una minúscula y un número'
    ),
  confirmPassword: z.string().min(1, 'Confirme su contraseña'),
}).refine((data) => data.newPassword === data.confirmPassword, {
  message: 'Las contraseñas no coinciden',
  path: ['confirmPassword'],
});

type ResetPasswordFormData = z.infer<typeof resetPasswordSchema>;

/**
 * Componente ResetPasswordPage
 */
export default function ResetPasswordPage() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [showNewPassword, setShowNewPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  // Obtener token de la URL (si existe)
  const tokenFromUrl = searchParams.get('token') || '';

  const {
    control,
    handleSubmit,
    setValue,
    formState: { errors, isSubmitting },
  } = useForm<ResetPasswordFormData>({
    resolver: zodResolver(resetPasswordSchema),
    defaultValues: {
      resetToken: tokenFromUrl,
      newPassword: '',
      confirmPassword: '',
    },
  });

  useEffect(() => {
    if (tokenFromUrl) {
      setValue('resetToken', tokenFromUrl);
    }
  }, [tokenFromUrl, setValue]);

  const onSubmit = async (data: ResetPasswordFormData) => {
    try {
      setIsLoading(true);
      setError(null);

      const resetData: ResetPasswordRequest = {
        resetToken: data.resetToken.trim(),
        newPassword: data.newPassword,
        confirmPassword: data.confirmPassword,
      };

      await authService.resetPassword(resetData);

      toast.success('Contraseña reseteada exitosamente');
      navigate('/login');
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Error al resetear contraseña';
      setError(errorMessage);
      toast.error(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Box
      sx={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
        py: 4,
      }}
    >
      <Container maxWidth="sm">
        <Paper elevation={6} sx={{ p: 4, borderRadius: 2 }}>
          {/* Logo y título */}
          <Box
            sx={{
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              mb: 3,
            }}
          >
            <BuildIcon sx={{ fontSize: 60, color: 'primary.main', mb: 1 }} />
            <LockResetIcon sx={{ fontSize: 48, color: 'secondary.main', mb: 2 }} />
            <Typography variant="h4" component="h1" gutterBottom fontWeight="bold">
              Restablecer Contraseña
            </Typography>
            <Typography variant="body2" color="text.secondary" textAlign="center">
              Ingresa tu token de recuperación y elige una nueva contraseña segura.
            </Typography>
          </Box>

          {/* Mensaje de error */}
          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}

          <form onSubmit={handleSubmit(onSubmit)}>
            {/* Token de reset */}
            <Controller
              name="resetToken"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  label="Token de Recuperación"
                  fullWidth
                  margin="normal"
                  error={!!errors.resetToken}
                  helperText={
                    errors.resetToken?.message ||
                    'Copia el token del email de recuperación'
                  }
                  disabled={isLoading || isSubmitting || !!tokenFromUrl}
                  autoComplete="off"
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

            {/* Confirmar contraseña */}
            <Controller
              name="confirmPassword"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  label="Confirmar Contraseña"
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

            {/* Botón resetear */}
            <Button
              type="submit"
              fullWidth
              variant="contained"
              size="large"
              disabled={isLoading || isSubmitting}
              startIcon={
                isLoading || isSubmitting ? <CircularProgress size={20} /> : <SaveIcon />
              }
              sx={{ mt: 3, mb: 2 }}
            >
              {isLoading || isSubmitting ? 'Reseteando...' : 'Restablecer Contraseña'}
            </Button>
          </form>

          {/* Link de regreso */}
          <Box sx={{ textAlign: 'center' }}>
            <Typography variant="body2" color="text.secondary">
              ¿Recordaste tu contraseña?{' '}
              <Link component={RouterLink} to="/login" underline="hover">
                Iniciar Sesión
              </Link>
            </Typography>
          </Box>

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
    </Box>
  );
}
