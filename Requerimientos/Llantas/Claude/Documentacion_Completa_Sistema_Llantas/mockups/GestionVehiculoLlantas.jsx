import React, { useState } from 'react';
import {
  Box,
  Card,
  CardContent,
  Grid,
  Typography,
  Button,
  Chip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Tooltip,
  IconButton,
  Alert,
  Paper,
  Divider,
} from '@mui/material';
import {
  DirectionsCar as CarIcon,
  TireRepair as TireIcon,
  Add as AddIcon,
  Remove as RemoveIcon,
  History as HistoryIcon,
  Assessment as AssessmentIcon,
  Warning as WarningIcon,
} from '@mui/icons-material';

/**
 * Gestión de Vehículos - Vista de Llantas
 * 
 * Interfaz visual para gestionar las llantas de un vehículo específico
 * Muestra el esquema de posiciones con estado de cada llanta
 */
const GestionVehiculoLlantas = () => {
  const [selectedVehiculo, setSelectedVehiculo] = useState('XYZ123');
  const [dialogMontaje, setDialogMontaje] = useState(false);
  const [dialogDesmontaje, setDialogDesmontaje] = useState(false);
  const [selectedPosicion, setSelectedPosicion] = useState(null);

  // Datos simulados del vehículo (en producción viene de la API)
  const vehiculoData = {
    placa: 'XYZ123',
    clase: 'Tractocamión',
    marca: 'Freightliner',
    modelo: 2020,
    kilometrajeActual: 256430,
    estado: 'ACTIVO',
    operando: true,
    configuracionLlantas: {
      tipo: 'TRACTO_10_LLANTAS',
      esquema: [
        // Direccionales
        { posicion: 1, tipo: 'DIRECCIONAL', lado: 'IZQUIERDO' },
        { posicion: 2, tipo: 'DIRECCIONAL', lado: 'DERECHO' },
        // Tracción eje 1
        { posicion: 3, tipo: 'TRACCION', lado: 'IZQUIERDO_INTERNO' },
        { posicion: 4, tipo: 'TRACCION', lado: 'IZQUIERDO_EXTERNO' },
        { posicion: 5, tipo: 'TRACCION', lado: 'DERECHO_INTERNO' },
        { posicion: 6, tipo: 'TRACCION', lado: 'DERECHO_EXTERNO' },
        // Tracción eje 2
        { posicion: 7, tipo: 'TRACCION', lado: 'IZQUIERDO_INTERNO' },
        { posicion: 8, tipo: 'TRACCION', lado: 'IZQUIERDO_EXTERNO' },
        { posicion: 9, tipo: 'TRACCION', lado: 'DERECHO_INTERNO' },
        { posicion: 10, tipo: 'TRACCION', lado: 'DERECHO_EXTERNO' },
      ],
    },
    llantasActuales: [
      {
        posicion: 1,
        numeroLlanta: 'LL-00543',
        grupo: '001',
        marca: 'Michelin',
        dimension: '295/80R22.5',
        kmInstalacion: 245000,
        kmRecorridos: 11430,
        profundidadActual: 8.5,
        profundidadInicial: 15.2,
        desgaste: 44.1,
        estado: 'BUENO',
        ultimoMuestreo: '2026-01-15',
      },
      {
        posicion: 2,
        numeroLlanta: 'LL-00892',
        grupo: '000',
        marca: 'Bridgestone',
        dimension: '295/80R22.5',
        kmInstalacion: 240000,
        kmRecorridos: 16430,
        profundidadActual: 2.1,
        profundidadInicial: 15.8,
        desgaste: 86.7,
        estado: 'CRITICO',
        alertas: ['DESGASTE_CRITICO'],
        ultimoMuestreo: '2026-01-18',
      },
      {
        posicion: 3,
        numeroLlanta: 'LL-01234',
        grupo: '002',
        marca: 'Goodyear',
        dimension: '11R22.5',
        kmInstalacion: 248000,
        kmRecorridos: 8430,
        profundidadActual: 11.2,
        profundidadInicial: 14.5,
        desgaste: 22.8,
        estado: 'EXCELENTE',
        ultimoMuestreo: '2026-01-16',
      },
      // Posiciones 4-10 con datos similares...
      { posicion: 4, numeroLlanta: 'LL-00765', grupo: '000', marca: 'Michelin', dimension: '11R22.5', kmInstalacion: 250000, kmRecorridos: 6430, profundidadActual: 12.8, profundidadInicial: 15.0, desgaste: 14.7, estado: 'EXCELENTE', ultimoMuestreo: '2026-01-17' },
      { posicion: 5, numeroLlanta: 'LL-00998', grupo: '001', marca: 'Continental', dimension: '11R22.5', kmInstalacion: 242000, kmRecorridos: 14430, profundidadActual: 6.2, profundidadInicial: 14.8, desgaste: 58.1, estado: 'REGULAR', ultimoMuestreo: '2026-01-14' },
      { posicion: 6, numeroLlanta: 'LL-01156', grupo: '000', marca: 'Bridgestone', dimension: '11R22.5', kmInstalacion: 251000, kmRecorridos: 5430, profundidadActual: 13.1, profundidadInicial: 15.2, desgaste: 13.8, estado: 'EXCELENTE', ultimoMuestreo: '2026-01-19' },
      { posicion: 7, numeroLlanta: 'LL-00654', grupo: '002', marca: 'Goodyear', dimension: '11R22.5', kmInstalacion: 246000, kmRecorridos: 10430, profundidadActual: 9.8, profundidadInicial: 14.5, desgaste: 32.4, estado: 'BUENO', ultimoMuestreo: '2026-01-15' },
      { posicion: 8, numeroLlanta: 'LL-01045', grupo: '001', marca: 'Michelin', dimension: '11R22.5', kmInstalacion: 249000, kmRecorridos: 7430, profundidadActual: 10.5, profundidadInicial: 15.0, desgaste: 30.0, estado: 'BUENO', ultimoMuestreo: '2026-01-16' },
      { posicion: 9, numeroLlanta: 'LL-00887', grupo: '000', marca: 'Continental', dimension: '11R22.5', kmInstalacion: 253000, kmRecorridos: 3430, profundidadActual: 14.2, profundidadInicial: 15.5, desgaste: 8.4, estado: 'EXCELENTE', ultimoMuestreo: '2026-01-20' },
      { posicion: 10, numeroLlanta: 'LL-01123', grupo: '001', marca: 'Bridgestone', dimension: '11R22.5', kmInstalacion: 247000, kmRecorridos: 9430, profundidadActual: 8.9, profundidadInicial: 14.8, desgaste: 39.9, estado: 'BUENO', ultimoMuestreo: '2026-01-17' },
    ],
  };

  const getEstadoColor = (estado) => {
    const colores = {
      EXCELENTE: '#4caf50',
      BUENO: '#8bc34a',
      REGULAR: '#ff9800',
      CRITICO: '#f44336',
    };
    return colores[estado] || '#9e9e9e';
  };

  const getEstadoTexto = (estado) => {
    const textos = {
      EXCELENTE: '✓ Excelente',
      BUENO: '✓ Bueno',
      REGULAR: '⚠ Regular',
      CRITICO: '⚠ Crítico',
    };
    return textos[estado] || 'Sin datos';
  };

  const LlantaCard = ({ llanta, posicion }) => {
    const ocupada = llanta !== undefined;
    const config = vehiculoData.configuracionLlantas.esquema.find(e => e.posicion === posicion);

    return (
      <Card
        elevation={ocupada ? 3 : 1}
        sx={{
          height: '100%',
          border: ocupada ? `3px solid ${getEstadoColor(llanta?.estado)}` : '2px dashed #ccc',
          cursor: 'pointer',
          transition: 'all 0.3s',
          '&:hover': {
            transform: ocupada ? 'scale(1.05)' : 'scale(1.02)',
            boxShadow: ocupada ? 6 : 3,
          },
        }}
        onClick={() => {
          setSelectedPosicion(posicion);
          if (ocupada) {
            setDialogDesmontaje(true);
          } else {
            setDialogMontaje(true);
          }
        }}
      >
        <CardContent sx={{ p: 2 }}>
          {/* Header */}
          <Box display="flex" justifyContent="space-between" alignItems="center" mb={1}>
            <Typography variant="caption" color="textSecondary" fontWeight="bold">
              POS {posicion} - {config?.tipo}
            </Typography>
            {ocupada && llanta.alertas && llanta.alertas.length > 0 && (
              <Tooltip title="Tiene alertas críticas">
                <WarningIcon fontSize="small" color="error" />
              </Tooltip>
            )}
          </Box>

          {ocupada ? (
            <>
              {/* Icono de Llanta */}
              <Box textAlign="center" my={1}>
                <TireIcon
                  sx={{
                    fontSize: 60,
                    color: getEstadoColor(llanta.estado),
                  }}
                />
              </Box>

              {/* Número de Llanta */}
              <Typography variant="h6" align="center" fontWeight="bold">
                {llanta.numeroLlanta}
              </Typography>
              <Typography variant="caption" align="center" display="block" color="textSecondary" mb={1}>
                Grupo: {llanta.grupo} | {llanta.marca}
              </Typography>

              <Divider sx={{ my: 1 }} />

              {/* Métricas */}
              <Box sx={{ fontSize: '0.75rem' }}>
                <Box display="flex" justifyContent="space-between" mb={0.5}>
                  <Typography variant="caption" color="textSecondary">Profundidad:</Typography>
                  <Typography variant="caption" fontWeight="bold">
                    {llanta.profundidadActual.toFixed(1)} mm
                  </Typography>
                </Box>
                <Box display="flex" justifyContent="space-between" mb={0.5}>
                  <Typography variant="caption" color="textSecondary">Desgaste:</Typography>
                  <Typography
                    variant="caption"
                    fontWeight="bold"
                    color={llanta.desgaste > 80 ? 'error' : llanta.desgaste > 60 ? 'warning.main' : 'success.main'}
                  >
                    {llanta.desgaste.toFixed(1)}%
                  </Typography>
                </Box>
                <Box display="flex" justifyContent="space-between" mb={0.5}>
                  <Typography variant="caption" color="textSecondary">KMs recorridos:</Typography>
                  <Typography variant="caption" fontWeight="bold">
                    {llanta.kmRecorridos.toLocaleString()}
                  </Typography>
                </Box>
              </Box>

              {/* Estado */}
              <Chip
                label={getEstadoTexto(llanta.estado)}
                size="small"
                sx={{
                  width: '100%',
                  mt: 1,
                  fontWeight: 'bold',
                  backgroundColor: getEstadoColor(llanta.estado),
                  color: 'white',
                }}
              />
            </>
          ) : (
            <>
              {/* Posición Vacía */}
              <Box textAlign="center" py={3}>
                <AddIcon sx={{ fontSize: 60, color: '#ccc' }} />
                <Typography variant="body2" color="textSecondary" mt={1}>
                  Posición Vacía
                </Typography>
                <Typography variant="caption" color="textSecondary">
                  Click para montar llanta
                </Typography>
              </Box>
            </>
          )}
        </CardContent>
      </Card>
    );
  };

  // Organizar llantas por posiciones
  const getLlantaPorPosicion = (posicion) => {
    return vehiculoData.llantasActuales.find(l => l.posicion === posicion);
  };

  return (
    <Box sx={{ p: 3 }}>
      {/* Header */}
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Box>
          <Typography variant="h4" sx={{ fontWeight: 'bold', mb: 1 }}>
            Gestión de Llantas - Vehículo {vehiculoData.placa}
          </Typography>
          <Box display="flex" gap={1} alignItems="center">
            <Chip label={vehiculoData.clase} color="primary" size="small" />
            <Chip label={`${vehiculoData.marca} ${vehiculoData.modelo}`} variant="outlined" size="small" />
            <Chip label={`${vehiculoData.kilometrajeActual.toLocaleString()} km`} color="info" size="small" />
            <Chip
              label={vehiculoData.operando ? 'Operando' : 'Inactivo'}
              color={vehiculoData.operando ? 'success' : 'error'}
              size="small"
            />
          </Box>
        </Box>
        <Box display="flex" gap={2}>
          <Button variant="outlined" startIcon={<HistoryIcon />}>
            Ver Histórico
          </Button>
          <Button variant="outlined" startIcon={<AssessmentIcon />}>
            Reportes
          </Button>
        </Box>
      </Box>

      {/* Alertas */}
      {vehiculoData.llantasActuales.some(l => l.alertas && l.alertas.length > 0) && (
        <Alert severity="error" icon={<WarningIcon />} sx={{ mb: 3 }}>
          <strong>Este vehículo tiene llantas con alertas críticas.</strong> Revise las posiciones marcadas.
        </Alert>
      )}

      {/* Esquema Visual del Vehículo */}
      <Paper elevation={3} sx={{ p: 3, mb: 3 }}>
        <Typography variant="h6" sx={{ mb: 2, fontWeight: 'bold' }}>
          Esquema de Llantas - {vehiculoData.configuracionLlantas.tipo}
        </Typography>

        {/* Tractocamión: Esquema Visual */}
        <Box>
          {/* Direccionales */}
          <Typography variant="subtitle2" color="textSecondary" gutterBottom>
            Eje Direccional
          </Typography>
          <Grid container spacing={2} sx={{ mb: 3 }}>
            <Grid item xs={6}>
              <LlantaCard llanta={getLlantaPorPosicion(1)} posicion={1} />
            </Grid>
            <Grid item xs={6}>
              <LlantaCard llanta={getLlantaPorPosicion(2)} posicion={2} />
            </Grid>
          </Grid>

          {/* Eje Tracción 1 */}
          <Typography variant="subtitle2" color="textSecondary" gutterBottom>
            Eje Tracción 1
          </Typography>
          <Grid container spacing={2} sx={{ mb: 3 }}>
            <Grid item xs={3}>
              <LlantaCard llanta={getLlantaPorPosicion(3)} posicion={3} />
            </Grid>
            <Grid item xs={3}>
              <LlantaCard llanta={getLlantaPorPosicion(4)} posicion={4} />
            </Grid>
            <Grid item xs={3}>
              <LlantaCard llanta={getLlantaPorPosicion(5)} posicion={5} />
            </Grid>
            <Grid item xs={3}>
              <LlantaCard llanta={getLlantaPorPosicion(6)} posicion={6} />
            </Grid>
          </Grid>

          {/* Eje Tracción 2 */}
          <Typography variant="subtitle2" color="textSecondary" gutterBottom>
            Eje Tracción 2
          </Typography>
          <Grid container spacing={2}>
            <Grid item xs={3}>
              <LlantaCard llanta={getLlantaPorPosicion(7)} posicion={7} />
            </Grid>
            <Grid item xs={3}>
              <LlantaCard llanta={getLlantaPorPosicion(8)} posicion={8} />
            </Grid>
            <Grid item xs={3}>
              <LlantaCard llanta={getLlantaPorPosicion(9)} posicion={9} />
            </Grid>
            <Grid item xs={3}>
              <LlantaCard llanta={getLlantaPorPosicion(10)} posicion={10} />
            </Grid>
          </Grid>
        </Box>

        {/* Leyenda */}
        <Box mt={3} display="flex" gap={2} justifyContent="center">
          <Chip label="Excelente" size="small" sx={{ backgroundColor: '#4caf50', color: 'white' }} />
          <Chip label="Bueno" size="small" sx={{ backgroundColor: '#8bc34a', color: 'white' }} />
          <Chip label="Regular" size="small" sx={{ backgroundColor: '#ff9800', color: 'white' }} />
          <Chip label="Crítico" size="small" sx={{ backgroundColor: '#f44336', color: 'white' }} />
        </Box>
      </Paper>

      {/* Resumen Estadístico */}
      <Grid container spacing={3}>
        <Grid item xs={12} md={4}>
          <Card elevation={2}>
            <CardContent>
              <Typography variant="h6" gutterBottom>Resumen del Vehículo</Typography>
              <Box display="flex" justifyContent="space-between" mb={1}>
                <Typography variant="body2">Llantas instaladas:</Typography>
                <Typography variant="body2" fontWeight="bold">10/10</Typography>
              </Box>
              <Box display="flex" justifyContent="space-between" mb={1}>
                <Typography variant="body2">Profundidad promedio:</Typography>
                <Typography variant="body2" fontWeight="bold">9.8 mm</Typography>
              </Box>
              <Box display="flex" justifyContent="space-between" mb={1}>
                <Typography variant="body2">Desgaste promedio:</Typography>
                <Typography variant="body2" fontWeight="bold">35.1%</Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={4}>
          <Card elevation={2}>
            <CardContent>
              <Typography variant="h6" gutterBottom>Kilometraje</Typography>
              <Box display="flex" justifyContent="space-between" mb={1}>
                <Typography variant="body2">KM promedio por llanta:</Typography>
                <Typography variant="body2" fontWeight="bold">9,343 km</Typography>
              </Box>
              <Box display="flex" justifyContent="space-between" mb={1}>
                <Typography variant="body2">Llanta más reciente:</Typography>
                <Typography variant="body2" fontWeight="bold">3,430 km</Typography>
              </Box>
              <Box display="flex" justifyContent="space-between" mb={1}>
                <Typography variant="body2">Llanta más antigua:</Typography>
                <Typography variant="body2" fontWeight="bold">16,430 km</Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={4}>
          <Card elevation={2}>
            <CardContent>
              <Typography variant="h6" gutterBottom>Alertas</Typography>
              <Box display="flex" justifyContent="space-between" mb={1}>
                <Typography variant="body2">Llantas en estado crítico:</Typography>
                <Typography variant="body2" fontWeight="bold" color="error">1</Typography>
              </Box>
              <Box display="flex" justifyContent="space-between" mb={1}>
                <Typography variant="body2">Requieren muestreo:</Typography>
                <Typography variant="body2" fontWeight="bold" color="warning.main">0</Typography>
              </Box>
              <Box display="flex" justifyContent="space-between" mb={1}>
                <Typography variant="body2">Programar rotación:</Typography>
                <Typography variant="body2" fontWeight="bold" color="info.main">No</Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Dialog de Montaje */}
      <Dialog open={dialogMontaje} onClose={() => setDialogMontaje(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Montar Llanta en Posición {selectedPosicion}</DialogTitle>
        <DialogContent>
          <Alert severity="info" sx={{ mb: 2 }}>
            Seleccione una llanta disponible del inventario
          </Alert>
          <FormControl fullWidth sx={{ mt: 2 }}>
            <InputLabel>Llanta del Inventario</InputLabel>
            <Select label="Llanta del Inventario">
              <MenuItem value="LL-02345">LL-02345 - Michelin 295/80R22.5 (Nueva)</MenuItem>
              <MenuItem value="LL-02346">LL-02346 - Bridgestone 11R22.5 (Reencauche 1)</MenuItem>
              <MenuItem value="LL-02347">LL-02347 - Goodyear 295/80R22.5 (Nueva)</MenuItem>
            </Select>
          </FormControl>
          <TextField
            fullWidth
            label="Kilometraje de Instalación"
            type="number"
            defaultValue={vehiculoData.kilometrajeActual}
            sx={{ mt: 2 }}
          />
          <TextField
            fullWidth
            label="Fecha de Instalación"
            type="date"
            defaultValue={new Date().toISOString().split('T')[0]}
            InputLabelProps={{ shrink: true }}
            sx={{ mt: 2 }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDialogMontaje(false)}>Cancelar</Button>
          <Button variant="contained" onClick={() => setDialogMontaje(false)}>
            Montar Llanta
          </Button>
        </DialogActions>
      </Dialog>

      {/* Dialog de Desmontaje */}
      <Dialog open={dialogDesmontaje} onClose={() => setDialogDesmontaje(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Desmontar Llanta de Posición {selectedPosicion}</DialogTitle>
        <DialogContent>
          {selectedPosicion && getLlantaPorPosicion(selectedPosicion) && (
            <>
              <Alert severity="warning" sx={{ mb: 2 }}>
                ¿Está seguro que desea desmontar esta llanta?
              </Alert>
              <Box mb={2}>
                <Typography variant="subtitle2">Llanta: {getLlantaPorPosicion(selectedPosicion).numeroLlanta}</Typography>
                <Typography variant="body2" color="textSecondary">
                  Kilómetros recorridos: {getLlantaPorPosicion(selectedPosicion).kmRecorridos.toLocaleString()} km
                </Typography>
              </Box>
              <TextField
                fullWidth
                label="Kilometraje de Remoción"
                type="number"
                defaultValue={vehiculoData.kilometrajeActual}
                sx={{ mt: 2 }}
              />
              <FormControl fullWidth sx={{ mt: 2 }}>
                <InputLabel>Motivo de Desmontaje</InputLabel>
                <Select label="Motivo de Desmontaje">
                  <MenuItem value="DESGASTE">Desgaste normal</MenuItem>
                  <MenuItem value="ROTACION">Rotación preventiva</MenuItem>
                  <MenuItem value="DANO">Daño en banda</MenuItem>
                  <MenuItem value="FIN_VIDA">Fin de vida útil</MenuItem>
                </Select>
              </FormControl>
            </>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDialogDesmontaje(false)}>Cancelar</Button>
          <Button variant="contained" color="error" onClick={() => setDialogDesmontaje(false)}>
            Desmontar
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default GestionVehiculoLlantas;
