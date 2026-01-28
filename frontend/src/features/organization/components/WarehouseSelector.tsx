/**
 * Componente selector de almacenes
 * Permite seleccionar un almacén filtrado por oficina
 */

import React from 'react';
import {
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  FormHelperText,
  CircularProgress,
} from '@mui/material';
import { useWarehousesByOffice, useWarehouses } from '../hooks/useWarehouses';
import type { Warehouse } from '../types/organization.types';

interface WarehouseSelectorProps {
  value: string;
  onChange: (warehouseId: string) => void;
  officeId?: string; // Si se proporciona, filtra por oficina
  label?: string;
  required?: boolean;
  disabled?: boolean;
  error?: boolean;
  helperText?: string;
  fullWidth?: boolean;
  showInactive?: boolean;
}

/**
 * Selector de almacenes que respeta RLS y el contexto multi-sede
 */
export const WarehouseSelector: React.FC<WarehouseSelectorProps> = ({
  value,
  onChange,
  officeId,
  label = 'Almacén',
  required = false,
  disabled = false,
  error = false,
  helperText,
  fullWidth = true,
  showInactive = false,
}) => {
  // Si hay officeId, filtrar por oficina; sino, obtener todos (RLS aplicará filtro)
  const {
    data: warehousesByOffice,
    isLoading: isLoadingByOffice,
    error: errorByOffice,
  } = useWarehousesByOffice(officeId || '', { enabled: !!officeId });

  const {
    data: allWarehouses,
    isLoading: isLoadingAll,
    error: errorAll,
  } = useWarehouses({ isActive: !showInactive });

  // Seleccionar el dataset correcto según si hay officeId
  const warehouses = officeId ? warehousesByOffice : allWarehouses;
  const isLoading = officeId ? isLoadingByOffice : isLoadingAll;
  const hasError = officeId ? errorByOffice : errorAll;

  // Si no hay oficina seleccionada aún, mostrar mensaje
  if (officeId === undefined || officeId === '') {
    return (
      <FormControl fullWidth={fullWidth} required={required} disabled>
        <InputLabel>{label}</InputLabel>
        <Select value="" displayEmpty>
          <MenuItem value="" disabled>
            Primero selecciona una oficina
          </MenuItem>
        </Select>
      </FormControl>
    );
  }

  if (isLoading) {
    return (
      <FormControl fullWidth={fullWidth} required={required} disabled>
        <InputLabel>{label}</InputLabel>
        <Select value="" displayEmpty>
          <MenuItem value="">
            <CircularProgress size={20} /> Cargando almacenes...
          </MenuItem>
        </Select>
      </FormControl>
    );
  }

  if (hasError) {
    return (
      <FormControl fullWidth={fullWidth} required={required} disabled error>
        <InputLabel>{label}</InputLabel>
        <Select value="" displayEmpty>
          <MenuItem value="" disabled>
            Error al cargar almacenes
          </MenuItem>
        </Select>
        <FormHelperText>No se pudieron cargar los almacenes</FormHelperText>
      </FormControl>
    );
  }

  return (
    <FormControl fullWidth={fullWidth} required={required} disabled={disabled} error={error}>
      <InputLabel id="warehouse-selector-label">{label}</InputLabel>
      <Select
        labelId="warehouse-selector-label"
        id="warehouse-selector"
        value={value}
        onChange={(e) => onChange(e.target.value)}
        label={label}
        disabled={disabled}
      >
        {!warehouses || warehouses.length === 0 ? (
          <MenuItem value="" disabled>
            No hay almacenes disponibles en esta oficina
          </MenuItem>
        ) : (
          warehouses.map((warehouse: Warehouse) => (
            <MenuItem key={warehouse.id} value={warehouse.id}>
              {warehouse.code} - {warehouse.name}
              {!warehouse.isActive && ' (Inactivo)'}
            </MenuItem>
          ))
        )}
      </Select>
      {helperText && <FormHelperText>{helperText}</FormHelperText>}
    </FormControl>
  );
};

export default WarehouseSelector;
