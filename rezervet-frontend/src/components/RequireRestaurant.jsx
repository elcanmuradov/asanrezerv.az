import { useEffect, useState } from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { getMyRestaurant } from '../api/restaurants';
import Spinner from './Spinner';

// Menecer panelini qoruyur: restoran yoxdursa əvvəlcə "restoran yarat" səhifəsinə yönləndirir.
// getMyRestaurant 404 (və ya boş data) qaytardıqda restoran yoxdur sayılır.
export default function RequireRestaurant() {
  const [state, setState] = useState('loading'); // loading | has | none

  useEffect(() => {
    let active = true;
    getMyRestaurant()
      .then((res) => { if (active) setState(res.data ? 'has' : 'none'); })
      .catch((err) => {
        if (!active) return;
        // 404 => restoran yoxdur; digər xətalarda da yaratma səhifəsinə göndəririk
        setState('none');
      });
    return () => { active = false; };
  }, []);

  if (state === 'loading') return <Spinner full />;
  if (state === 'none') return <Navigate to="/manager/restoran-yarat" replace />;
  return <Outlet />;
}
