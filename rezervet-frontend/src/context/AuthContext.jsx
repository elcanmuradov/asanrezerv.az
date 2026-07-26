import { createContext, useContext, useEffect, useState, useCallback } from 'react';
import { login as loginApi, getMe } from '../api/auth';

const AuthContext = createContext(null);

// Rol -> giriş sonrası yönləndirmə
export const roleHome = (role) => {
  switch (role) {
    case 'ADMIN': return '/admin';
    case 'MANAGER': return '/manager';
    case 'WAITER': return '/waiter';
    default: return '/';
  }
};

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    if (!token) { setLoading(false); return; }
    getMe()
      .then((res) => setUser(res.data))
      .catch(() => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
      })
      .finally(() => setLoading(false));
  }, []);

  const login = useCallback(async (email, password) => {
    const { data } = await loginApi(email, password);
    localStorage.setItem('accessToken', data.jwtToken);
    if (data.refreshToken) localStorage.setItem('refreshToken', data.refreshToken);
    const me = data.user ?? (await getMe()).data;
    setUser(me);
    return me;
  }, []);

  // OAuth2 (Google) axınından qayıdan token ilə giriş — backend /oauth-success?token=... ilə yönləndirir
  const loginWithToken = useCallback(async (token) => {
    localStorage.setItem('accessToken', token);
    const me = (await getMe()).data;
    setUser(me);
    return me;
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    setUser(null);
  }, []);

  return (
    <AuthContext.Provider value={{ user, setUser, login, loginWithToken, logout, loading }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
