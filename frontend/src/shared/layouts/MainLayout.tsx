/**
 * Layout principal con Sidebar y AppBar
 */

import { useState } from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import {
  Box,
  AppBar,
  Toolbar,
  IconButton,
  Typography,
  Drawer,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Avatar,
  Divider,
  Menu,
  MenuItem,
  useTheme,
  useMediaQuery,
  Collapse,
} from '@mui/material';
import {
  Menu as MenuIcon,
  Dashboard as DashboardIcon,
  Build as BuildIcon,
  Inventory as InventoryIcon,
  ShoppingCart as PurchasingIcon,
  DirectionsCar as FleetIcon,
  People as HRIcon,
  Assessment as ReportingIcon,
  Person as PersonIcon,
  Lock as LockIcon,
  ExitToApp as LogoutIcon,
  ChevronLeft as ChevronLeftIcon,
  TireRepair as TireRepairIcon,
  ExpandLess,
  ExpandMore,
  Business as BusinessIcon,
  Warehouse as WarehouseIcon,
  Place as PlaceIcon,
  Store as StoreIcon,
  AccountTree as OrganizationIcon,
} from '@mui/icons-material';
import { useAuthStore } from '@/features/auth/store/authStore';

const DRAWER_WIDTH = 260;

interface SubMenuItem {
  text: string;
  icon: React.ReactElement;
  path: string;
}

interface MenuItem {
  text: string;
  icon: React.ReactElement;
  path?: string;
  subItems?: SubMenuItem[];
}

const menuItems: MenuItem[] = [
  { text: 'Dashboard', icon: <DashboardIcon />, path: '/dashboard' },
  {
    text: 'Organización',
    icon: <OrganizationIcon />,
    subItems: [
      { text: 'Oficinas', icon: <BusinessIcon />, path: '/organization/offices' },
      { text: 'Almacenes', icon: <WarehouseIcon />, path: '/organization/warehouses' },
      { text: 'Ubicaciones', icon: <PlaceIcon />, path: '/organization/locations' },
      { text: 'Proveedores de Llantas', icon: <StoreIcon />, path: '/organization/suppliers' },
    ]
  },
  {
    text: 'Taller',
    icon: <BuildIcon />,
    subItems: [
      { text: 'Fichas Técnicas de Llantas', icon: <TireRepairIcon />, path: '/tire/specifications' },
    ]
  },
  { text: 'Inventario', icon: <InventoryIcon />, path: '/inventory' },
  { text: 'Compras', icon: <PurchasingIcon />, path: '/purchasing' },
  { text: 'Flota', icon: <FleetIcon />, path: '/fleet' },
  { text: 'Recursos Humanos', icon: <HRIcon />, path: '/hr' },
  { text: 'Reportes', icon: <ReportingIcon />, path: '/reporting' },
];

/**
 * Componente MainLayout
 */
