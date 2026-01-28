/**
 * Página principal para la gestión de Oficinas/Sedes
 */

import { useState } from 'react';
import { Box, Container, Typography, Breadcrumbs, Link } from '@mui/material';
import { NavigateNext as NavigateNextIcon, Business as BusinessIcon } from '@mui/icons-material';
import { Link as RouterLink } from 'react-router-dom';
import OfficeList from '../components/OfficeList';
import OfficeForm from '../components/OfficeForm';
import OfficeDetailDialog from '../components/OfficeDetailDialog';

/**
 * Tipos para el estado de los diálogos
 */
type DialogState = {
  detail: { open: boolean; id: string | null };
  form: { open: boolean; id: string | null };
};

/**
 * Componente OfficePage
 */
export default function OfficePage() {
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
   * Abre el formulario para crear una nueva oficina
   */
  const handleOpenCreateForm = () => {
    setDialogState((prev) => ({
      ...prev,
      form: { open: true, id: null },
    }));
  };

  /**
   * Abre el formulario para editar una oficina
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
              <BusinessIcon sx={{ mr: 0.5 }} fontSize="small" />
              Dashboard
            </Link>
            <Typography color="text.primary">Organización</Typography>
            <Typography color="text.primary">Oficinas</Typography>
          </Breadcrumbs>

          {/* Título */}
          <Typography variant="h4" component="h1" gutterBottom fontWeight="bold">
            Gestión de Oficinas
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Administre las sedes de la empresa. Cada oficina puede tener múltiples almacenes y proveedores asignados.
          </Typography>
        </Box>

        {/* Lista de Oficinas */}
        <OfficeList
          onView={handleOpenDetailDialog}
          onEdit={handleOpenEditForm}
          onCreate={handleOpenCreateForm}
        />

        {/* Diálogo de Detalle */}
        <OfficeDetailDialog
          open={dialogState.detail.open}
          officeId={dialogState.detail.id}
          onClose={handleCloseDetailDialog}
          onEdit={handleEditFromDetail}
        />

        {/* Formulario de Creación/Edición */}
        <OfficeForm
          open={dialogState.form.open}
          officeId={dialogState.form.id}
          onClose={handleCloseForm}
          onSuccess={handleFormSuccess}
        />
      </Container>
    </Box>
  );
}
