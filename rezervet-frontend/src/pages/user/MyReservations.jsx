import { useEffect, useState } from 'react';
import PublicNavbar from '../../components/PublicNavbar';
import Footer from '../../components/Footer';
import Spinner from '../../components/Spinner';
import EmptyState from '../../components/EmptyState';
import ErrorAlert from '../../components/ErrorAlert';
import { getMyReservations, cancelReservation } from '../../api/reservations';

const statusLabels = {
  PENDING: { text: 'Gözləyir', cls: 'bg-surface-container-high text-on-surface-variant' },
  CONFIRMED: { text: 'Təsdiqlənib', cls: 'bg-primary-fixed text-on-primary-fixed' },
  CANCELLED: { text: 'Ləğv edilib', cls: 'bg-error-container text-on-error-container' },
  COMPLETED: { text: 'Tamamlanıb', cls: 'bg-secondary-container text-on-secondary-container' },
  NO_SHOW: { text: 'Gəlmədi', cls: 'bg-error-container text-on-error-container' },
};

export default function MyReservations() {
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [cancellingId, setCancellingId] = useState(null);

  const load = () => {
    setLoading(true);
    getMyReservations()
      .then((res) => setReservations(res.data?.content ?? res.data ?? []))
      .catch((err) => setError(err))
      .finally(() => setLoading(false));
  };

  useEffect(load, []);

  const onCancel = async (id) => {
    setCancellingId(id);
    setError(null);
    try {
      await cancelReservation(id);
      load();
    } catch (err) {
      setError(err);
    } finally {
      setCancellingId(null);
    }
  };

  return (
    <div className="min-h-screen flex flex-col bg-background">
      <PublicNavbar />
      <main className="flex-1 max-w-container-max mx-auto w-full px-gutter py-lg">
        <h1 className="font-serif text-headline-md text-on-background mb-md">Rezervlərim</h1>
        <ErrorAlert error={error} className="mb-md" />

        {loading ? (
          <Spinner />
        ) : reservations.length === 0 ? (
          <EmptyState
            icon="event_available"
            title="Hələ rezerviniz yoxdur"
            subtitle="Restoran seçib ilk rezervinizi edin — hamısı burada görünəcək."
          />
        ) : (
          <div className="space-y-md">
            {reservations.map((r) => {
              const status = statusLabels[r.status] ?? { text: r.status, cls: 'bg-surface-container text-on-surface-variant' };
              const cancellable = r.status === 'PENDING' || r.status === 'CONFIRMED';
              return (
                <div
                  key={r.id}
                  className="bg-surface-container-lowest border border-outline-variant rounded-xl p-md flex flex-col sm:flex-row sm:items-center justify-between gap-md elevation-step-1"
                >
                  <div className="flex items-start gap-md">
                    <div className="w-12 h-12 rounded-lg bg-primary-fixed flex items-center justify-center shrink-0">
                      <span className="material-symbols-outlined text-primary">restaurant</span>
                    </div>
                    <div>
                      <h3 className="font-serif text-title-lg text-on-surface">
                        {r.restaurantName ?? 'Restoran'}
                        {r.branchName && <span className="text-on-surface-variant font-sans text-body-md"> — {r.branchName}</span>}
                      </h3>
                      <div className="flex flex-wrap gap-md text-on-surface-variant text-body-md mt-xs">
                        <span className="flex items-center gap-xs">
                          <span className="material-symbols-outlined text-[18px]">calendar_today</span>
                          {r.date}
                        </span>
                        <span className="flex items-center gap-xs">
                          <span className="material-symbols-outlined text-[18px]">schedule</span>
                          {r.time}
                        </span>
                        <span className="flex items-center gap-xs">
                          <span className="material-symbols-outlined text-[18px]">groups</span>
                          {r.partySize} qonaq
                        </span>
                        {r.tableName && (
                          <span className="flex items-center gap-xs">
                            <span className="material-symbols-outlined text-[18px]">table_restaurant</span>
                            {r.tableName}
                          </span>
                        )}
                      </div>
                    </div>
                  </div>
                  <div className="flex items-center gap-md">
                    <span className={`px-sm py-1 rounded-full font-sans text-label-md ${status.cls}`}>{status.text}</span>
                    {cancellable && (
                      <button
                        onClick={() => onCancel(r.id)}
                        disabled={cancellingId === r.id}
                        className="px-md py-2 border border-outline text-error rounded-xl font-sans text-label-md hover:bg-error-container transition-colors disabled:opacity-50"
                      >
                        {cancellingId === r.id ? 'Ləğv edilir...' : 'Ləğv et'}
                      </button>
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </main>
      <Footer />
    </div>
  );
}
