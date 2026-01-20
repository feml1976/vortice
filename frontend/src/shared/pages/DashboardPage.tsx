/**
 * Página de Dashboard
 */

import { Box, Typography, Paper, Avatar, Chip, Grid, Card, CardContent } from '@mui/material';
import { Dashboard as DashboardIcon } from '@mui/material';
import { useAuthStore } from '@/features/auth/store/authStore';

/**
 * Componente DashboardPage
 */
export default function DashboardPage() {
  const { user } = useAuthStore();

  return (
    <Box>
      {/* Header */}
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" component="h1" fontWeight="bold" gutterBottom>
          Dashboard
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Bienvenido al Sistema de Gestión de Taller - Vórtice
        </Typography>
      </Box>

      {/* Información del Usuario */}
      <Paper elevation={2} sx={{ p: 3, mb: 4 }}>
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
      <Grid container spacing={3}>
        <Grid item xs={12}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Funcionalidades Implementadas
              </Typography>
              <Typography variant="body2" color="text.secondary" paragraph>
                <strong>Autenticación y Gestión de Usuarios:</strong>
              </Typography>
              <ul>
                <li>✅ Login con validación de formularios</li>
                <li>✅ Registro de nuevos usuarios</li>
                <li>✅ Recuperación de contraseña</li>
                <li>✅ Perfil de usuario (ver/editar)</li>
                <li>✅ Cambio de contraseña</li>
                <li>✅ Autenticación con JWT</li>
                <li>✅ Manejo de tokens (access y refresh)</li>
                <li>✅ Interceptores de Axios para refresh automático</li>
              </ul>
              <Typography variant="body2" color="text.secondary" paragraph>
                <strong>Interfaz de Usuario:</strong>
              </Typography>
              <ul>
                <li>✅ Layout con Sidebar y AppBar</li>
                <li>✅ Rutas protegidas con AuthGuard</li>
                <li>✅ Material-UI v6 para diseño</li>
                <li>✅ Notificaciones con React Hot Toast</li>
                <li>✅ Almacenamiento de estado con Zustand</li>
                <li>✅ Navegación responsiva</li>
              </ul>
              <Typography variant="body2" color="text.secondary" sx={{ mt: 2 }}>
                <strong>Próximos pasos:</strong> Implementar módulos de negocio (Workshop,
                Inventario, Compras, Flota, HR, Reportes).
              </Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
}
