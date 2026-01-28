/**
 * Context Provider para la oficina actual del usuario
 * Proporciona información sobre la oficina y permisos de acceso
 */

import React, { createContext, useContext, useMemo } from 'react';
import { useAuthStore } from '@/features/auth/store/authStore';
import { useOffice } from '../hooks/useOffices';
import type { OfficeContext as IOfficeContext } from '../types/organization.types';

const OfficeContext = createContext<IOfficeContext | undefined>(undefined);

interface OfficeProviderProps {
  children: React.ReactNode;
}

/**
 * Provider que proporciona información sobre la oficina actual del usuario
 * y sus permisos en el sistema multi-sede
 */
export const OfficeProvider: React.FC<OfficeProviderProps> = ({ children }) => {
  const user = useAuthStore((state) => state.user);

  // Cargar información de la oficina del usuario
  const { data: currentOffice, isLoading } = useOffice(user?.officeId || '', {
    enabled: !!user?.officeId,
  });

  // Verificar roles del usuario
  const isNationalAdmin = useMemo(() => {
    return user?.roles.includes('ADMIN_NATIONAL') || false;
  }, [user?.roles]);

  const isOfficeAdmin = useMemo(() => {
    return user?.roles.includes('ADMIN_OFFICE') || false;
  }, [user?.roles]);

  const isWarehouseManager = useMemo(() => {
    return user?.roles.includes('WAREHOUSE_MANAGER') || false;
  }, [user?.roles]);

  // Función para verificar si tiene acceso a una oficina
  const hasAccessToOffice = useMemo(() => {
    return (officeId: string): boolean => {
      // Admin nacional tiene acceso a todas las oficinas
      if (isNationalAdmin) {
        return true;
      }

      // Otros usuarios solo tienen acceso a su propia oficina
      return user?.officeId === officeId;
    };
  }, [isNationalAdmin, user?.officeId]);

  // Permisos CRUD
  const canManageOffices = useMemo(() => {
    return (): boolean => {
      // Solo admin nacional puede gestionar oficinas
      return isNationalAdmin;
    };
  }, [isNationalAdmin]);

  const canManageWarehouses = useMemo(() => {
    return (): boolean => {
      // Admin nacional y admin de oficina pueden gestionar almacenes
      return isNationalAdmin || isOfficeAdmin;
    };
  }, [isNationalAdmin, isOfficeAdmin]);

  const canManageLocations = useMemo(() => {
    return (): boolean => {
      // Admin nacional, admin de oficina y gerente de almacén pueden gestionar ubicaciones
      return isNationalAdmin || isOfficeAdmin || isWarehouseManager;
    };
  }, [isNationalAdmin, isOfficeAdmin, isWarehouseManager]);

  const canManageSuppliers = useMemo(() => {
    return (): boolean => {
      // Admin nacional y admin de oficina pueden gestionar proveedores
      return isNationalAdmin || isOfficeAdmin;
    };
  }, [isNationalAdmin, isOfficeAdmin]);

  const contextValue: IOfficeContext = useMemo(
    () => ({
      currentOffice: currentOffice || null,
      isNationalAdmin,
      isOfficeAdmin,
      isWarehouseManager,
      hasAccessToOffice,
      canManageOffices,
      canManageWarehouses,
      canManageLocations,
      canManageSuppliers,
    }),
    [
      currentOffice,
      isNationalAdmin,
      isOfficeAdmin,
      isWarehouseManager,
      hasAccessToOffice,
      canManageOffices,
      canManageWarehouses,
      canManageLocations,
      canManageSuppliers,
    ]
  );

  // Mostrar loading mientras carga la oficina
  if (user && isLoading) {
    return <div>Cargando información de oficina...</div>;
  }

  return <OfficeContext.Provider value={contextValue}>{children}</OfficeContext.Provider>;
};

/**
 * Hook para usar el contexto de oficina
 * Debe usarse dentro de un OfficeProvider
 */
export const useOfficeContext = (): IOfficeContext => {
  const context = useContext(OfficeContext);

  if (context === undefined) {
    throw new Error('useOfficeContext debe usarse dentro de un OfficeProvider');
  }

  return context;
};

export default OfficeProvider;
