/**
 * Página principal para la gestión de Ubicaciones de Almacén
 */

import { useState } from 'react';
import { Box, Container, Typography, Breadcrumbs, Link } from '@mui/material';
import { NavigateNext as NavigateNextIcon, Place as PlaceIcon } from '@mui/icons-material';
import { Link as RouterLink } from 'react-router-dom';
import WarehouseLocationList from '../components/WarehouseLocationList';
import WarehouseLocationForm from '../components/WarehouseLocationForm';

/**
 * Tipos para el estado de los diálogos
 */
type DialogState = {
  form: { open: boolean; id: string | null };
};

/**
 * Componente WarehouseLocationPage
 */
export default function WarehouseLocationPage() {
  // =====================================================
  // STATE
  // =====================================================

  const [dialogState, setDialogState] = useState<DialogState>({
    form: { open: false, id: null },
  });

  // =====================================================
  // HANDLERS - FORM DIALOG
  // =====================================================

  /**
   * Abre el formulario para crear una nueva ubicación
   */
  const handleOpenCreateForm = () => {
    setDialogState((prev) => ({
      ...prev,
      form: { open: true, id: null },
    }));
  };

  /**
   * Abre el formulario para editar una ubicación
   */
  const handleOpenEditForm = (id: string) => {
    setDialogState((prev) => ({
      ...prev,
      form: { open: true, id },
    }));
  };

  /**
   * Cierra el formulario
   */
  const handleCloseForm = () => {
    setDialogState((prev) => ({
      ...prev,
      form: { open: false, id: null },
    }));
  };

  /**
   * Callback cuando el formulario se completa exitosamente
   */
  const handleFormSuccess = () => {
    // El formulario ya muestra el toast de éxito
    // Aquí podríamos agregar lógica adicional si es necesario
  };

  // =====================================================
  // RENDER
  // =====================================================

  return (
    <Box
      sx={{
        minHeight: '100vh',
        bgcolor: 'background.default',
        py: 3,
      }}
    >
      <Container maxWidth="xl">
        {/* Header */}
        <Box sx={{ mb: 3 }}>
          {/* Breadcrumbs */}
          <Breadcrumbs
            separator={<NavigateNextIcon fontSize="small" />}
            aria-label="breadcrumb"
            sx={{ mb: 2 }}
          >
            <Link
              component={RouterLink}
              to="/dashboard"
              underline="hover"
              color="inherit"
              sx={{ display: 'flex', alignItems: 'center' }}
            >
              <PlaceIcon sx={{ mr: 0.5 }} fontSize="small" />
              Dashboard
            </Link>
            <Typography color="text.primary">Organización</Typography>
            <Typography color="text.primary">Ubicaciones de Almacén</Typography>
          </Breadcrumbs>

          {/* Título */}
          <Typography variant="h4" component="h1" gutterBottom fontWeight="bold">
            Gestión de Ubicaciones de Almacén
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Administre las ubicaciones físicas dentro de cada almacén para organizar el inventario de llantas y repuestos.
          </Typography>
        </Box>

        {/* Lista de Ubicaciones */}
        <WarehouseLocationList
          onEdit={handleOpenEditForm}
          onCreate={handleOpenCreateForm}
        />

        {/* Formulario de Creación/Edición */}
        <WarehouseLocationForm
          open={dialogState.form.open}
          locationId={dialogState.form.id}
          onClose={handleCloseForm}
          onSuccess={handleFormSuccess}
        />
      </Container>
    </Box>
  );
}
