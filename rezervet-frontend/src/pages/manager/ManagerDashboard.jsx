import { useEffect, useState } from 'react';
import Spinner from '../../components/Spinner';
import ErrorAlert from '../../components/ErrorAlert';
import { getAnalyticsSummary, getMonthlyReservationStats } from '../../api/manager';

// Manager əsas səhifə — analitika xülasəsi (dizayndakı "Branch Performance Overview")
export default function ManagerDashboard() {
  const [summary, setSummary] = useState(null);
  const [monthly, setMonthly] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [noAiAccess, setNoAiAccess] = useState(false);

  useEffect(() => {
    Promise.all([getAnalyticsSummary(), getMonthlyReservationStats()])
      .then(([s, m]) => {
        setSummary(s.data);
        setMonthly(m.data?.content ?? m.data ?? []);
      })
      .catch((err) => {
        // AI analitika planda bağlıdırsa (aiAnalysisLevel < 1) backend 403 qaytarır —
        // bu, xəta deyil, sadəcə statistika bölməsi bu planda yoxdur.
        if (err?.response?.status === 403) {
          setNoAiAccess(true);
        } else {
          setError(err);
        }
      })
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <Spinner />;

  const maxCount = Math.max(1, ...monthly.map((m) => m.count ?? 0));

  const stats = [
    { label: 'Ümumi tutum', value: summary?.totalCapacity != null ? `${summary.totalCapacity} yer` : '—' },
    { label: 'Aktiv filiallar', value: summary?.branchCount ?? '—' },
    { label: 'Ümumi masa', value: summary?.tableCount ?? '—' },
    { label: 'Bu ay rezerv', value: summary?.monthlyReservations ?? '—' },
  ];

  return (
    <div className="space-y-gutter">
      <header className="flex justify-between items-end">
        <div className="space-y-base">
          <nav className="flex items-center gap-2 text-on-surface-variant opacity-60">
            <span className="font-sans text-label-md">Panel</span>
            <span className="material-symbols-outlined text-sm">chevron_right</span>
            <span className="font-sans text-label-md">İdarəetmə</span>
          </nav>
          <h2 className="font-serif text-headline-md text-on-background">Ümumi baxış</h2>
        </div>
      </header>

      <ErrorAlert error={error} />

      {noAiAccess ? (
        <div className="bg-surface-container border border-outline-variant rounded-xl p-md flex items-center gap-md">
          <span className="material-symbols-outlined text-primary text-[28px]">lock</span>
          <div>
            <p className="font-sans text-title-lg text-on-surface">Statistika bu paketdə yoxdur</p>
            <p className="font-sans text-body-md text-on-surface-variant">
              Ümumi tutum, filial/masa sayı və rezerv statistikasını görmək üçün AI analitika daxil olan bir paketə keçin.
            </p>
          </div>
        </div>
      ) : (
        <>
          {/* Statistika kartları */}
          <div className="grid grid-cols-2 lg:grid-cols-4 gap-md">
            {stats.map((s) => (
              <div key={s.label} className="bg-surface border border-outline-variant rounded-xl p-md">
                <p className="font-sans text-label-md text-on-surface-variant">{s.label}</p>
                <h5 className="font-serif text-headline-md text-primary">{s.value}</h5>
                {s.label === 'Bu ay rezerv' && summary?.reservationsGrowthPercent != null && (
                  <p className={`font-sans text-caption mt-xs flex items-center gap-xs ${summary.reservationsGrowthPercent >= 0 ? 'text-green-600' : 'text-error'}`}>
                    <span className="material-symbols-outlined text-[16px]">
                      {summary.reservationsGrowthPercent >= 0 ? 'trending_up' : 'trending_down'}
                    </span>
                    {summary.reservationsGrowthPercent >= 0 ? '+' : ''}{summary.reservationsGrowthPercent}% keçən aya nisbətən
                  </p>
                )}
              </div>
            ))}
          </div>

          {/* Aylıq rezerv qrafiki */}
          <section className="bg-surface-container border border-outline-variant rounded-xl overflow-hidden">
            <div className="flex items-center justify-between px-6 py-4 bg-surface-container-high border-b border-outline-variant">
              <h3 className="font-sans text-title-lg">Aylıq rezerv statistikası</h3>
            </div>
            {monthly.length === 0 ? (
              <p className="p-md text-on-surface-variant text-body-md">
                Hələ statistika yoxdur — rezervlər gəldikcə burada görünəcək.
              </p>
            ) : (
              <div className="p-6 h-64 flex items-end justify-between gap-4">
                {monthly.map((m, i) => (
                  <div key={i} className="flex-1 flex flex-col items-center gap-2 group">
                    <div
                      className="w-full bg-primary/40 rounded-t-lg transition-all hover:bg-primary/70 cursor-help relative"
                      style={{ height: `${Math.round(((m.count ?? 0) / maxCount) * 100)}%`, minHeight: '4px' }}
                    >
                      <div className="absolute -top-10 left-1/2 -translate-x-1/2 bg-on-background text-background px-2 py-1 rounded text-xs opacity-0 group-hover:opacity-100 transition-opacity whitespace-nowrap">
                        {m.label}: {m.count} rezerv
                      </div>
                    </div>
                    <span className="font-sans text-label-md text-on-surface-variant">{m.label}</span>
                  </div>
                ))}
              </div>
            )}
          </section>
        </>
      )}
    </div>
  );
}
