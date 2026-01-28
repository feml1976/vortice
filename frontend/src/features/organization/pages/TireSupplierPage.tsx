/**
 * Página principal para la gestión de Proveedores de Llantas
 */

import { useState } from 'react';
import { Box, Container, Typography, Breadcrumbs, Link } from '@mui/material';
import { NavigateNext as NavigateNextIcon, Store as StoreIcon } from '@mui/icons-material';
import { Link as RouterLink } from 'react-router-dom';
import TireSupplierList from '../components/TireSupplierList';
import TireSupplierForm from '../components/TireSupplierForm';

/**
 * Tipos para el estado de los diálogos
 */
type DialogState = {
  form: { open: boolean; id: string | null };
};

/**
 * Componente TireSupplierPage
 */
export default function TireSupplierPage() {
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
   * Abre el formulario para crear un nuevo proveedor
   */
  const handleOpenCreateForm = () => {
    setDialogState((prev) => ({
      ...prev,
      form: { open: true, id: null },
    }));
  };

  /**
   * Abre el formulario para editar un proveedor
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
              <StoreIcon sx={{ mr: 0.5 }} fontSize="small" />
              Dashboard
            </Link>
            <Typography color="text.primary">Organización</Typography>
            <Typography color="text.primary">Proveedores de Llantas</Typography>
          </Breadcrumbs>

          {/* Título */}
          <Typography variant="h4" component="h1" gutterBottom fontWeight="bold">
            Gestión de Proveedores de Llantas
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Administre los proveedores de llantas de su oficina. Registre información de contacto y condiciones comerciales.
          </Typography>
        </Box>

        {/* Lista de Proveedores */}
        <TireSupplierList
          onEdit={handleOpenEditForm}
          onCreate={handleOpenCreateForm}
        />

        {/* Formulario de Creación/Edición */}
        <TireSupplierForm
          open={dialogState.form.open}
          supplierId={dialogState.form.id}
          onClose={handleCloseForm}
          onSuccess={handleFormSuccess}
        />
      </Container>
    </Box>
  );
}
