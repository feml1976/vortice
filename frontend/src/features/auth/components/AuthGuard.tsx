/**
 * AuthGuard - Componente para proteger rutas que requieren autenticación
 */

import { Navigate, useLocation } from 'react-router-dom';
import { Box, CircularProgress } from '@mui/material';
import { useAuthStore } from '../store/authStore';

interface AuthGuardProps {
  children: React.ReactNode;
  requireAuth?: boolean;
  redirectTo?: string;
}

/**
 * Componente AuthGuard
 *
 * Protege rutas que requieren autenticación.
 * Si el usuario no está autenticado, redirige a la página de login.
 */
export default function AuthGuard({
  children,
  requireAuth = true,
  redirectTo = '/login',
}: AuthGuardProps) {
  const { isAuthenticated, isLoading } = useAuthStore();
  const location = useLocation();

  // Mostrar loading mientras se verifica la autenticación
  if (isLoading) {
    return (
      <Box
        sx={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          minHeight: '100vh',
        }}
      >
        <CircularProgress />
      </Box>
    );
  }

  // Si la ruta requiere autenticación y el usuario no está autenticado
  if (requireAuth && !isAuthenticated) {
    // Guardar la ubicación a la que el usuario intentaba acceder
    return <Navigate to={redirectTo} state={{ from: location }} replace />;
  }

  // Si la ruta NO requiere autenticación y el usuario está autenticado
  // (por ejemplo, página de login cuando ya está logueado)
  if (!requireAuth && isAuthenticated) {
    // Redirigir al dashboard o a la página anterior
    const from = (location.state as any)?.from?.pathname || '/dashboard';
    return <Navigate to={from} replace />;
  }

  // En cualquier otro caso, renderizar los children
  return <>{children}</>;
}
