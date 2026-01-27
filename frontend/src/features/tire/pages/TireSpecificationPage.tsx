/**
 * Página principal del módulo de Especificaciones Técnicas de Llantas
 */

import { useState } from 'react';
import { Box, Container, Typography, Breadcrumbs, Link } from '@mui/material';
import { NavigateNext as NavigateNextIcon, Build as BuildIcon } from '@mui/icons-material';
import { Link as RouterLink } from 'react-router-dom';
import TireSpecificationList from '../components/TireSpecificationList';
import TireSpecificationDetailDialog from '../components/TireSpecificationDetailDialog';
import TireSpecificationForm from '../components/TireSpecificationForm';

/**
 * Tipos para el estado de los diálogos
 */
type DialogState = {
  detail: { open: boolean; id: string | null };
  form: { open: boolean; id: string | null };
};

/**
 * Componente TireSpecificationPage
 */
export default function TireSpecificationPage() {
  // =====================================================
  // STATE
  // =====================================================

  const [dialogState, setDialogState] = useState<DialogState>({
    detail: { open: false, id: null },
    form: { open: false, id: null },
  });

  // =====================================================
  // HANDLERS - DETAIL DIALOG
  // =====================================================

  /**
   * Abre el diálogo de detalle
   */
  const handleOpenDetailDialog = (id: string) => {
    setDialogState((prev) => ({
      ...prev,
      detail: { open: true, id },
    }));
  };

  /**
   * Cierra el diálogo de detalle
   */
  const handleCloseDetailDialog = () => {
    setDialogState((prev) => ({
      ...prev,
      detail: { open: false, id: null },
    }));
  };

  /**
   * Abre el formulario de edición desde el detalle
   */
  const handleEditFromDetail = (id: string) => {
    setDialogState({
      detail: { open: false, id: null },
      form: { open: true, id },
    });
  };

  // =====================================================
  // HANDLERS - FORM DIALOG
  // =====================================================

  /**
   * Abre el formulario para crear una nueva especificación
   */
  const handleOpenCreateForm = () => {
    setDialogState((prev) => ({
      ...prev,
      form: { open: true, id: null },
    }));
  };

  /**
   * Abre el formulario para editar una especificación
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
              <BuildIcon sx={{ mr: 0.5 }} fontSize="small" />
              Dashboard
            </Link>
            <Typography color="text.primary">Llantas</Typography>
            <Typography color="text.primary">Especificaciones Técnicas</Typography>
          </Breadcrumbs>

          {/* Título */}
          <Typography variant="h4" component="h1" gutterBottom fontWeight="bold">
            Gestión de Especificaciones Técnicas
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Administre las fichas técnicas de llantas con información de rendimiento,
            profundidades, proveedores y especificaciones comerciales.
          </Typography>
        </Box>

        {/* Lista de Especificaciones */}
        <TireSpecificationList
          onView={handleOpenDetailDialog}
          onEdit={handleOpenEditForm}
          onCreate={handleOpenCreateForm}
        />

        {/* Diálogo de Detalle */}
        <TireSpecificationDetailDialog
          open={dialogState.detail.open}
          specificationId={dialogState.detail.id}
          onClose={handleCloseDetailDialog}
          onEdit={handleEditFromDetail}
        />

        {/* Formulario de Creación/Edición */}
        <TireSpecificationForm
          open={dialogState.form.open}
          specificationId={dialogState.form.id}
          onClose={handleCloseForm}
          onSuccess={handleFormSuccess}
        />
      </Container>
    </Box>
  );
}
