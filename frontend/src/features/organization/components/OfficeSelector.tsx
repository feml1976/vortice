/**
 * Componente selector de oficinas
 * Permite seleccionar una oficina del listado accesible por el usuario
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
import { useOffices } from '../hooks/useOffices';
import { useOfficeContext } from '../context/OfficeContext';
import type { Office } from '../types/organization.types';

interface OfficeSelectorProps {
  value: string;
  onChange: (officeId: string) => void;
  label?: string;
  required?: boolean;
  disabled?: boolean;
  error?: boolean;
  helperText?: string;
  fullWidth?: boolean;
  showInactive?: boolean;
}

/**
 * Selector de oficinas que respeta el contexto multi-sede
 * Admin nacional ve todas las oficinas, usuarios normales solo ven la suya
 */
export const OfficeSelector: React.FC<OfficeSelectorProps> = ({
  value,
  onChange,
  label = 'Oficina',
  required = false,
  disabled = false,
  error = false,
  helperText,
  fullWidth = true,
  showInactive = false,
}) => {
  const { isNationalAdmin, currentOffice } = useOfficeContext();
  const { data: offices, isLoading } = useOffices({ isActive: !showInactive });

  // Filtrar oficinas según permisos
  const availableOffices = React.useMemo(() => {
    if (!offices) return [];

    // Admin nacional ve todas las oficinas
    if (isNationalAdmin) {
      return offices;
    }

    // Usuarios normales solo ven su oficina
    return currentOffice ? [currentOffice] : [];
  }, [offices, isNationalAdmin, currentOffice]);

  // Si no es admin nacional y hay oficina, pre-seleccionar automáticamente
  React.useEffect(() => {
    if (!isNationalAdmin && currentOffice && !value) {
      onChange(currentOffice.id);
    }
  }, [isNationalAdmin, currentOffice, value, onChange]);

  if (isLoading) {
    return (
      <FormControl fullWidth={fullWidth} required={required} disabled>
        <InputLabel>{label}</InputLabel>
        <Select value="" displayEmpty>
          <MenuItem value="">
            <CircularProgress size={20} /> Cargando...
          </MenuItem>
        </Select>
      </FormControl>
    );
  }

  return (
    <FormControl fullWidth={fullWidth} required={required} disabled={disabled} error={error}>
      <InputLabel id="office-selector-label">{label}</InputLabel>
      <Select
        labelId="office-selector-label"
        id="office-selector"
        value={value}
        onChange={(e) => onChange(e.target.value)}
        label={label}
        disabled={disabled || (!isNationalAdmin && availableOffices.length === 1)}
      >
        {availableOffices.length === 0 && (
          <MenuItem value="" disabled>
            No hay oficinas disponibles
          </MenuItem>
        )}
        {availableOffices.map((office: Office) => (
          <MenuItem key={office.id} value={office.id}>
            {office.code} - {office.name} ({office.city})
            {!office.isActive && ' (Inactiva)'}
          </MenuItem>
        ))}
      </Select>
      {helperText && <FormHelperText>{helperText}</FormHelperText>}
      {!isNationalAdmin && availableOffices.length === 1 && (
        <FormHelperText>Tu oficina asignada</FormHelperText>
      )}
    </FormControl>
  );
};

export default OfficeSelector;
