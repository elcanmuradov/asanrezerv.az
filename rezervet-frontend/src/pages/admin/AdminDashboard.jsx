import { useEffect, useState } from 'react';
import Spinner from '../../components/Spinner';
import ErrorAlert from '../../components/ErrorAlert';
import { getAdminStats } from '../../api/admin';

export default function AdminDashboard() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    getAdminStats()
      .then((res) => setStats(res.data))
      .catch((err) => setError(err))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <Spinner />;

  const cards = [
    { label: 'Ümumi restoran', value: stats?.restaurantCount ?? '—', icon: 'restaurant' },
    { label: 'Aktiv abunə', value: stats?.activeSubscriptions ?? '—', icon: 'workspace_premium' },
    { label: 'Ümumi istifadəçi', value: stats?.userCount ?? '—', icon: 'group' },
    { label: 'Bu ay rezerv', value: stats?.monthlyReservations ?? '—', icon: 'event_note' },
    { label: 'Aylıq gəlir', value: stats?.monthlyRevenue != null ? `${stats.monthlyRevenue} ₼` : '—', icon: 'payments' },
  ];

  return (
    <div className="space-y-gutter">
      <header className="space-y-base">
        <h2 className="font-serif text-headline-md text-on-background">Platforma statistikası</h2>
      </header>
      <ErrorAlert error={error} />
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-md">
        {cards.map((c) => (
          <div key={c.label} className="bg-surface border border-outline-variant rounded-xl p-md flex items-center gap-md">
            <div className="w-12 h-12 rounded-lg bg-primary-fixed flex items-center justify-center">
              <span className="material-symbols-outlined text-primary">{c.icon}</span>
            </div>
            <div>
              <p className="font-sans text-label-md text-on-surface-variant">{c.label}</p>
              <h5 className="font-serif text-headline-md text-primary">{c.value}</h5>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
