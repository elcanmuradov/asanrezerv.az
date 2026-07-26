import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import PublicNavbar from '../../components/PublicNavbar';
import Footer from '../../components/Footer';
import Spinner from '../../components/Spinner';
import EmptyState from '../../components/EmptyState';
import ErrorAlert from '../../components/ErrorAlert';
import { getPlans } from '../../api/subscriptions';

// Restoran sahibləri üçün paketlər (Starter / Standart / Premium) — API-dən gəlir
export default function Pricing() {
  const [plans, setPlans] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    getPlans()
      .then((res) => setPlans(res.data?.content ?? res.data ?? []))
      .catch((err) => setError(err))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="min-h-screen flex flex-col bg-background">
      <PublicNavbar />

      <main className="flex-1 py-xl px-gutter bg-surface-container-low">
        <div className="max-w-container-max mx-auto">
          <div className="text-center mb-xl">
            <h1 className="font-serif text-headline-md text-on-background mb-4">Hər ölçüdə restoran üçün paketlər</h1>
            <p className="font-sans text-body-md text-on-surface-variant max-w-2xl mx-auto">
              Restoranınız böyüdükcə sizinlə birlikdə böyüyən şəffaf qiymətlər.
            </p>
          </div>

          <ErrorAlert error={error} className="mb-md max-w-2xl mx-auto" />

          {loading ? (
            <Spinner />
          ) : plans.length === 0 ? (
            <EmptyState
              icon="workspace_premium"
              title="Paketlər hazırlanır"
              subtitle="Abunə paketləri tezliklə burada görünəcək."
            />
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-3 gap-md items-stretch">
              {plans.map((plan) => (
                <div
                  key={plan.id}
                  className={`bg-surface-container-lowest p-lg rounded-xl flex flex-col transition-all group relative ${
                    plan.popular
                      ? 'border-2 border-primary shadow-xl md:scale-105 z-10'
                      : 'border border-outline-variant hover:shadow-lg'
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
                    {plan.description && (
                      <p className="font-sans text-body-md text-on-surface-variant mt-4">{plan.description}</p>
                    )}
                  </div>
                  <ul className="space-y-4 mb-lg flex-grow">
                    {(plan.features ?? []).map((f, i) => (
                      <li key={i} className="flex items-center gap-3 font-sans text-body-md text-on-surface-variant">
                        <span className={`material-symbols-outlined text-primary text-[20px] ${plan.popular ? 'filled' : ''}`}>check_circle</span>
                        {f}
                      </li>
                    ))}
                  </ul>
                  <Link
                    to="/biznes/register"
                    className={`w-full py-4 rounded-xl font-sans text-label-md text-center transition-all ${
                      plan.popular
                        ? 'bg-primary text-on-primary shadow-md hover:opacity-90'
                        : 'border border-outline text-primary group-hover:bg-primary group-hover:text-on-primary'
                    }`}
                  >
                    Başla
                  </Link>
                </div>
              ))}
            </div>
          )}
        </div>
      </main>

      <Footer />
    </div>
  );
}
