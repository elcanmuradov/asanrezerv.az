import { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth, roleHome } from '../../context/AuthContext';
import ErrorAlert from '../../components/ErrorAlert';

// mode: 'user' -> adi istifadəçilər (USER, ADMIN)
// mode: 'business' -> restoran hesabları (MANAGER, WAITER)
export default function Login({ mode = 'user' }) {
  const isBusiness = mode === 'business';
  const { login, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  const allowedRoles = isBusiness ? ['MANAGER', 'WAITER'] : ['USER', 'ADMIN'];

  const onSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      const user = await login(email, password);
      // Yanlış girişdən istifadə olunubsa sessiyanı bağla və düzgün səhifəyə yönləndir
      if (!allowedRoles.includes(user.role)) {
        logout();
        setError(
          isBusiness
            ? 'Bu hesab istifadəçi hesabıdır. Zəhmət olmasa istifadəçi girişindən daxil olun.'
            : 'Bu hesab restoran hesabıdır. Zəhmət olmasa restoran girişindən daxil olun.'
        );
        return;
      }
      const from = location.state?.from?.pathname;
      navigate(from || roleHome(user.role), { replace: true });
    } catch (err) {
      setError(err);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="min-h-screen bg-background flex items-center justify-center px-gutter">
      <div className="w-full max-w-md">
        <div className="text-center mb-lg">
          <Link to="/" className="font-serif text-display-lg-mobile font-bold text-primary">AsanRezerv</Link>
          {isBusiness && (
            <div className="inline-flex items-center gap-xs bg-primary-container text-on-primary-container px-md py-1.5 rounded-full font-sans text-label-md mt-sm mx-auto w-fit block">
              <span className="material-symbols-outlined text-[18px]">storefront</span>
              Restoran paneli
            </div>
          )}
          <p className="text-on-surface-variant text-body-md mt-xs">
            {isBusiness
              ? 'Menecer və ya ofisiant hesabınızla daxil olun'
              : 'Hesabınıza daxil olun'}
          </p>
        </div>

        <form onSubmit={onSubmit} className="bg-surface-container-lowest border border-outline-variant rounded-xl p-md space-y-md elevation-step-1">
          <ErrorAlert error={error} />
          <div className="space-y-xs">
            <label className="font-sans text-label-md text-on-surface-variant">E-poçt</label>
            <input
              type="email"
              required
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="numune@gmail.com"
              className="w-full px-md py-sm bg-surface-container rounded-lg border border-outline-variant focus:ring-2 focus:ring-primary focus:border-primary outline-none text-body-md"
            />
          </div>
          <div className="space-y-xs">
            <label className="font-sans text-label-md text-on-surface-variant">Şifrə</label>
            <input
              type="password"
              required
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
              className="w-full px-md py-sm bg-surface-container rounded-lg border border-outline-variant focus:ring-2 focus:ring-primary focus:border-primary outline-none text-body-md"
            />
          </div>
          <button
            type="submit"
            disabled={submitting}
            className="w-full py-sm bg-primary text-on-primary rounded-xl font-sans text-title-lg hover:opacity-90 active:scale-95 transition-all disabled:opacity-50"
          >
            {submitting ? 'Daxil olunur...' : 'Daxil ol'}
          </button>
          <p className="text-center text-body-md text-on-surface-variant">
            Hesabınız yoxdur?{' '}
            <Link
              to={isBusiness ? '/biznes/register' : '/register'}
              className="text-primary font-sans text-label-md hover:underline"
            >
              Qeydiyyatdan keçin
            </Link>
          </p>

          {/* Google ilə giriş — yalnız adi istifadəçi hesabları üçün (backend rolu USER təyin edir) */}
          {!isBusiness && (
            <>
              <div className="flex items-center gap-sm">
                <div className="flex-1 h-px bg-outline-variant" />
                <span className="font-sans text-caption text-on-surface-variant">və ya</span>
                <div className="flex-1 h-px bg-outline-variant" />
              </div>
              <a
                href="/oauth2/authorization/google"
                className="w-full py-sm border border-outline-variant rounded-xl font-sans text-label-md text-on-surface hover:bg-surface-container transition-colors flex items-center justify-center gap-sm"
              >
                <GoogleIcon />
                Google ilə davam et
              </a>
            </>
          )}
        </form>

        {/* Qarşı tərəfə keçid */}
        <div className="text-center mt-md">
          <Link
            to={isBusiness ? '/login' : '/biznes/login'}
            className="inline-flex items-center gap-xs text-on-surface-variant hover:text-primary font-sans text-label-md transition-colors"
          >
            <span className="material-symbols-outlined text-[18px]">
              {isBusiness ? 'person' : 'storefront'}
            </span>
            {isBusiness ? 'İstifadəçi girişi' : 'Restoran girişi'}
          </Link>
        </div>
      </div>
    </div>
  );
}

function GoogleIcon() {
  return (
    <svg width="18" height="18" viewBox="0 0 18 18" xmlns="http://www.w3.org/2000/svg">
      <path fill="#4285F4" d="M17.64 9.2c0-.64-.06-1.25-.16-1.84H9v3.48h4.84a4.14 4.14 0 0 1-1.8 2.72v2.26h2.9C16.66 14.2 17.64 11.9 17.64 9.2z" />
      <path fill="#34A853" d="M9 18c2.43 0 4.47-.8 5.96-2.18l-2.9-2.26c-.8.54-1.83.86-3.06.86-2.35 0-4.34-1.59-5.05-3.72H.98v2.33A9 9 0 0 0 9 18z" />
      <path fill="#FBBC05" d="M3.95 10.7A5.4 5.4 0 0 1 3.68 9c0-.59.1-1.17.27-1.7V4.97H.98A9 9 0 0 0 0 9c0 1.45.35 2.83.98 4.03l2.97-2.33z" />
      <path fill="#EA4335" d="M9 3.58c1.32 0 2.51.45 3.44 1.35l2.58-2.58C13.46.89 11.43 0 9 0A9 9 0 0 0 .98 4.97l2.97 2.33C4.66 5.17 6.65 3.58 9 3.58z" />
    </svg>
  );
}
