import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import PanelLayout from './components/PanelLayout';
import RequireRestaurant from './components/RequireRestaurant';

// Public
import Home from './pages/public/Home';
import RestaurantDetail from './pages/public/RestaurantDetail';
import Pricing from './pages/public/Pricing';
import MapPage from './pages/public/MapPage';
import BranchPage from './pages/public/BranchPage';

// Auth
import Login from './pages/auth/Login';
import Register from './pages/auth/Register';
import OAuthSuccess from './pages/auth/OAuthSuccess';

// User
import MyReservations from './pages/user/MyReservations';

// Manager
import ManagerDashboard from './pages/manager/ManagerDashboard';
import Branches from './pages/manager/Branches';
import Tables from './pages/manager/Tables';
import ManagerReservations from './pages/manager/ManagerReservations';
import Staff from './pages/manager/Staff';
import Subscription from './pages/manager/Subscription';
import CreateRestaurant from './pages/manager/CreateRestaurant';
import AiInsights from './pages/manager/AiInsights';
import RestaurantSettings from './pages/manager/RestaurantSettings';

// Waiter
import FloorPlan from './pages/waiter/FloorPlan';

// Admin
import AdminDashboard from './pages/admin/AdminDashboard';
import AdminRestaurants from './pages/admin/AdminRestaurants';
import AdminSubscriptions from './pages/admin/AdminSubscriptions';
import AdminPlans from './pages/admin/AdminPlans';

const managerNav = [
  { to: '/manager', icon: 'grid_view', label: 'Ümumi baxış', end: true },
  { to: '/manager/restoran', icon: 'store', label: 'Restoran' },
  { to: '/manager/filiallar', icon: 'storefront', label: 'Filiallar' },
  { to: '/manager/masalar', icon: 'table_restaurant', label: 'Masalar' },
  { to: '/manager/rezervler', icon: 'event_note', label: 'Rezervlər' },
  { to: '/manager/iscilar', icon: 'groups', label: 'İşçilər' },
  { to: '/manager/ai-analitika', icon: 'auto_awesome', label: 'AI Analitika' },
  { to: '/manager/abune', icon: 'workspace_premium', label: 'Abunə' },
];

const waiterNav = [
  { to: '/waiter', icon: 'grid_view', label: 'Masa xəritəsi', end: true },
];

const adminNav = [
  { to: '/admin', icon: 'analytics', label: 'Statistika', end: true },
  { to: '/admin/restoranlar', icon: 'restaurant', label: 'Restoranlar' },
  { to: '/admin/paketler', icon: 'sell', label: 'Paketlər' },
  { to: '/admin/abuneler', icon: 'workspace_premium', label: 'Abunələr' },
];

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          {/* Public */}
          <Route path="/" element={<Home />} />
          <Route path="/restoran/:id" element={<RestaurantDetail />} />
          <Route path="/filial/:id" element={<BranchPage />} />
          <Route path="/xerite" element={<MapPage />} />
          <Route path="/qiymetler" element={<Pricing />} />
          {/* İstifadəçi girişi/qeydiyyatı */}
          <Route path="/login" element={<Login mode="user" />} />
          <Route path="/register" element={<Register mode="user" />} />
          <Route path="/oauth-success" element={<OAuthSuccess />} />

          {/* Restoran (biznes) girişi/qeydiyyatı — avtomatik MANAGER rolu */}
          <Route path="/biznes/login" element={<Login mode="business" />} />
          <Route path="/biznes/register" element={<Register mode="business" />} />

          {/* User */}
          <Route
            path="/rezervlerim"
            element={
              <ProtectedRoute roles={['USER']}>
                <MyReservations />
              </ProtectedRoute>
            }
          />

          {/* Restoran yaratma — panel-dən kənar, tam ekran (restoran yoxdursa bura yönlənir) */}
          <Route
            path="/manager/restoran-yarat"
            element={
              <ProtectedRoute roles={['MANAGER']} loginPath="/biznes/login">
                <CreateRestaurant />
              </ProtectedRoute>
            }
          />

          {/* Manager panel — RequireRestaurant restoran yoxdursa yaratma səhifəsinə göndərir */}
          <Route
            element={
              <ProtectedRoute roles={['MANAGER']} loginPath="/biznes/login">
                <PanelLayout title="AsanRezerv" subtitle="Menecer paneli" items={managerNav} />
              </ProtectedRoute>
            }
          >
            <Route element={<RequireRestaurant />}>
              <Route path="/manager" element={<ManagerDashboard />} />
              <Route path="/manager/restoran" element={<RestaurantSettings />} />
              <Route path="/manager/filiallar" element={<Branches />} />
              <Route path="/manager/masalar" element={<Tables />} />
              <Route path="/manager/rezervler" element={<ManagerReservations />} />
              <Route path="/manager/iscilar" element={<Staff />} />
              <Route path="/manager/ai-analitika" element={<AiInsights />} />
              <Route path="/manager/abune" element={<Subscription />} />
            </Route>
          </Route>

          {/* Waiter panel */}
          <Route
            element={
              <ProtectedRoute roles={['WAITER', 'MANAGER']} loginPath="/biznes/login">
                <PanelLayout title="AsanRezerv" subtitle="Ofisiant paneli" items={waiterNav} />
              </ProtectedRoute>
            }
          >
            <Route path="/waiter" element={<FloorPlan />} />
          </Route>

          {/* Admin panel */}
          <Route
            element={
              <ProtectedRoute roles={['ADMIN']}>
                <PanelLayout title="AsanRezerv" subtitle="Admin paneli" items={adminNav} />
              </ProtectedRoute>
            }
          >
            <Route path="/admin" element={<AdminDashboard />} />
            <Route path="/admin/restoranlar" element={<AdminRestaurants />} />
            <Route path="/admin/paketler" element={<AdminPlans />} />
            <Route path="/admin/abuneler" element={<AdminSubscriptions />} />
          </Route>

          {/* 404 -> ana səhifə */}
          <Route path="*" element={<Home />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
