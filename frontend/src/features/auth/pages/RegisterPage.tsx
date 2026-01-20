/**
 * Página de registro de usuario
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
  Grid,
} from '@mui/material';
import {
  Visibility,
  VisibilityOff,
  PersonAdd as RegisterIcon,
  Build as BuildIcon,
} from '@mui/icons-material';
import { useAuthStore } from '../store/authStore';
import type { RegisterRequest } from '../types/auth.types';

/**
 * Schema de validación para el formulario de registro
 */
const registerSchema = z.object({
  username: z
    .string()
    .min(3, 'El nombre de usuario debe tener al menos 3 caracteres')
    .max(50, 'Máximo 50 caracteres')
    .regex(/^[a-zA-Z0-9_]+$/, 'Solo letras, números y guión bajo'),
  email: z
    .string()
    .min(1, 'El email es requerido')
    .email('Email inválido')
    .max(100, 'Máximo 100 caracteres'),
  password: z
    .string()
    .min(8, 'La contraseña debe tener al menos 8 caracteres')
    .max(100, 'Máximo 100 caracteres')
    .regex(
      /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/,
      'Debe contener al menos una mayúscula, una minúscula y un número'
    ),
  confirmPassword: z.string().min(1, 'Confirme su contraseña'),
  firstName: z
    .string()
    .min(1, 'El nombre es requerido')
    .max(100, 'Máximo 100 caracteres'),
  lastName: z
    .string()
    .min(1, 'El apellido es requerido')
    .max(100, 'Máximo 100 caracteres'),
}).refine((data) => data.password === data.confirmPassword, {
  message: 'Las contraseñas no coinciden',
  path: ['confirmPassword'],
});

type RegisterFormData = z.infer<typeof registerSchema>;

/**
 * Componente RegisterPage
 */
export default function RegisterPage() {
  const navigate = useNavigate();
  const { register: registerUser, isLoading, error } = useAuthStore();
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  const {
    control,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<RegisterFormData>({
    resolver: zodResolver(registerSchema),
    defaultValues: {
      username: '',
      email: '',
      password: '',
      confirmPassword: '',
      firstName: '',
      lastName: '',
    },
  });

  /**
   * Maneja el envío del formulario
   */
  const onSubmit = async (data: RegisterFormData) => {
    try {
      const registerData: RegisterRequest = {
        username: data.username.trim(),
        email: data.email.trim(),
        password: data.password,
        firstName: data.firstName.trim(),
        lastName: data.lastName.trim(),
      };

      await registerUser(registerData);
      navigate('/dashboard');
    } catch (err) {
      console.error('Error al registrar:', err);
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
      <Container maxWidth="md">
        <Paper
          elevation={6}
          sx={{
            p: 4,
            borderRadius: 2,
          }}
        >
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
            <Typography variant="h4" component="h1" gutterBottom fontWeight="bold">
              Crear Cuenta
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Sistema de Gestión de Taller - Vórtice
            </Typography>
          </Box>

          {/* Mensaje de error */}
          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}

          {/* Formulario */}
          <form onSubmit={handleSubmit(onSubmit)}>
            <Grid container spacing={2}>
              {/* Nombre de usuario */}
              <Grid item xs={12} sm={6}>
                <Controller
                  name="username"
                  control={control}
                  render={({ field }) => (
                    <TextField
                      {...field}
                      label="Nombre de Usuario"
                      fullWidth
                      error={!!errors.username}
                      helperText={errors.username?.message}
                      disabled={isLoading || isSubmitting}
                      autoComplete="username"
                    />
                  )}
                />
              </Grid>

              {/* Email */}
              <Grid item xs={12} sm={6}>
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
                      disabled={isLoading || isSubmitting}
                      autoComplete="email"
                    />
                  )}
                />
              </Grid>

              {/* Nombre */}
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
                      disabled={isLoading || isSubmitting}
                      autoComplete="given-name"
                    />
                  )}
                />
              </Grid>

              {/* Apellido */}
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
                      disabled={isLoading || isSubmitting}
                      autoComplete="family-name"
                    />
                  )}
                />
              </Grid>

              {/* Contraseña */}
              <Grid item xs={12} sm={6}>
                <Controller
                  name="password"
                  control={control}
                  render={({ field }) => (
                    <TextField
                      {...field}
                      label="Contraseña"
                      type={showPassword ? 'text' : 'password'}
                      fullWidth
                      error={!!errors.password}
                      helperText={errors.password?.message}
                      disabled={isLoading || isSubmitting}
                      autoComplete="new-password"
                      InputProps={{
                        endAdornment: (
                          <InputAdornment position="end">
                            <IconButton
                              onClick={() => setShowPassword(!showPassword)}
                              edge="end"
                              disabled={isLoading || isSubmitting}
                            >
                              {showPassword ? <VisibilityOff /> : <Visibility />}
                            </IconButton>
                          </InputAdornment>
                        ),
                      }}
                    />
                  )}
                />
              </Grid>

              {/* Confirmar contraseña */}
              <Grid item xs={12} sm={6}>
                <Controller
                  name="confirmPassword"
                  control={control}
                  render={({ field }) => (
                    <TextField
                      {...field}
                      label="Confirmar Contraseña"
                      type={showConfirmPassword ? 'text' : 'password'}
                      fullWidth
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
              </Grid>
            </Grid>

            {/* Botón de registro */}
            <Button
              type="submit"
              fullWidth
              variant="contained"
              size="large"
              disabled={isLoading || isSubmitting}
              startIcon={
                isLoading || isSubmitting ? <CircularProgress size={20} /> : <RegisterIcon />
              }
              sx={{ mt: 3, mb: 2 }}
            >
              {isLoading || isSubmitting ? 'Registrando...' : 'Crear Cuenta'}
            </Button>

            {/* Link a login */}
            <Box sx={{ textAlign: 'center' }}>
              <Typography variant="body2" color="text.secondary">
                ¿Ya tienes una cuenta?{' '}
                <Link component={RouterLink} to="/login" underline="hover">
                  Iniciar Sesión
                </Link>
              </Typography>
            </Box>
          </form>
        </Paper>
      </Container>
    </Box>
  );
}
