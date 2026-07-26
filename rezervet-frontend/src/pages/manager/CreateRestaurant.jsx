import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getMyRestaurant, createMyRestaurant } from '../../api/restaurants';
import { useAuth } from '../../context/AuthContext';
import ErrorAlert from '../../components/ErrorAlert';
import Spinner from '../../components/Spinner';
import { formatAzPhoneInput } from '../../utils/phone';

// Menecerin ilk addımı: restoranını yaradır. Restoran artıq varsa panelə yönləndirir.
export default function CreateRestaurant() {
  const navigate = useNavigate();
  const { user, logout } = useAuth();

  const [checking, setChecking] = useState(true);
  const [form, setForm] = useState({ name: '', cuisine: '', city: '', phone: '', description: '' });
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);

  // Restoran onsuz da varsa, bu səhifəyə ehtiyac yoxdur — panelə keç
  useEffect(() => {
    let active = true;
    getMyRestaurant()
      .then((res) => { if (active && res.data) navigate('/manager', { replace: true }); })
      .catch(() => {})
      .finally(() => { if (active) setChecking(false); });
    return () => { active = false; };
  }, [navigate]);

  const onSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      await createMyRestaurant(form);
      navigate('/manager', { replace: true });
    } catch (err) {
      setError(err);
    } finally {
      setSubmitting(false);
    }
  };

  const inputClass =
    'w-full px-md py-sm bg-surface-container rounded-lg border border-outline-variant focus:ring-2 focus:ring-primary focus:border-primary outline-none text-body-md';

  if (checking) return <Spinner full />;

  return (
    <div className="min-h-screen bg-background flex items-center justify-center px-gutter py-lg">
      <div className="w-full max-w-lg">
        <div className="text-center mb-lg">
          <span className="font-serif text-display-lg-mobile font-bold text-primary">Rezervet</span>
          <div className="inline-flex items-center gap-xs bg-primary-container text-on-primary-container px-md py-1.5 rounded-full font-sans text-label-md mt-sm">
            <span className="material-symbols-outlined text-[18px]">storefront</span>
            İlk addım
          </div>
          <h1 className="font-serif text-headline-md text-on-background mt-sm">Restoranınızı yaradın</h1>
          <p className="text-on-surface-variant text-body-md mt-xs">
            Filiallar, masalar və rezervlər restoranınıza bağlıdır — əvvəlcə onu yaradın.
          </p>
        </div>

        <form onSubmit={onSubmit} className="bg-surface-container-lowest border border-outline-variant rounded-xl p-md space-y-md elevation-step-1">
          <ErrorAlert error={error} />

          <div className="space-y-xs">
            <label className="font-sans text-label-md text-on-surface-variant">Restoran adı *</label>
            <input type="text" required value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} placeholder="Məs: Firuzə Restoran" className={inputClass} />
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-md">
            <div className="space-y-xs">
              <label className="font-sans text-label-md text-on-surface-variant">Mətbəx növü</label>
              <input type="text" value={form.cuisine} onChange={(e) => setForm({ ...form, cuisine: e.target.value })} placeholder="Milli, Avropa..." className={inputClass} />
            </div>
            <div className="space-y-xs">
              <label className="font-sans text-label-md text-on-surface-variant">Şəhər</label>
              <input type="text" value={form.city} onChange={(e) => setForm({ ...form, city: e.target.value })} placeholder="Bakı" className={inputClass} />
            </div>
          </div>

          <div className="space-y-xs">
            <label className="font-sans text-label-md text-on-surface-variant">Telefon</label>
            <input
              type="tel"
              value={form.phone}
              onChange={(e) => setForm({ ...form, phone: formatAzPhoneInput(e.target.value) })}
              placeholder="+994 (50) 000 00 00"
              className={inputClass}
            />
          </div>

          <div className="space-y-xs">
            <label className="font-sans text-label-md text-on-surface-variant">Qısa təsvir</label>
            <textarea rows={3} value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} placeholder="Restoranınız haqqında bir neçə cümlə..." className={inputClass} />
          </div>

          <button
            type="submit"
            disabled={submitting}
            className="w-full py-sm bg-primary text-on-primary rounded-xl font-sans text-title-lg hover:opacity-90 active:scale-95 transition-all disabled:opacity-50"
          >
            {submitting ? 'Yaradılır...' : 'Restoranı yarat və panelə keç'}
          </button>
        </form>

        <div className="text-center mt-md">
          <button
            onClick={() => { logout(); navigate('/biznes/login'); }}
            className="inline-flex items-center gap-xs text-on-surface-variant hover:text-primary font-sans text-label-md transition-colors"
          >
            <span className="material-symbols-outlined text-[18px]">logout</span>
            {user?.email} — çıxış
          </button>
        </div>
      </div>
    </div>
  );
}
