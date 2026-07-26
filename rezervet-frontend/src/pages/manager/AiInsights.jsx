import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Spinner from '../../components/Spinner';
import ErrorAlert from '../../components/ErrorAlert';
import EmptyState from '../../components/EmptyState';
import { getAiAccess, getAiSummary, getAiForecast, getAiInsights } from '../../api/ai';

const WEEKDAY_LABELS = {
  MONDAY: 'B.e', TUESDAY: 'Ç.a', WEDNESDAY: 'Çərşənbə',
  THURSDAY: 'C.a', FRIDAY: 'Cümə', SATURDAY: 'Şənbə', SUNDAY: 'Bazar',
};

// Manager: AI analitika — səviyyəyə görə (0=bağlı, 1=əsas statistika, 2=+proqnoz/tövsiyə/LLM)
export default function AiInsights() {
  const navigate = useNavigate();
  const [access, setAccess] = useState(null);
  const [summary, setSummary] = useState(null);
  const [forecast, setForecast] = useState(null);
  const [insights, setInsights] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    let active = true;
    (async () => {
      try {
        const { data: acc } = await getAiAccess();
        if (!active) return;
        setAccess(acc);

        if (acc?.canBasic) {
          const { data: s } = await getAiSummary();
          if (active) setSummary(s);
        }
        if (acc?.canAdvanced) {
          const [{ data: f }, { data: i }] = await Promise.all([getAiForecast(), getAiInsights()]);
          if (active) { setForecast(f); setInsights(i); }
        }
      } catch (err) {
        if (active) setError(err);
      } finally {
        if (active) setLoading(false);
      }
    })();
    return () => { active = false; };
  }, []);

  if (loading) return <Spinner />;

  return (
    <div className="space-y-gutter">
      <header className="space-y-base">
        <nav className="flex items-center gap-2 text-on-surface-variant opacity-60">
          <span className="font-sans text-label-md">Panel</span>
          <span className="material-symbols-outlined text-sm">chevron_right</span>
          <span className="font-sans text-label-md">AI Analitika</span>
        </nav>
        <h2 className="font-serif text-headline-md text-on-background">AI Analitika</h2>
      </header>

      <ErrorAlert error={error} />

      {/* Səviyyə 0 — bağlıdır */}
      {access && !access.canBasic && (
        <div className="bg-primary-container text-on-primary-container rounded-xl p-lg flex flex-col items-center text-center gap-md">
          <span className="material-symbols-outlined text-[48px]">lock</span>
          <div>
            <h3 className="font-serif text-title-lg">AI analitika bu paketdə yoxdur</h3>
            <p className="font-sans text-body-md opacity-90 mt-xs">
              Doluluq, peak saatlar, proqnoz və LLM tövsiyələri üçün paketinizi yüksəldin.
            </p>
          </div>
          <button
            onClick={() => navigate('/manager/abune')}
            className="px-lg py-3 bg-surface text-primary rounded-xl font-sans text-label-md hover:opacity-90 transition-all"
          >
            Paketləri gör
          </button>
        </div>
      )}

      {/* Səviyyə 1+ — əsas statistika */}
      {summary && (
        <>
          <div className="grid grid-cols-2 lg:grid-cols-4 gap-md">
            <StatCard label="Ümumi rezerv" value={summary.totalReservations} />
            <StatCard label="Ümumi tutum" value={summary.totalCapacity != null ? `${summary.totalCapacity} yer` : '—'} />
            <StatCard label="Orta doluluq" value={`${summary.avgOccupancyPercent ?? 0}%`} />
            <StatCard label="İmtina dərəcəsi" value={`${Math.round((summary.noShowRate ?? 0) * 100)}%`} />
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-2 gap-md">
            {/* Peak saatlar */}
            <section className="bg-surface-container border border-outline-variant rounded-xl overflow-hidden">
              <div className="px-6 py-4 bg-surface-container-high border-b border-outline-variant">
                <h3 className="font-sans text-title-lg">Peak saatlar</h3>
              </div>
              {Object.keys(summary.peakHours ?? {}).length === 0 ? (
                <p className="p-md text-on-surface-variant text-body-md">Hələ kifayət qədər data yoxdur.</p>
              ) : (
                <div className="p-6 h-48 flex items-end justify-between gap-1">
                  {Object.entries(summary.peakHours).map(([hour, count]) => {
                    const max = Math.max(1, ...Object.values(summary.peakHours));
                    return (
                      <div key={hour} className="flex-1 flex flex-col items-center gap-1 group">
                        <div
                          className="w-full bg-primary/50 rounded-t hover:bg-primary/80 transition-all relative"
                          style={{ height: `${Math.round((count / max) * 100)}%`, minHeight: '4px' }}
                        >
                          <div className="absolute -top-8 left-1/2 -translate-x-1/2 bg-on-background text-background px-2 py-0.5 rounded text-caption opacity-0 group-hover:opacity-100 transition-opacity whitespace-nowrap">
                            {count}
                          </div>
                        </div>
                        <span className="font-sans text-caption text-on-surface-variant">{hour}</span>
                      </div>
                    );
                  })}
                </div>
              )}
            </section>

            {/* Kanal bölgüsü */}
            <section className="bg-surface-container border border-outline-variant rounded-xl overflow-hidden">
              <div className="px-6 py-4 bg-surface-container-high border-b border-outline-variant">
                <h3 className="font-sans text-title-lg">Rezerv mənbəyi</h3>
              </div>
              {Object.keys(summary.channelSplit ?? {}).length === 0 ? (
                <p className="p-md text-on-surface-variant text-body-md">Hələ kifayət qədər data yoxdur.</p>
              ) : (
                <div className="p-6 space-y-sm">
                  {Object.entries(summary.channelSplit).map(([channel, count]) => {
                    const total = Object.values(summary.channelSplit).reduce((a, b) => a + b, 0);
                    const pct = total > 0 ? Math.round((count / total) * 100) : 0;
                    return (
                      <div key={channel}>
                        <div className="flex justify-between font-sans text-body-md mb-1">
                          <span>{channel === 'ONLINE' ? 'Onlayn' : channel === 'MANUAL' ? 'Əl ilə (zəng)' : channel}</span>
                          <span className="text-on-surface-variant">{count} ({pct}%)</span>
                        </div>
                        <div className="w-full bg-surface-container-high rounded-full h-2">
                          <div className="bg-primary h-2 rounded-full" style={{ width: `${pct}%` }} />
                        </div>
                      </div>
                    );
                  })}
                </div>
              )}
            </section>
          </div>

          {/* Aylıq trend */}
          <section className="bg-surface-container border border-outline-variant rounded-xl overflow-hidden">
            <div className="px-6 py-4 bg-surface-container-high border-b border-outline-variant">
              <h3 className="font-sans text-title-lg">Aylıq rezerv trendi</h3>
            </div>
            {(summary.monthlyTrend ?? []).length === 0 ? (
              <p className="p-md text-on-surface-variant text-body-md">Hələ statistika yoxdur.</p>
            ) : (
              <div className="p-6 h-56 flex items-end justify-between gap-4">
                {summary.monthlyTrend.map((m, i) => {
                  const max = Math.max(1, ...summary.monthlyTrend.map((x) => x.count ?? 0));
                  return (
                    <div key={i} className="flex-1 flex flex-col items-center gap-2 group">
                      <div
                        className="w-full bg-primary/40 rounded-t-lg hover:bg-primary/70 transition-all relative"
                        style={{ height: `${Math.round(((m.count ?? 0) / max) * 100)}%`, minHeight: '4px' }}
                      >
                        <div className="absolute -top-10 left-1/2 -translate-x-1/2 bg-on-background text-background px-2 py-1 rounded text-xs opacity-0 group-hover:opacity-100 transition-opacity whitespace-nowrap">
                          {m.label}: {m.count} rezerv
                        </div>
                      </div>
                      <span className="font-sans text-label-md text-on-surface-variant">{m.label}</span>
                    </div>
                  );
                })}
              </div>
            )}
          </section>
        </>
      )}

      {/* Səviyyə 1-də, 2 olmadıqda — yüksəlt təklifi */}
      {access?.canBasic && !access?.canAdvanced && (
        <div className="bg-surface-container-high border border-outline-variant rounded-xl p-lg flex items-center justify-between gap-md flex-wrap">
          <div className="flex items-center gap-md">
            <span className="material-symbols-outlined text-primary text-[32px]">auto_awesome</span>
            <div>
              <h3 className="font-sans text-title-lg">Proqnoz və LLM tövsiyələri yüksək paketdədir</h3>
              <p className="font-sans text-body-md text-on-surface-variant">Növbəti günlərin tələb proqnozu və süni intellekt tövsiyələri üçün paketi yüksəldin.</p>
            </div>
          </div>
          <button
            onClick={() => navigate('/manager/abune')}
            className="px-md py-2 border border-primary text-primary rounded-xl font-sans text-label-md hover:bg-primary-fixed transition-colors shrink-0"
          >
            Yüksəlt
          </button>
        </div>
      )}

      {/* Səviyyə 2 — proqnoz */}
      {forecast && (
        <section className="bg-surface-container border border-outline-variant rounded-xl overflow-hidden">
          <div className="px-6 py-4 bg-surface-container-high border-b border-outline-variant">
            <h3 className="font-sans text-title-lg">Növbəti 7 gün — tələb proqnozu</h3>
          </div>
          {(forecast.days ?? []).length === 0 ? (
            <EmptyState icon="query_stats" title="Proqnoz üçün kifayət qədər data yoxdur" />
          ) : (
            <div className="p-6 grid grid-cols-2 sm:grid-cols-4 lg:grid-cols-7 gap-sm">
              {forecast.days.map((d, i) => (
                <div key={i} className="bg-surface rounded-lg border border-outline-variant p-sm text-center">
                  <p className="font-sans text-caption text-on-surface-variant">{WEEKDAY_LABELS[d.weekday] ?? d.weekday}</p>
                  <p className="font-sans text-caption text-on-surface-variant">{d.date}</p>
                  <p className="font-serif text-title-lg text-primary mt-xs">{d.expectedReservations}</p>
                </div>
              ))}
            </div>
          )}
        </section>
      )}

      {/* Səviyyə 2 — insight/LLM */}
      {insights && (
        <section className="bg-inverse-surface text-inverse-on-surface rounded-xl p-lg space-y-md">
          <div className="flex items-center gap-sm">
            <span className="material-symbols-outlined text-primary-fixed">auto_awesome</span>
            <h3 className="font-serif text-title-lg">
              {insights.llmGenerated ? 'AI hesabatı' : 'Analitik xülasə'}
            </h3>
          </div>
          <p className="font-sans text-body-md opacity-90 whitespace-pre-line">{insights.narrative}</p>
          {(insights.recommendations ?? []).length > 0 && (
            <ul className="space-y-2">
              {insights.recommendations.map((r, i) => (
                <li key={i} className="flex items-start gap-sm font-sans text-body-md">
                  <span className="material-symbols-outlined text-primary-fixed text-[20px] mt-0.5">check_circle</span>
                  <span>{r}</span>
                </li>
              ))}
            </ul>
          )}
        </section>
      )}
    </div>
  );
}

function StatCard({ label, value }) {
  return (
    <div className="bg-surface border border-outline-variant rounded-xl p-md">
      <p className="font-sans text-label-md text-on-surface-variant">{label}</p>
      <h5 className="font-serif text-headline-md text-primary">{value}</h5>
    </div>
  );
}
