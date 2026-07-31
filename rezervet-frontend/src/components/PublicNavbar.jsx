import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useAuth, roleHome } from '../context/AuthContext';

export default function PublicNavbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const navLinkClass = ({ isActive }) =>
    isActive
      ? 'font-sans text-label-md text-primary border-b-2 border-primary pb-1'
      : 'font-sans text-label-md text-on-surface-variant hover:text-primary transition-colors';

  return (
    <header className="bg-surface border-b border-outline-variant sticky top-0 z-50">
      <nav className="flex justify-between items-center w-full px-gutter max-w-container-max mx-auto h-16">
        <div className="flex items-center gap-md">
          <Link to="/" className="font-serif text-headline-md font-bold text-primary">
            AsanRezerv
          </Link>
          <div className="hidden md:flex items-center gap-md ml-lg">
            <NavLink to="/" end className={navLinkClass}>Restoranlar</NavLink>
            <NavLink to="/xerite" className={navLinkClass}>Xəritə</NavLink>
            <NavLink to="/qiymetler" className={navLinkClass}>Biznes üçün</NavLink>
            {user?.role === 'USER' && (
              <NavLink to="/rezervlerim" className={navLinkClass}>Rezervlərim</NavLink>
            )}
          </div>
        </div>
        <div className="flex items-center gap-sm">
          {!user ? (
            <>
              <Link
                to="/biznes/login"
                className="hidden sm:flex items-center gap-xs px-md py-2 text-on-surface-variant font-sans text-label-md hover:text-primary rounded-xl transition-colors"
              >
                <span className="material-symbols-outlined text-[18px]">storefront</span>
                Restoran girişi
              </Link>
              <Link
                to="/login"
                className="px-md py-2 text-primary font-sans text-label-md hover:bg-surface-container rounded-xl transition-colors"
              >
                Daxil ol
              </Link>
              <Link
                to="/register"
                className="px-md py-2 bg-primary text-on-primary rounded-xl font-sans text-label-md hover:opacity-90 transition-all active:scale-95"
              >
                Qeydiyyat
              </Link>
            </>
          ) : (
            <>
              {user.role !== 'USER' && (
                <button
                  onClick={() => navigate(roleHome(user.role))}
                  className="px-md py-2 border border-outline text-on-surface rounded-xl font-sans text-label-md hover:bg-surface-container transition-colors"
                >
                  Panelə keç
                </button>
              )}
              <div className="flex items-center gap-base ml-xs">
                <div className="w-8 h-8 rounded-full bg-primary-fixed flex items-center justify-center text-primary font-bold text-caption uppercase">
                  {(user.fullName || user.email || '?').charAt(0)}
                </div>
                <span className="hidden sm:block font-sans text-label-md text-on-surface">
                  {user.fullName || user.email}
                </span>
                <button
                  onClick={() => { logout(); navigate('/'); }}
                  title="Çıxış"
                  className="p-xs text-on-surface-variant hover:text-primary transition-colors"
                >
                  <span className="material-symbols-outlined">logout</span>
                </button>
              </div>
            </>
          )}
        </div>
      </nav>
    </header>
  );
}
