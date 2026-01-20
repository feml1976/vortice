/**
 * Página de recuperación de contraseña
 */

import { useState } from 'react';
import { Link as RouterLink } from 'react-router-dom';
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
  Link,
  CircularProgress,
  Alert,
} from '@mui/material';
import {
  LockReset as LockResetIcon,
  Send as SendIcon,
  ArrowBack as BackIcon,
  Build as BuildIcon,
} from '@mui/icons-material';
import toast from 'react-hot-toast';
import authService from '../services/authService';
import type { ForgotPasswordRequest } from '../types/auth.types';

/**
 * Schema de validación
 */
const forgotPasswordSchema = z.object({
  email: z
    .string()
    .min(1, 'El email es requerido')
    .email('Email inválido')
    .max(100, 'Máximo 100 caracteres'),
});

type ForgotPasswordFormData = z.infer<typeof forgotPasswordSchema>;

/**
 * Componente ForgotPasswordPage
 */
export default function ForgotPasswordPage() {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  const {
    control,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<ForgotPasswordFormData>({
    resolver: zodResolver(forgotPasswordSchema),
    defaultValues: {
      email: '',
    },
  });

  const onSubmit = async (data: ForgotPasswordFormData) => {
    try {
      setIsLoading(true);
      setError(null);
      setSuccess(false);

      const requestData: ForgotPasswordRequest = {
        email: data.email.trim(),
      };

      const response = await authService.forgotPassword(requestData);

      setSuccess(true);
      toast.success('Email de recuperación enviado');

      // En desarrollo, mostrar el token en consola
      if (import.meta.env.DEV) {
        console.log('🔑 Token de reset (solo desarrollo):', response);
      }
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Error al solicitar recuperación';
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
              ¿Olvidaste tu contraseña?
            </Typography>
            <Typography variant="body2" color="text.secondary" textAlign="center">
              Ingresa tu email y te enviaremos instrucciones para recuperar tu cuenta.
            </Typography>
          </Box>

          {/* Mensaje de éxito */}
          {success && (
            <Alert severity="success" sx={{ mb: 2 }}>
              <Typography variant="body2" fontWeight="bold" gutterBottom>
                ¡Email enviado exitosamente!
              </Typography>
              <Typography variant="body2">
                Revisa tu bandeja de entrada y sigue las instrucciones para resetear tu
                contraseña.
              </Typography>
            </Alert>
          )}

          {/* Mensaje de error */}
          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}

          {!success && (
            <form onSubmit={handleSubmit(onSubmit)}>
              {/* Email */}
              <Controller
                name="email"
                control={control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    label="Email"
                    type="email"
                    fullWidth
                    margin="normal"
                    error={!!errors.email}
                    helperText={errors.email?.message}
                    disabled={isLoading || isSubmitting}
                    autoComplete="email"
                    autoFocus
                  />
                )}
              />

              {/* Botón enviar */}
              <Button
                type="submit"
                fullWidth
                variant="contained"
                size="large"
                disabled={isLoading || isSubmitting}
                startIcon={
                  isLoading || isSubmitting ? <CircularProgress size={20} /> : <SendIcon />
                }
                sx={{ mt: 3, mb: 2 }}
              >
                {isLoading || isSubmitting ? 'Enviando...' : 'Enviar Instrucciones'}
              </Button>
            </form>
          )}

          {/* Link de regreso */}
          <Box sx={{ textAlign: 'center', mt: 2 }}>
            <Link
              component={RouterLink}
              to="/login"
              underline="hover"
              sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 1 }}
            >
              <BackIcon fontSize="small" />
              Volver al Login
            </Link>
          </Box>

          {/* Nota de desarrollo */}
          {import.meta.env.DEV && success && (
            <Alert severity="warning" sx={{ mt: 2 }}>
              <Typography variant="body2" fontWeight="bold" gutterBottom>
                Modo Desarrollo:
              </Typography>
              <Typography variant="body2">
                El token de reset ha sido generado. Revisa la consola del navegador para
                obtenerlo y poder usarlo en la página de reset.
              </Typography>
            </Alert>
          )}
        </Paper>
      </Container>
    </Box>
  );
}
