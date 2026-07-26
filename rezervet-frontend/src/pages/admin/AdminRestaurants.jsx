import { useEffect, useState } from 'react';
import Spinner from '../../components/Spinner';
import EmptyState from '../../components/EmptyState';
import ErrorAlert from '../../components/ErrorAlert';
import { getAdminRestaurants, updateRestaurantStatus } from '../../api/admin';

export default function AdminRestaurants() {
  const [restaurants, setRestaurants] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [busyId, setBusyId] = useState(null);

  const load = () => {
    setLoading(true);
    getAdminRestaurants()
      .then((res) => setRestaurants(res.data?.content ?? res.data ?? []))
      .catch((err) => setError(err))
      .finally(() => setLoading(false));
  };

  useEffect(load, []);

  const onToggleStatus = async (r) => {
    const next = r.status === 'SUSPENDED' ? 'ACTIVE' : 'SUSPENDED';
    const question = next === 'SUSPENDED'
      ? `"${r.name}" restoranını dayandırmaq istədiyinizə əminsiniz?`
      : `"${r.name}" restoranını aktivləşdirmək istəyirsiniz?`;
    if (!window.confirm(question)) return;
    setBusyId(r.id);
    setError(null);
    try {
      await updateRestaurantStatus(r.id, next);
      load();
    } catch (err) {
      setError(err);
    } finally {
      setBusyId(null);
    }
  };

  return (
    <div className="space-y-gutter">
      <header className="space-y-base">
        <h2 className="font-serif text-headline-md text-on-background">Restoranlar</h2>
      </header>
      <ErrorAlert error={error} />

      {loading ? (
        <Spinner />
      ) : restaurants.length === 0 ? (
        <EmptyState icon="restaurant" title="Hələ restoran yoxdur" />
      ) : (
        <div className="bg-surface-container-lowest border border-outline-variant rounded-xl overflow-hidden elevation-step-1">
          <div className="overflow-x-auto">
            <table className="w-full text-left">
              <thead className="bg-surface-container-high border-b border-outline-variant">
                <tr>
                  {['Restoran', 'Sahibi', 'Paket', 'Filial', 'Status', ''].map((h, i) => (
                    <th key={i} className="px-md py-sm font-sans text-label-md text-on-surface-variant whitespace-nowrap">{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {restaurants.map((r) => (
                  <tr key={r.id} className="border-b border-outline-variant/50 last:border-0 hover:bg-surface-container-low transition-colors">
                    <td className="px-md py-sm font-sans text-body-md text-on-surface font-bold">{r.name}</td>
                    <td className="px-md py-sm text-body-md text-on-surface-variant">
                      {r.ownerName ?? '—'}
                      {r.ownerEmail && <div className="text-caption">{r.ownerEmail}</div>}
                    </td>
                    <td className="px-md py-sm text-body-md text-on-surface-variant">{r.planName ?? '—'}</td>
                    <td className="px-md py-sm text-body-md text-on-surface-variant">{r.branchCount ?? '—'}</td>
                    <td className="px-md py-sm">
                      <span className={`px-sm py-1 rounded-full font-sans text-caption ${
                        r.status === 'ACTIVE'
                          ? 'bg-primary-fixed text-on-primary-fixed'
                          : 'bg-error-container text-on-error-container'
                      }`}>
                        {r.status === 'ACTIVE' ? 'Aktiv' : r.status === 'SUSPENDED' ? 'Dayandırılıb' : r.status}
                      </span>
                    </td>
                    <td className="px-md py-sm text-right">
                      <button
                        onClick={() => onToggleStatus(r)}
                        disabled={busyId === r.id}
                        className={`px-md py-1.5 rounded-lg font-sans text-label-md transition-colors disabled:opacity-50 ${
                          r.status === 'SUSPENDED'
                            ? 'text-primary border border-primary hover:bg-primary-fixed'
                            : 'text-error border border-error hover:bg-error-container'
                        }`}
                      >
                        {r.status === 'SUSPENDED' ? 'Aktivləşdir' : 'Dayandır'}
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
}
