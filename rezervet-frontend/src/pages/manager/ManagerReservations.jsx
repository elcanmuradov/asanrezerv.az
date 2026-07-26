import { useEffect, useState } from 'react';
import Spinner from '../../components/Spinner';
import EmptyState from '../../components/EmptyState';
import ErrorAlert from '../../components/ErrorAlert';
import { getRestaurantReservations, getBranchReservations } from '../../api/reservations';
import { getBranches, getMyRestaurant } from '../../api/restaurants';

const statusLabels = {
  PENDING: { text: 'Gözləyir', cls: 'bg-surface-container-high text-on-surface-variant' },
  CONFIRMED: { text: 'Təsdiqlənib', cls: 'bg-primary-fixed text-on-primary-fixed' },
  CANCELLED: { text: 'Ləğv edilib', cls: 'bg-error-container text-on-error-container' },
  COMPLETED: { text: 'Tamamlanıb', cls: 'bg-secondary-container text-on-secondary-container' },
  NO_SHOW: { text: 'Gəlmədi', cls: 'bg-error-container text-on-error-container' },
};

export default function ManagerReservations() {
  const [restaurantId, setRestaurantId] = useState(null);
  const [branches, setBranches] = useState([]);
  const [branchId, setBranchId] = useState('');
  const [date, setDate] = useState('');
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    getMyRestaurant()
      .then((res) => setRestaurantId(res.data?.id ?? null))
      .catch(() => setRestaurantId(null));
    getBranches()
      .then((res) => setBranches(res.data?.content ?? res.data ?? []))
      .catch(() => {});
  }, []);

  const load = () => {
    setLoading(true);
    setError(null);
    // Filial seçilibsə → filialın rezervləri, əks halda → bütün restoranın rezervləri.
    // Rezervlər reservation-service-dən (/api/reservations) gəlir.
    const req = branchId
      ? getBranchReservations(branchId)
      : restaurantId
      ? getRestaurantReservations(restaurantId)
      : null;
    if (!req) {
      setReservations([]);
      setLoading(false);
      return;
    }
    req
      .then((res) => {
        let list = res.data?.content ?? res.data ?? [];
        if (date) list = list.filter((r) => r.date === date); // tarix filtri client tərəfdə
        setReservations(list);
      })
      .catch((err) => setError(err))
      .finally(() => setLoading(false));
  };

  useEffect(load, [branchId, date, restaurantId]);

  return (
    <div className="space-y-gutter">
      <header className="flex flex-wrap justify-between items-end gap-md">
        <div className="space-y-base">
          <nav className="flex items-center gap-2 text-on-surface-variant opacity-60">
            <span className="font-sans text-label-md">Panel</span>
            <span className="material-symbols-outlined text-sm">chevron_right</span>
            <span className="font-sans text-label-md">Rezervlər</span>
          </nav>
          <h2 className="font-serif text-headline-md text-on-background">Rezervlər</h2>
        </div>
        <div className="flex items-end gap-md">
          <div className="flex flex-col gap-xs">
            <label className="font-sans text-label-md text-on-surface-variant">Filial</label>
            <select
              value={branchId}
              onChange={(e) => setBranchId(e.target.value)}
              className="px-md py-sm bg-surface-container rounded-lg border border-outline-variant focus:ring-2 focus:ring-primary text-body-md min-w-[180px]"
            >
              <option value="">Bütün filiallar</option>
              {branches.map((b) => (
                <option key={b.id} value={b.id}>{b.name}</option>
              ))}
            </select>
          </div>
          <div className="flex flex-col gap-xs">
            <label className="font-sans text-label-md text-on-surface-variant">Tarix</label>
            <input
              type="date"
              value={date}
              onChange={(e) => setDate(e.target.value)}
              className="px-md py-sm bg-surface-container rounded-lg border border-outline-variant focus:ring-2 focus:ring-primary text-body-md"
            />
          </div>
        </div>
      </header>

      <ErrorAlert error={error} />

      {loading ? (
        <Spinner />
      ) : reservations.length === 0 ? (
        <EmptyState
          icon="event_note"
          title="Rezerv tapılmadı"
          subtitle="Seçilmiş filtrlərə uyğun rezerv yoxdur."
        />
      ) : (
        <div className="bg-surface-container-lowest border border-outline-variant rounded-xl overflow-hidden elevation-step-1">
          <div className="overflow-x-auto">
            <table className="w-full text-left">
              <thead className="bg-surface-container-high border-b border-outline-variant">
                <tr>
                  {['Qonaq', 'Filial', 'Masa', 'Tarix', 'Saat', 'Qonaq sayı', 'Mənbə', 'Status'].map((h) => (
                    <th key={h} className="px-md py-sm font-sans text-label-md text-on-surface-variant whitespace-nowrap">{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {reservations.map((r) => {
                  const status = statusLabels[r.status] ?? { text: r.status, cls: 'bg-surface-container text-on-surface-variant' };
                  return (
                    <tr key={r.id} className="border-b border-outline-variant/50 last:border-0 hover:bg-surface-container-low transition-colors">
                      <td className="px-md py-sm font-sans text-body-md text-on-surface">
                        {r.guestName ?? r.userFullName ?? '—'}
                        {r.guestPhone && <div className="text-caption text-on-surface-variant">{r.guestPhone}</div>}
                      </td>
                      <td className="px-md py-sm text-body-md text-on-surface-variant">{r.branchName ?? '—'}</td>
                      <td className="px-md py-sm text-body-md text-on-surface-variant">{r.tableName ?? '—'}</td>
                      <td className="px-md py-sm text-body-md text-on-surface-variant whitespace-nowrap">{r.date}</td>
                      <td className="px-md py-sm text-body-md text-on-surface-variant">{r.time}</td>
                      <td className="px-md py-sm text-body-md text-on-surface-variant">{r.partySize}</td>
                      <td className="px-md py-sm text-body-md text-on-surface-variant">
                        {r.source === 'MANUAL' ? 'Əl ilə' : 'Onlayn'}
                      </td>
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
