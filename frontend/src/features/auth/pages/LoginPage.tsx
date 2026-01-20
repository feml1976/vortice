/**
 * Página de inicio de sesión
 */

import { useState } from 'react';
import { useNavigate, Link as RouterLink } from 'react-router-dom';
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
  Login as LoginIcon,
  Build as BuildIcon,
} from '@mui/icons-material';
import { useAuthStore } from '../store/authStore';
import type { LoginRequest } from '../types/auth.types';

/**
 * Schema de validación para el formulario de login
 */
const loginSchema = z.object({
  usernameOrEmail: z
    .string()
    .min(1, 'El usuario o email es requerido')
    .max(100, 'Máximo 100 caracteres'),
  password: z
    .string()
    .min(1, 'La contraseña es requerida')
    .min(6, 'La contraseña debe tener al menos 6 caracteres')
    .max(100, 'Máximo 100 caracteres'),
});

type LoginFormData = z.infer<typeof loginSchema>;

/**
 * Componente LoginPage
 */
export default function LoginPage() {
  const navigate = useNavigate();
  const { login, isLoading, error } = useAuthStore();
  const [showPassword, setShowPassword] = useState(false);

  const {
    control,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      usernameOrEmail: '',
      password: '',
    },
  });

  /**
   * Maneja el envío del formulario
   */
  const onSubmit = async (data: LoginFormData) => {
    try {
      const credentials: LoginRequest = {
        usernameOrEmail: data.usernameOrEmail.trim(),
        password: data.password,
      };

      await login(credentials);
      navigate('/dashboard');
    } catch (error) {
      // El error ya fue manejado por el store
      console.error('Login error:', error);
    }
  };

  /**
   * Toggle para mostrar/ocultar contraseña
   */
  const handleTogglePasswordVisibility = () => {
    setShowPassword((prev) => !prev);
  };

  return (
    <Box
      sx={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
        padding: 2,
      }}
    >
      <Container maxWidth="sm">
        <Paper
          elevation={10}
          sx={{
            padding: { xs: 3, sm: 5 },
            borderRadius: 2,
          }}
        >
          {/* Logo y Título */}
          <Box sx={{ textAlign: 'center', mb: 4 }}>
            <BuildIcon
              sx={{
                fontSize: 60,
                color: 'primary.main',
                mb: 2,
              }}
            />
            <Typography variant="h4" component="h1" gutterBottom fontWeight="bold">
              Vórtice
            </Typography>
            <Typography variant="body1" color="text.secondary">
              Sistema de Gestión de Taller
            </Typography>
          </Box>

          {/* Alert de error */}
          {error && (
            <Alert severity="error" sx={{ mb: 3 }} onClose={() => useAuthStore.getState().setError(null)}>
              {error}
            </Alert>
          )}

          {/* Formulario de Login */}
          <Box component="form" onSubmit={handleSubmit(onSubmit)} noValidate>
            {/* Campo Usuario o Email */}
            <Controller
              name="usernameOrEmail"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  fullWidth
                  label="Usuario o Email"
                  type="text"
                  autoComplete="username"
                  autoFocus
                  margin="normal"
                  error={!!errors.usernameOrEmail}
                  helperText={errors.usernameOrEmail?.message}
                  disabled={isLoading || isSubmitting}
                  sx={{ mb: 2 }}
                />
              )}
            />

            {/* Campo Contraseña */}
            <Controller
              name="password"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  fullWidth
                  label="Contraseña"
                  type={showPassword ? 'text' : 'password'}
                  autoComplete="current-password"
                  margin="normal"
                  error={!!errors.password}
                  helperText={errors.password?.message}
                  disabled={isLoading || isSubmitting}
                  InputProps={{
                    endAdornment: (
                      <InputAdornment position="end">
                        <IconButton
                          aria-label="toggle password visibility"
                          onClick={handleTogglePasswordVisibility}
                          edge="end"
                          disabled={isLoading || isSubmitting}
                        >
                          {showPassword ? <VisibilityOff /> : <Visibility />}
                        </IconButton>
                      </InputAdornment>
                    ),
                  }}
                  sx={{ mb: 3 }}
                />
              )}
            />

            {/* Botón de Login */}
            <Button
              type="submit"
              fullWidth
              variant="contained"
              size="large"
              disabled={isLoading || isSubmitting}
              startIcon={
                isLoading || isSubmitting ? <CircularProgress size={20} color="inherit" /> : <LoginIcon />
              }
              sx={{ mb: 2, py: 1.5 }}
            >
              {isLoading || isSubmitting ? 'Iniciando sesión...' : 'Iniciar Sesión'}
            </Button>

            {/* Link de Registro */}
            <Box sx={{ textAlign: 'center', mt: 2 }}>
              <Typography variant="body2" color="text.secondary">
                ¿No tienes cuenta?{' '}
                <Link
                  component={RouterLink}
                  to="/register"
                  underline="hover"
                  sx={{ fontWeight: 'medium' }}
                >
                  Regístrate aquí
                </Link>
              </Typography>
            </Box>
          </Box>

          {/* Información adicional */}
          <Box sx={{ mt: 4, textAlign: 'center' }}>
            <Typography variant="caption" color="text.secondary">
              © 2026 Vórtice - TRANSER. Todos los derechos reservados.
            </Typography>
          </Box>
        </Paper>
      </Container>
    </Box>
  );
}
