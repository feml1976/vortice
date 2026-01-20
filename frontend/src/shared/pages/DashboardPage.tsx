/**
 * Página de Dashboard
 */

import { Box, Container, Typography, Button, Paper, Avatar, Chip } from '@mui/material';
import { Dashboard as DashboardIcon, ExitToApp as LogoutIcon } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '@/features/auth/store/authStore';

/**
 * Componente DashboardPage
 */
export default function DashboardPage() {
  const navigate = useNavigate();
  const { user, logout } = useAuthStore();

  /**
   * Maneja el cierre de sesión
   */
  const handleLogout = async () => {
    try {
      await logout();
      navigate('/login');
    } catch (error) {
      console.error('Error al cerrar sesión:', error);
    }
  };

  return (
    <Box
      sx={{
        minHeight: '100vh',
        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
        py: 4,
      }}
    >
      <Container maxWidth="lg">
        <Paper
          elevation={3}
          sx={{
            p: 4,
            borderRadius: 2,
          }}
        >
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
              <DashboardIcon sx={{ fontSize: 40, color: 'primary.main' }} />
              <Typography variant="h4" component="h1" fontWeight="bold">
                Dashboard
              </Typography>
            </Box>

            <Button
              variant="outlined"
              color="error"
              startIcon={<LogoutIcon />}
              onClick={handleLogout}
            >
              Cerrar Sesión
            </Button>
          </Box>

          {/* Información del Usuario */}
          <Paper
            elevation={1}
            sx={{
              p: 3,
              mb: 4,
              backgroundColor: 'primary.lighter',
              border: '1px solid',
              borderColor: 'primary.light',
            }}
          >
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 3, flexWrap: 'wrap' }}>
              <Avatar
                sx={{
                  width: 80,
                  height: 80,
                  bgcolor: 'primary.main',
                  fontSize: '2rem',
                }}
              >
                {user?.firstName?.charAt(0)}
                {user?.lastName?.charAt(0)}
              </Avatar>

              <Box sx={{ flex: 1 }}>
                <Typography variant="h5" gutterBottom>
                  {user?.firstName} {user?.lastName}
                </Typography>
                <Typography variant="body1" color="text.secondary" gutterBottom>
                  @{user?.username}
                </Typography>
                <Typography variant="body2" color="text.secondary" gutterBottom>
                  {user?.email}
                </Typography>
                <Box sx={{ display: 'flex', gap: 1, mt: 1, flexWrap: 'wrap' }}>
                  {user?.roles?.map((role) => (
                    <Chip
                      key={role}
                      label={role}
                      color="primary"
                      size="small"
                      variant="outlined"
                    />
                  ))}
                  <Chip
                    label={user?.isActive ? 'Activo' : 'Inactivo'}
                    color={user?.isActive ? 'success' : 'error'}
                    size="small"
                  />
                </Box>
              </Box>
            </Box>
          </Paper>

          {/* Contenido del Dashboard */}
          <Box>
            <Typography variant="h6" gutterBottom>
              Bienvenido al Sistema de Gestión de Taller - Vórtice
            </Typography>
            <Typography variant="body1" color="text.secondary" paragraph>
              Has iniciado sesión exitosamente. Esta es una página placeholder del dashboard.
            </Typography>
            <Typography variant="body2" color="text.secondary" paragraph>
              <strong>Funcionalidades implementadas:</strong>
            </Typography>
            <ul>
              <li>✅ Autenticación con JWT</li>
              <li>✅ Login con validación de formularios</li>
              <li>✅ Almacenamiento de estado con Zustand</li>
              <li>✅ Rutas protegidas con AuthGuard</li>
              <li>✅ Manejo de tokens (access y refresh)</li>
              <li>✅ Interceptores de Axios para refresh automático</li>
              <li>✅ Material-UI para diseño</li>
              <li>✅ Notificaciones con React Hot Toast</li>
            </ul>
            <Typography variant="body2" color="text.secondary" sx={{ mt: 2 }}>
              <strong>Próximos pasos:</strong> Implementar módulos de Workshop, Inventario, Compras, Flota, etc.
            </Typography>
          </Box>
        </Paper>
      </Container>
    </Box>
  );
}
