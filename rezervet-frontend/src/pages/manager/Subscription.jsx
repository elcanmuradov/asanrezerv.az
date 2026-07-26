import { useEffect, useState } from 'react';
import Spinner from '../../components/Spinner';
import EmptyState from '../../components/EmptyState';
import ErrorAlert from '../../components/ErrorAlert';
import { getPlans, getCurrentSubscription, startCheckout } from '../../api/subscriptions';
import { getMyRestaurant } from '../../api/restaurants';

const statusLabels = {
  ACTIVE: { text: 'Aktiv', cls: 'bg-primary-fixed text-on-primary-fixed' },
  PAST_DUE: { text: 'Ödəniş gecikir', cls: 'bg-error-container text-on-error-container' },
  CANCELLED: { text: 'Ləğv edilib', cls: 'bg-surface-container-high text-on-surface-variant' },
  PENDING: { text: 'Gözləyir', cls: 'bg-surface-container-high text-on-surface-variant' },
};

// Manager: cari abunə + paket seçimi (checkout abunəni yaradır/yeniləyir)
export default function Subscription() {
  const [restaurantId, setRestaurantId] = useState(null);
  const [current, setCurrent] = useState(null);
  const [plans, setPlans] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [checkoutId, setCheckoutId] = useState(null);
  const [autoRenew, setAutoRenew] = useState(true);

  useEffect(() => {
    let active = true;
    (async () => {
      try {
        // Cari abunə və checkout üçün restaurant-id lazımdır — əvvəlcə restoranı al
        const restRes = await getMyRestaurant();
        const rid = restRes.data?.id;
        if (active) setRestaurantId(rid);
        const [c, p] = await Promise.all([
          rid
            ? getCurrentSubscription(rid).catch(() => ({ data: null })) // abunə olmaya bilər
            : Promise.resolve({ data: null }),
          getPlans(),
        ]);
        if (!active) return;
        setCurrent(c.data);
        setPlans(p.data?.content ?? p.data ?? []);
      } catch (err) {
        if (active) setError(err);
      } finally {
        if (active) setLoading(false);
      }
    })();
    return () => { active = false; };
  }, []);

  const onCheckout = async (planId) => {
    if (!restaurantId) return;
    setError(null);
    setCheckoutId(planId);
    try {
      // Backend yaradılmış/yenilənmiş abunəni (SubscriptionDto) qaytarır
      const { data } = await startCheckout(restaurantId, planId, autoRenew);
      setCurrent(data);
    } catch (err) {
      setError(err);
    } finally {
      setCheckoutId(null);
    }
  };

  if (loading) return <Spinner />;

  const status = current ? (statusLabels[current.status] ?? { text: current.status, cls: 'bg-surface-container text-on-surface-variant' }) : null;

  return (
    <div className="space-y-gutter">
      <header className="space-y-base">
        <nav className="flex items-center gap-2 text-on-surface-variant opacity-60">
          <span className="font-sans text-label-md">Panel</span>
          <span className="material-symbols-outlined text-sm">chevron_right</span>
          <span className="font-sans text-label-md">Abunə</span>
        </nav>
        <h2 className="font-serif text-headline-md text-on-background">Abunə paketi</h2>
      </header>

      <ErrorAlert error={error} />

      {/* Cari abunə */}
      {current ? (
        <div className="bg-inverse-surface text-inverse-on-surface rounded-xl p-md flex flex-col md:flex-row md:items-center justify-between gap-md">
          <div>
            <p className="font-sans text-label-md opacity-80 uppercase tracking-wider">Cari paket</p>
            <h3 className="font-serif text-headline-md">{current.planName}</h3>
            {current.currentPeriodEnd && (
              <p className="font-sans text-body-md opacity-80 mt-xs">
                Növbəti ödəniş: {current.currentPeriodEnd}
              </p>
            )}
            <p className="font-sans text-caption opacity-70 mt-xs flex items-center gap-xs">
              <span className="material-symbols-outlined text-[16px]">
                {current.autoRenew ? 'autorenew' : 'event_busy'}
              </span>
              {current.autoRenew ? 'Avtomatik yenilənir' : 'Avtomatik yenilənmə bağlıdır'}
            </p>
          </div>
          {status && <span className={`px-md py-2 rounded-full font-sans text-label-md self-start ${status.cls}`}>{status.text}</span>}
        </div>
      ) : (
        <div className="bg-surface-container-high rounded-xl p-md flex items-center gap-md">
          <span className="material-symbols-outlined text-primary text-[40px]">info</span>
          <p className="font-sans text-body-md text-on-surface">
            Hazırda aktiv abunəniz yoxdur. Restoranınızın platformada görünməsi üçün paket seçin.
          </p>
        </div>
      )}

      {/* Avtomatik yenilənmə seçimi — paket seçiləndə tətbiq olunur */}
      <label className="flex items-center gap-sm p-md bg-surface-container-low border border-outline-variant rounded-xl cursor-pointer w-fit">
        <input
          type="checkbox"
          checked={autoRenew}
          onChange={(e) => setAutoRenew(e.target.checked)}
          className="rounded text-primary focus:ring-primary"
        />
        <span className="font-sans text-body-md text-on-surface">
          Paket müddəti bitəndə avtomatik yenilənsin
        </span>
      </label>

      {/* Paketlər */}
      {plans.length === 0 ? (
        <EmptyState icon="workspace_premium" title="Paketlər hazırlanır" />
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-md items-stretch">
          {plans.map((plan) => {
            const isCurrent = current?.planId === plan.id;
            return (
              <div
                key={plan.id}
                className={`bg-surface-container-lowest p-lg rounded-xl flex flex-col relative ${
                  plan.popular ? 'border-2 border-primary shadow-xl' : 'border border-outline-variant'
                }`}
              >
                {plan.popular && (
                  <div className="absolute -top-4 left-1/2 -translate-x-1/2 bg-primary text-on-primary px-4 py-1 rounded-full font-sans text-caption uppercase tracking-wider">
                    Ən populyar
                  </div>
                )}
                <div className="mb-lg">
                  <h3 className="font-sans text-title-lg mb-2">{plan.name}</h3>
                  <div className="flex items-baseline gap-1">
                    <span className="font-serif text-headline-md text-on-background">
                      {plan.monthlyPrice != null ? `${plan.monthlyPrice} ₼` : '—'}
                    </span>
                    <span className="text-on-surface-variant font-sans text-body-md">/ay</span>
                  </div>
                </div>
                <ul className="space-y-4 mb-lg flex-grow">
                  {(plan.features ?? []).map((f, i) => (
                    <li key={i} className="flex items-center gap-3 font-sans text-body-md text-on-surface-variant">
                      <span className="material-symbols-outlined text-primary text-[20px]">check_circle</span>
                      {f}
                    </li>
                  ))}
                </ul>
                <button
                  onClick={() => onCheckout(plan.id)}
                  disabled={isCurrent || checkoutId !== null}
                  className={`w-full py-4 rounded-xl font-sans text-label-md transition-all ${
                    isCurrent
                      ? 'bg-surface-container text-on-surface-variant cursor-default'
                      : 'bg-primary text-on-primary shadow-md hover:opacity-90 disabled:opacity-50'
                  }`}
                >
                  {isCurrent ? 'Cari paketiniz' : checkoutId === plan.id ? 'Aktivləşdirilir...' : 'Bu paketi seç'}
                </button>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}
