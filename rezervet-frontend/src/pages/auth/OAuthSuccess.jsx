import { useEffect, useRef, useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth, roleHome } from '../../context/AuthContext';
import Spinner from '../../components/Spinner';
import ErrorAlert from '../../components/ErrorAlert';

// Google OAuth2 axınından qayıdış nöqtəsi.
// Backend uğurlu girişdən sonra brauzeri bura yönləndirir: /oauth-success?token=<jwt>
export default function OAuthSuccess() {
  const [searchParams] = useSearchParams();
  const { loginWithToken } = useAuth();
  const navigate = useNavigate();
  const [error, setError] = useState(null);
  const ran = useRef(false); // StrictMode-da iki dəfə işləməsinin qarşısını al

  useEffect(() => {
    if (ran.current) return;
    ran.current = true;

    const token = searchParams.get('token');
    if (!token) {
      setError('Giriş linki etibarsızdır — token tapılmadı.');
      return;
    }
    loginWithToken(token)
      .then((user) => navigate(roleHome(user.role), { replace: true }))
      .catch(() => setError('Google ilə giriş uğursuz oldu. Yenidən cəhd edin.'));
  }, [searchParams, loginWithToken, navigate]);

  return (
    <div className="min-h-screen bg-background flex items-center justify-center px-gutter">
      <div className="w-full max-w-md text-center space-y-md">
        {error ? (
          <>
            <ErrorAlert error={error} />
            <Link to="/login" className="text-primary font-sans text-label-md hover:underline">
              Giriş səhifəsinə qayıt
            </Link>
          </>
        ) : (
          <>
            <Spinner />
            <p className="font-sans text-body-md text-on-surface-variant">Daxil olunur...</p>
          </>
        )}
      </div>
    </div>
  );
}
