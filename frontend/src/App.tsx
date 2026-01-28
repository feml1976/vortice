import { CssBaseline, ThemeProvider } from '@mui/material';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ReactQueryDevtools } from '@tanstack/react-query-devtools';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import theme from './theme';

// Pages - Auth
import LoginPage from '@/features/auth/pages/LoginPage';
import RegisterPage from '@/features/auth/pages/RegisterPage';
import ForgotPasswordPage from '@/features/auth/pages/ForgotPasswordPage';
import ResetPasswordPage from '@/features/auth/pages/ResetPasswordPage';
import ProfilePage from '@/features/auth/pages/ProfilePage';
import ChangePasswordPage from '@/features/auth/pages/ChangePasswordPage';

// Pages - Dashboard
import DashboardPage from '@/shared/pages/DashboardPage';

// Pages - Tire Module
import { TireSpecificationPage } from '@/features/tire';

// Pages - Organization Module
import {
  OfficePage,
  WarehousePage,
  WarehouseLocationPage,
  TireSupplierPage,
  OfficeProvider,
} from '@/features/organization';

// Components
import AuthGuard from '@/features/auth/components/AuthGuard';
import MainLayout from '@/shared/layouts/MainLayout';

// Crear instancia de QueryClient
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      retry: 1,
      staleTime: 5 * 60 * 1000, // 5 minutos
    },
  },
});

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <OfficeProvider>
          <BrowserRouter>
            <Routes>
            {/* Ruta raíz - redirige a dashboard o login según autenticación */}
            <Route path="/" element={<Navigate to="/dashboard" replace />} />

            {/* Rutas Públicas - No requieren autenticación */}
            <Route
              path="/login"
              element={
                <AuthGuard requireAuth={false}>
                  <LoginPage />
                </AuthGuard>
              }
            />
            <Route
              path="/register"
              element={
                <AuthGuard requireAuth={false}>
                  <RegisterPage />
                </AuthGuard>
              }
            />
            <Route
              path="/forgot-password"
              element={
                <AuthGuard requireAuth={false}>
                  <ForgotPasswordPage />
                </AuthGuard>
              }
            />
            <Route
              path="/reset-password"
              element={
                <AuthGuard requireAuth={false}>
                  <ResetPasswordPage />
                </AuthGuard>
              }
            />

            {/* Rutas Protegidas con Layout - Requieren autenticación */}
            <Route
              element={
                <AuthGuard>
                  <MainLayout />
                </AuthGuard>
              }
            >
              <Route path="/dashboard" element={<DashboardPage />} />
              <Route path="/profile" element={<ProfilePage />} />
              <Route path="/change-password" element={<ChangePasswordPage />} />

              {/* Módulo de Llantas */}
              <Route path="/tire/specifications" element={<TireSpecificationPage />} />

              {/* Módulo de Organización */}
              <Route path="/organization/offices" element={<OfficePage />} />
              <Route path="/organization/warehouses" element={<WarehousePage />} />
              <Route path="/organization/locations" element={<WarehouseLocationPage />} />
              <Route path="/organization/suppliers" element={<TireSupplierPage />} />

              {/* Módulos (placeholder - a implementar) */}
              <Route path="/workshop" element={<DashboardPage />} />
              <Route path="/inventory" element={<DashboardPage />} />
              <Route path="/purchasing" element={<DashboardPage />} />
              <Route path="/fleet" element={<DashboardPage />} />
              <Route path="/hr" element={<DashboardPage />} />
              <Route path="/reporting" element={<DashboardPage />} />
            </Route>

            {/* Ruta 404 - Página no encontrada */}
            <Route path="*" element={<Navigate to="/dashboard" replace />} />
          </Routes>
        </BrowserRouter>
        </OfficeProvider>
        <Toaster
          position="top-right"
          toastOptions={{
            duration: 4000,
            style: {
              background: '#333',
              color: '#fff',
            },
            success: {
              duration: 3000,
              iconTheme: {
                primary: '#4caf50',
                secondary: '#fff',
              },
            },
            error: {
              duration: 5000,
              iconTheme: {
                primary: '#f44336',
                secondary: '#fff',
              },
            },
          }}
        />
      </ThemeProvider>
      {import.meta.env.DEV && <ReactQueryDevtools initialIsOpen={false} />}
    </QueryClientProvider>
  );
}

export default App;
