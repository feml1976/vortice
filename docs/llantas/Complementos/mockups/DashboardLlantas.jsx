import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Grid,
  Typography,
  LinearProgress,
  Chip,
  Alert,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  IconButton,
  Tooltip,
} from '@mui/material';
import {
  TrendingUp as TrendingUpIcon,
  TrendingDown as TrendingDownIcon,
  Warning as WarningIcon,
  CheckCircle as CheckCircleIcon,
  DirectionsCar as CarIcon,
  Build as BuildIcon,
  Notifications as NotificationsIcon,
} from '@mui/icons-material';
import { LineChart, Line, BarChart, Bar, PieChart, Pie, Cell, XAxis, YAxis, CartesianGrid, Tooltip as ChartTooltip, Legend, ResponsiveContainer } from 'recharts';

/**
 * Dashboard Principal - Sistema de Gestión de Llantas
 * 
 * Muestra los KPIs principales del sistema con visualizaciones en tiempo real
 */
const DashboardLlantas = () => {
  const [loading, setLoading] = useState(true);

  // Datos simulados (en producción vendrían de la API)
  const kpisData = {
    totalLlantas: 1248,
    llantasActivas: 856,
    llantasInventario: 234,
    llantasIntermedio: 98,
    llantasRetiradas: 60,
    alertasCriticas: 12,
    alertasMedias: 28,
    costoPorKmPromedio: 0.0245,
    vidaUtilPromedio: 127350,
    muestreosPendientes: 45,
  };

  const rendimientoMarcas = [
    { marca: 'Michelin', kmsPromedio: 145000, costoKm: 0.0198, cantidad: 320 },
    { marca: 'Bridgestone', kmsPromedio: 138000, costoKm: 0.0212, cantidad: 285 },
    { marca: 'Goodyear', kmsPromedio: 125000, costoKm: 0.0235, cantidad: 195 },
    { marca: 'Continental', kmsPromedio: 133000, costoKm: 0.0221, cantidad: 156 },
  ];

  const consumoMensual = [
    { mes: 'Ago', consumo: 45, presupuesto: 50 },
    { mes: 'Sep', consumo: 52, presupuesto: 50 },
    { mes: 'Oct', consumo: 38, presupuesto: 50 },
    { mes: 'Nov', consumo: 48, presupuesto: 50 },
    { mes: 'Dic', consumo: 41, presupuesto: 50 },
    { mes: 'Ene', consumo: 35, presupuesto: 50 },
  ];

  const distribucionEstados = [
    { name: 'Activas', value: kpisData.llantasActivas, color: '#4caf50' },
    { name: 'Inventario', value: kpisData.llantasInventario, color: '#2196f3' },
    { name: 'Intermedio', value: kpisData.llantasIntermedio, color: '#ff9800' },
    { name: 'Retiradas', value: kpisData.llantasRetiradas, color: '#f44336' },
  ];

  const alertasCriticas = [
    { id: 1, vehiculo: 'XYZ123', llanta: 'LL-00543', posicion: 3, tipo: 'Profundidad crítica', valor: '1.2mm', prioridad: 'ALTA' },
    { id: 2, vehiculo: 'ABC456', llanta: 'LL-00892', posicion: 7, tipo: 'Desgaste irregular', valor: '45% diferencia', prioridad: 'ALTA' },
    { id: 3, vehiculo: 'DEF789', llanta: 'LL-01234', posicion: 2, tipo: 'Presión baja', valor: '65 PSI', prioridad: 'MEDIA' },
    { id: 4, vehiculo: 'GHI012', llanta: 'LL-00765', posicion: 5, tipo: 'Sin muestreo', valor: '45 días', prioridad: 'MEDIA' },
  ];

  useEffect(() => {
    // Simular carga de datos
    setTimeout(() => setLoading(false), 1000);
  }, []);

  const KPICard = ({ title, value, subtitle, icon, trend, trendValue, color = 'primary' }) => (
    <Card elevation={3}>
      <CardContent>
        <Box display="flex" justifyContent="space-between" alignItems="flex-start">
          <Box>
            <Typography color="textSecondary" gutterBottom variant="overline">
              {title}
            </Typography>
            <Typography variant="h3" component="div" sx={{ fontWeight: 'bold', mb: 1 }}>
              {value.toLocaleString()}
            </Typography>
            <Typography variant="body2" color="textSecondary">
              {subtitle}
            </Typography>
            {trend && (
              <Box display="flex" alignItems="center" mt={1}>
                {trend === 'up' ? (
                  <TrendingUpIcon fontSize="small" color="success" />
                ) : (
                  <TrendingDownIcon fontSize="small" color="error" />
                )}
                <Typography variant="caption" sx={{ ml: 0.5 }}>
                  {trendValue} vs mes anterior
                </Typography>
              </Box>
            )}
          </Box>
          <Box
            sx={{
              backgroundColor: `${color}.light`,
              borderRadius: 2,
              p: 1.5,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
            }}
          >
            {icon}
          </Box>
        </Box>
      </CardContent>
    </Card>
  );

  if (loading) {
    return (
      <Box sx={{ width: '100%', mt: 2 }}>
        <LinearProgress />
        <Typography align="center" sx={{ mt: 2 }}>
          Cargando dashboard...
        </Typography>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      {/* Header */}
      <Box sx={{ mb: 3 }}>
        <Typography variant="h4" sx={{ fontWeight: 'bold', mb: 1 }}>
          Dashboard de Gestión de Llantas
        </Typography>
        <Typography variant="body1" color="textSecondary">
          Visión general del estado de la flota • Última actualización: Hoy, 14:30
        </Typography>
      </Box>

      {/* Alertas Críticas Banner */}
      {kpisData.alertasCriticas > 0 && (
        <Alert severity="error" icon={<WarningIcon />} sx={{ mb: 3 }}>
          <strong>{kpisData.alertasCriticas} llantas requieren atención inmediata</strong> • 
          {kpisData.alertasMedias} alertas de prioridad media
        </Alert>
      )}

      {/* KPIs Principales */}
      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid item xs={12} sm={6} md={3}>
          <KPICard
            title="Total Llantas"
            value={kpisData.totalLlantas}
            subtitle="En el sistema"
            icon={<BuildIcon sx={{ fontSize: 40, color: 'primary.main' }} />}
            trend="up"
            trendValue="+3.2%"
            color="primary"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <KPICard
            title="Llantas Activas"
            value={kpisData.llantasActivas}
            subtitle="Montadas en vehículos"
            icon={<CarIcon sx={{ fontSize: 40, color: 'success.main' }} />}
            trend="up"
            trendValue="+1.8%"
            color="success"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <KPICard
            title="Costo/Km Promedio"
            value={`$${kpisData.costoPorKmPromedio.toFixed(4)}`}
            subtitle="USD por kilómetro"
            icon={<TrendingDownIcon sx={{ fontSize: 40, color: 'info.main' }} />}
            trend="down"
            trendValue="-0.5%"
            color="info"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <KPICard
            title="Vida Útil Promedio"
            value={kpisData.vidaUtilPromedio.toLocaleString()}
            subtitle="Kilómetros"
            icon={<CheckCircleIcon sx={{ fontSize: 40, color: 'warning.main' }} />}
            trend="up"
            trendValue="+2.1%"
            color="warning"
          />
        </Grid>
      </Grid>

      {/* Gráficos y Visualizaciones */}
      <Grid container spacing={3} sx={{ mb: 3 }}>
        {/* Consumo Mensual */}
        <Grid item xs={12} md={6}>
          <Card elevation={3}>
            <CardContent>
              <Typography variant="h6" sx={{ mb: 2, fontWeight: 'bold' }}>
                Consumo Mensual de Llantas
              </Typography>
              <ResponsiveContainer width="100%" height={300}>
                <BarChart data={consumoMensual}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="mes" />
                  <YAxis />
                  <ChartTooltip />
                  <Legend />
                  <Bar dataKey="consumo" fill="#2196f3" name="Consumo Real" />
                  <Bar dataKey="presupuesto" fill="#90caf9" name="Presupuesto" />
                </BarChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </Grid>

        {/* Distribución por Estado */}
        <Grid item xs={12} md={6}>
          <Card elevation={3}>
            <CardContent>
              <Typography variant="h6" sx={{ mb: 2, fontWeight: 'bold' }}>
                Distribución por Estado
              </Typography>
              <ResponsiveContainer width="100%" height={300}>
                <PieChart>
                  <Pie
                    data={distribucionEstados}
                    cx="50%"
                    cy="50%"
                    labelLine={false}
                    label={({ name, percent }) => `${name}: ${(percent * 100).toFixed(0)}%`}
                    outerRadius={100}
                    fill="#8884d8"
                    dataKey="value"
                  >
                    {distribucionEstados.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={entry.color} />
                    ))}
                  </Pie>
                  <ChartTooltip />
                </PieChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Rendimiento por Marca */}
      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid item xs={12}>
          <Card elevation={3}>
            <CardContent>
              <Typography variant="h6" sx={{ mb: 2, fontWeight: 'bold' }}>
                Rendimiento por Marca de Llantas
              </Typography>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell><strong>Marca</strong></TableCell>
                    <TableCell align="right"><strong>Km Promedio</strong></TableCell>
                    <TableCell align="right"><strong>Costo/Km (USD)</strong></TableCell>
                    <TableCell align="right"><strong>Cantidad</strong></TableCell>
                    <TableCell align="center"><strong>Eficiencia</strong></TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {rendimientoMarcas.map((marca) => {
                    const eficiencia = ((marca.kmsPromedio / 150000) * 100).toFixed(0);
                    return (
                      <TableRow key={marca.marca} hover>
                        <TableCell>{marca.marca}</TableCell>
                        <TableCell align="right">{marca.kmsPromedio.toLocaleString()}</TableCell>
                        <TableCell align="right">${marca.costoKm.toFixed(4)}</TableCell>
                        <TableCell align="right">{marca.cantidad}</TableCell>
                        <TableCell align="center">
                          <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                            <Box sx={{ width: '100%', maxWidth: 150, mr: 1 }}>
                              <LinearProgress
                                variant="determinate"
                                value={parseInt(eficiencia)}
                                sx={{
                                  height: 8,
                                  borderRadius: 5,
                                  backgroundColor: 'grey.200',
                                  '& .MuiLinearProgress-bar': {
                                    backgroundColor: eficiencia >= 90 ? 'success.main' : eficiencia >= 75 ? 'warning.main' : 'error.main',
                                  },
                                }}
                              />
                            </Box>
                            <Typography variant="body2">{eficiencia}%</Typography>
                          </Box>
                        </TableCell>
                      </TableRow>
                    );
                  })}
                </TableBody>
              </Table>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Alertas Críticas */}
      <Grid container spacing={3}>
        <Grid item xs={12}>
          <Card elevation={3}>
            <CardContent>
              <Box display="flex" justifyContent="space-between" alignItems="center" sx={{ mb: 2 }}>
                <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                  Alertas Críticas y Muestreos Pendientes
                </Typography>
                <Chip
                  label={`${kpisData.alertasCriticas + kpisData.alertasMedias} Total`}
                  color="error"
                  icon={<NotificationsIcon />}
                />
              </Box>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell><strong>Vehículo</strong></TableCell>
                    <TableCell><strong>Llanta</strong></TableCell>
                    <TableCell align="center"><strong>Posición</strong></TableCell>
                    <TableCell><strong>Tipo de Alerta</strong></TableCell>
                    <TableCell><strong>Valor</strong></TableCell>
                    <TableCell align="center"><strong>Prioridad</strong></TableCell>
                    <TableCell align="center"><strong>Acción</strong></TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {alertasCriticas.map((alerta) => (
                    <TableRow key={alerta.id} hover>
                      <TableCell>{alerta.vehiculo}</TableCell>
                      <TableCell>{alerta.llanta}</TableCell>
                      <TableCell align="center">{alerta.posicion}</TableCell>
                      <TableCell>{alerta.tipo}</TableCell>
                      <TableCell>
                        <Chip
                          label={alerta.valor}
                          size="small"
                          color={alerta.prioridad === 'ALTA' ? 'error' : 'warning'}
                        />
                      </TableCell>
                      <TableCell align="center">
                        <Chip
                          label={alerta.prioridad}
                          size="small"
                          color={alerta.prioridad === 'ALTA' ? 'error' : 'warning'}
                        />
                      </TableCell>
                      <TableCell align="center">
                        <Tooltip title="Ver detalle">
                          <IconButton size="small" color="primary">
                            <WarningIcon />
                          </IconButton>
                        </Tooltip>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

export default DashboardLlantas;