export default function MainLayout() {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));
  const navigate = useNavigate();
  const location = useLocation();
  const { user, logout } = useAuthStore();

  const [drawerOpen, setDrawerOpen] = useState(!isMobile);
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const [expandedMenus, setExpandedMenus] = useState<Record<string, boolean>>({});

  const handleDrawerToggle = () => {
    setDrawerOpen(!drawerOpen);
  };

  const handleMenuOpen = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
  };

  const handleNavigate = (path: string) => {
    navigate(path);
    if (isMobile) {
      setDrawerOpen(false);
    }
  };

  const handleToggleMenu = (menuText: string) => {
    setExpandedMenus((prev) => ({
      ...prev,
      [menuText]: !prev[menuText],
    }));
  };

  const handleProfile = () => {
    handleMenuClose();
    navigate('/profile');
  };

  const handleChangePassword = () => {
    handleMenuClose();
    navigate('/change-password');
  };

  const handleLogout = async () => {
    handleMenuClose();
    await logout();
    navigate('/login');
  };

  const drawerContent = (
    <Box>
      {/* Logo y título */}
      <Box
        sx={{
          p: 2,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          minHeight: 64,
        }}
      >
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <BuildIcon sx={{ fontSize: 32, color: 'primary.main' }} />
          <Typography variant="h6" fontWeight="bold">
            Vórtice
          </Typography>
        </Box>
        {isMobile && (
          <IconButton onClick={handleDrawerToggle}>
            <ChevronLeftIcon />
          </IconButton>
        )}
      </Box>

      <Divider />

      {/* Menú de navegación */}
      <List>
        {menuItems.map((item) => (
          <Box key={item.text}>
            {/* Ítem de menú principal */}
            <ListItem disablePadding>
              <ListItemButton
                selected={item.path ? location.pathname === item.path : false}
                onClick={() => {
                  if (item.path) {
                    handleNavigate(item.path);
                  } else if (item.subItems) {
                    handleToggleMenu(item.text);
                  }
                }}
              >
                <ListItemIcon>{item.icon}</ListItemIcon>
                <ListItemText primary={item.text} />
                {item.subItems && (
                  expandedMenus[item.text] ? <ExpandLess /> : <ExpandMore />
                )}
              </ListItemButton>
            </ListItem>

            {/* Submenú colapsable */}
            {item.subItems && (
              <Collapse in={expandedMenus[item.text]} timeout="auto" unmountOnExit>
                <List component="div" disablePadding>
                  {item.subItems.map((subItem) => (
                    <ListItem key={subItem.path} disablePadding>
                      <ListItemButton
                        sx={{ pl: 4 }}
                        selected={location.pathname === subItem.path}
                        onClick={() => handleNavigate(subItem.path)}
                      >
                        <ListItemIcon>{subItem.icon}</ListItemIcon>
                        <ListItemText primary={subItem.text} />
                      </ListItemButton>
                    </ListItem>
                  ))}
                </List>
              </Collapse>
            )}
          </Box>
        ))}
      </List>
    </Box>
  );

  return (
    <Box sx={{ display: 'flex' }}>
      {/* AppBar */}
      <AppBar
        position="fixed"
        sx={{
          zIndex: theme.zIndex.drawer + 1,
          backgroundColor: 'primary.main',
        }}
      >
        <Toolbar>
          <IconButton
            color="inherit"
            edge="start"
            onClick={handleDrawerToggle}
            sx={{ mr: 2 }}
          >
            <MenuIcon />
          </IconButton>

          <Typography variant="h6" noWrap component="div" sx={{ flexGrow: 1 }}>
            Sistema de Gestión de Taller
          </Typography>

          {/* Usuario y menú */}
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
            <Typography variant="body2" sx={{ display: { xs: 'none', sm: 'block' } }}>
              {user?.firstName} {user?.lastName}
            </Typography>
            <IconButton onClick={handleMenuOpen} sx={{ p: 0 }}>
              <Avatar sx={{ bgcolor: 'secondary.main' }}>
                {user?.firstName?.charAt(0)}
                {user?.lastName?.charAt(0)}
              </Avatar>
            </IconButton>
          </Box>
        </Toolbar>
      </AppBar>

      {/* Menú de usuario */}
      <Menu
        anchorEl={anchorEl}
        open={Boolean(anchorEl)}
        onClose={handleMenuClose}
        anchorOrigin={{
          vertical: 'bottom',
          horizontal: 'right',
        }}
        transformOrigin={{
          vertical: 'top',
          horizontal: 'right',
        }}
      >
        <MenuItem onClick={handleProfile}>
          <ListItemIcon>
            <PersonIcon fontSize="small" />
          </ListItemIcon>
          Mi Perfil
        </MenuItem>
        <MenuItem onClick={handleChangePassword}>
          <ListItemIcon>
            <LockIcon fontSize="small" />
          </ListItemIcon>
          Cambiar Contraseña
        </MenuItem>
        <Divider />
        <MenuItem onClick={handleLogout}>
          <ListItemIcon>
            <LogoutIcon fontSize="small" />
          </ListItemIcon>
          Cerrar Sesión
        </MenuItem>
      </Menu>

      {/* Drawer (Sidebar) */}
      <Drawer
        variant={isMobile ? 'temporary' : 'persistent'}
        open={drawerOpen}
        onClose={handleDrawerToggle}
        sx={{
          width: DRAWER_WIDTH,
          flexShrink: 0,
          '& .MuiDrawer-paper': {
            width: DRAWER_WIDTH,
            boxSizing: 'border-box',
          },
        }}
      >
        {drawerContent}
      </Drawer>

      {/* Contenido principal */}
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          p: 3,
          mt: 8,
          width: { sm: `calc(100% - ${drawerOpen ? DRAWER_WIDTH : 0}px)` },
          transition: theme.transitions.create(['width', 'margin'], {
            easing: theme.transitions.easing.sharp,
            duration: theme.transitions.duration.enteringScreen,
          }),
        }}
      >
        <Outlet />
      </Box>
    </Box>
  );
}
