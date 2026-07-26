import { useEffect, useState } from 'react';
import Spinner from '../../components/Spinner';
import EmptyState from '../../components/EmptyState';
import ErrorAlert from '../../components/ErrorAlert';
import { getAdminSubscriptions } from '../../api/admin';

const statusLabels = {
  ACTIVE: { text: 'Aktiv', cls: 'bg-primary-fixed text-on-primary-fixed' },
  PAST_DUE: { text: 'Gecikir', cls: 'bg-error-container text-on-error-container' },
  CANCELLED: { text: 'Ləğv edilib', cls: 'bg-surface-container-high text-on-surface-variant' },
  PENDING: { text: 'Gözləyir', cls: 'bg-surface-container-high text-on-surface-variant' },
};

export default function AdminSubscriptions() {
  const [subs, setSubs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    getAdminSubscriptions()
      .then((res) => setSubs(res.data?.content ?? res.data ?? []))
      .catch((err) => setError(err))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="space-y-gutter">
      <header className="space-y-base">
        <h2 className="font-serif text-headline-md text-on-background">Abunələr</h2>
      </header>
      <ErrorAlert error={error} />

      {loading ? (
        <Spinner />
      ) : subs.length === 0 ? (
        <EmptyState icon="workspace_premium" title="Hələ abunə yoxdur" />
      ) : (
        <div className="bg-surface-container-lowest border border-outline-variant rounded-xl overflow-hidden elevation-step-1">
          <div className="overflow-x-auto">
            <table className="w-full text-left">
              <thead className="bg-surface-container-high border-b border-outline-variant">
                <tr>
                  {['Restoran', 'Paket', 'Başlama', 'Növbəti ödəniş', 'Məbləğ', 'Status'].map((h) => (
                    <th key={h} className="px-md py-sm font-sans text-label-md text-on-surface-variant whitespace-nowrap">{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {subs.map((s) => {
                  const status = statusLabels[s.status] ?? { text: s.status, cls: 'bg-surface-container text-on-surface-variant' };
                  return (
                    <tr key={s.id} className="border-b border-outline-variant/50 last:border-0 hover:bg-surface-container-low transition-colors">
                      <td className="px-md py-sm font-sans text-body-md text-on-surface font-bold">{s.restaurantName ?? '—'}</td>
                      <td className="px-md py-sm text-body-md text-on-surface-variant">{s.planName ?? '—'}</td>
                      <td className="px-md py-sm text-body-md text-on-surface-variant whitespace-nowrap">{s.startedAt ?? '—'}</td>
                      <td className="px-md py-sm text-body-md text-on-surface-variant whitespace-nowrap">{s.currentPeriodEnd ?? '—'}</td>
                      <td className="px-md py-sm text-body-md text-on-surface-variant">{s.monthlyPrice != null ? `${s.monthlyPrice} ₼` : '—'}</td>
                      <td className="px-md py-sm">
                        <span className={`px-sm py-1 rounded-full font-sans text-caption ${status.cls}`}>{status.text}</span>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
}
