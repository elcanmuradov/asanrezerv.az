import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

// Manager / Waiter / Admin panelləri üçün ortaq sol menyu tərtibatı.
// items: [{ to, icon, label, end? }]
export default function PanelLayout({ title, subtitle, items }) {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const linkClass = ({ isActive }) =>
    isActive
      ? 'flex items-center gap-sm px-md py-sm bg-primary-container text-on-primary-container rounded-lg font-bold font-sans text-body-md transition-transform'
      : 'flex items-center gap-sm px-md py-sm text-on-surface-variant hover:bg-surface-container-high rounded-lg font-sans text-body-md transition-colors group';

  return (
    <div className="flex min-h-screen bg-background">
      <aside className="fixed left-0 top-0 h-full flex flex-col p-base bg-surface-container-low border-r border-outline-variant w-64 z-40">
        <div className="flex items-center gap-sm px-3 py-6 mb-md">
          <div className="w-10 h-10 rounded-lg bg-primary flex items-center justify-center text-on-primary">
            <span className="material-symbols-outlined">restaurant</span>
          </div>
          <div>
            <h1 className="font-serif text-title-lg font-bold text-primary leading-tight">{title}</h1>
            <p className="font-sans text-caption text-on-surface-variant">{subtitle}</p>
          </div>
        </div>

        <nav className="flex-1 space-y-1 overflow-y-auto">
          {items.map((item) => (
            <NavLink key={item.to} to={item.to} end={item.end} className={linkClass}>
              <span className="material-symbols-outlined">{item.icon}</span>
              <span>{item.label}</span>
            </NavLink>
          ))}
        </nav>

        <div className="mt-auto space-y-xs border-t border-outline-variant pt-base">
          <div className="px-md py-sm">
            <p className="font-sans text-label-md text-on-surface truncate">{user?.fullName || user?.email}</p>
            <p className="font-sans text-caption text-on-surface-variant">{roleLabel(user?.role)}</p>
          </div>
          <button
            onClick={() => { logout(); navigate('/'); }}
            className="w-full flex items-center gap-sm px-md py-sm text-on-surface-variant hover:bg-surface-container-high rounded-lg font-sans text-body-md transition-colors"
          >
            <span className="material-symbols-outlined">logout</span>
            <span>Çıxış</span>
          </button>
        </div>
      </aside>

      <main className="ml-64 flex-1 p-gutter">
        <Outlet />
      </main>
    </div>
  );
}

function roleLabel(role) {
  switch (role) {
    case 'ADMIN': return 'Administrator';
    case 'MANAGER': return 'Menecer';
    case 'WAITER': return 'Ofisiant';
    default: return 'İstifadəçi';
  }
}
