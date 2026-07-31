import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import PublicNavbar from '../../components/PublicNavbar';
import Footer from '../../components/Footer';
import EmptyState from '../../components/EmptyState';
import Spinner from '../../components/Spinner';
import ErrorAlert from '../../components/ErrorAlert';
import { getRestaurants } from '../../api/restaurants';

// Login olmayan istifadəçilər üçün əsas səhifə — restoranların siyahısı
export default function Home() {
  const [restaurants, setRestaurants] = useState([]);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const load = (params = {}) => {
    setLoading(true);
    setError(null);
    getRestaurants(params)
      .then((res) => setRestaurants(res.data?.content ?? res.data ?? []))
      .catch((err) => setError(err))
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  const onSearch = (e) => {
    e.preventDefault();
    load(search ? { search } : {});
  };

  return (
    <div className="min-h-screen flex flex-col bg-background">
      <PublicNavbar />

      {/* Hero + axtarış */}
      <section className="relative pt-xl pb-lg px-gutter overflow-hidden">
        <div className="max-w-container-max mx-auto">
          <div className="flex flex-col gap-base mb-lg text-center md:text-left">
            <div className="inline-flex items-center self-center md:self-start px-4 py-1.5 rounded-full bg-surface-container-high text-primary font-sans text-label-md mb-2">
              <span className="material-symbols-outlined filled text-[18px] mr-2">stars</span>
              Unudulmaz axşamlar üçün
            </div>
            <h1 className="font-serif text-display-lg-mobile md:text-display-lg text-on-background max-w-2xl">
              Mükəmməl masanızı <span className="text-primary italic">bir kliklə</span> rezerv edin.
            </h1>
            <p className="text-on-surface-variant font-sans text-body-lg max-w-xl">
              Şəhərin ən yaxşı restoranlarında onlayn rezervasiya — sürətli, rahat və pulsuz.
            </p>
          </div>

          <form
            onSubmit={onSearch}
            className="bg-surface-container-low rounded-xl border border-outline-variant p-md shadow-sm flex flex-col sm:flex-row gap-md items-end"
          >
            <div className="flex flex-col gap-xs flex-1 w-full">
              <label className="font-sans text-label-md text-on-surface-variant px-xs">Restoran axtar</label>
              <div className="relative">
                <span className="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-outline">search</span>
                <input
                  type="text"
                  value={search}
                  onChange={(e) => setSearch(e.target.value)}
                  placeholder="Restoran adı və ya mətbəx növü..."
                  className="w-full pl-10 pr-md py-sm bg-surface-container rounded-lg border-none focus:ring-2 focus:ring-primary text-body-md text-on-surface outline-none"
                />
              </div>
            </div>
            <button
              type="submit"
              className="w-full sm:w-auto h-[52px] px-lg bg-primary text-on-primary rounded-lg font-sans text-title-lg hover:opacity-90 transition-all flex items-center justify-center gap-sm shadow-md active:scale-95"
            >
              Axtar
            </button>
          </form>
        </div>
        <div className="absolute -bottom-24 -right-24 w-96 h-96 bg-primary-fixed/30 blur-[120px] rounded-full -z-10" />
      </section>

      {/* Restoran siyahısı */}
      <section className="pb-xl px-gutter max-w-container-max mx-auto w-full flex-1">
        <div className="flex items-center justify-between mb-md border-b border-outline-variant pb-base">
          <div className="flex items-center gap-sm">
            <h2 className="font-serif text-headline-md text-on-background">Restoranlar</h2>
            {!loading && (
              <span className="bg-secondary-container text-on-secondary-container px-sm py-xs rounded-full font-sans text-label-md">
                {restaurants.length} nəticə
              </span>
            )}
          </div>
        </div>

        <ErrorAlert error={error} className="mb-md" />

        {loading ? (
          <Spinner />
        ) : restaurants.length === 0 ? (
          <EmptyState
            icon="restaurant"
            title="Hələlik restoran yoxdur"
            subtitle="Yeni restoranlar əlavə olunduqca burada görünəcək."
          />
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-md">
            {restaurants.map((r) => (
              <Link
                key={r.id}
                to={`/restoran/${r.id}`}
                className="group bg-surface-container-low rounded-xl overflow-hidden border border-outline-variant hover:shadow-lg transition-all duration-300 flex flex-col"
              >
                <div className="relative h-48 overflow-hidden bg-surface-container-highest">
                  {(r.bannerUrl || r.profilePhotoUrl || r.mediaAssets?.bannerUrl || r.mediaAssets?.profilePhotoUrl || r.coverImageUrl) ? (
                    <img
                      src={r.bannerUrl || r.profilePhotoUrl || r.mediaAssets?.bannerUrl || r.mediaAssets?.profilePhotoUrl || r.coverImageUrl}
                      alt={r.name}
                      className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-700"
                    />
                  ) : (
                    <div className="w-full h-full flex items-center justify-center">
                      <span className="material-symbols-outlined text-primary/30 text-[64px]">restaurant</span>
                    </div>
                  )}
                  {(r.featured || (r.visibilityLevel ?? 0) > 0) && (
                    <div className="absolute top-4 left-4 bg-primary text-on-primary px-sm py-xs rounded-full font-sans text-label-md">
                      Seçilmiş
                    </div>
                  )}
                </div>
                <div className="p-md flex flex-col flex-1">
                  <h3 className="font-serif text-title-lg text-on-background">{r.name}</h3>
                  <div className="flex items-center gap-base text-on-surface-variant text-body-md mt-xs">
                    {r.rating != null && (
                      <span className="flex items-center gap-xs">
                        <span className="material-symbols-outlined filled text-primary text-[18px]">star</span>
                        {r.rating}
                      </span>
                    )}
                    {r.cuisine && <span>{r.cuisine}</span>}
                    {r.city && <span>• {r.city}</span>}
                  </div>
                  {r.description && (
                    <p className="text-on-surface-variant text-body-md mt-sm line-clamp-2">{r.description}</p>
                  )}
                  <div className="mt-auto pt-md flex justify-between items-center">
                    <span className="text-on-surface-variant text-caption">
                      {r.branchCount != null ? `${r.branchCount} filial` : ''}
                    </span>
                    <span className="text-primary font-sans text-label-md flex items-center group-hover:gap-2 transition-all">
                      Ətraflı <span className="material-symbols-outlined text-[18px] ml-1">arrow_forward</span>
                    </span>
                  </div>
                </div>
              </Link>
            ))}
          </div>
        )}
      </section>

      {/* Restoran sahibləri üçün CTA */}
      <section className="py-xl px-gutter">
        <div className="max-w-container-max mx-auto bg-primary-container rounded-3xl p-xl flex flex-col items-center text-center text-on-primary-container">
          <h2 className="font-serif text-display-lg-mobile md:text-display-lg mb-6">
            Restoran sahibisiniz?
          </h2>
          <p className="font-sans text-body-lg mb-lg max-w-2xl opacity-90">
            AsanRezerv ilə masalarınızı, filiallarınızı və rezervlərinizi bir paneldən idarə edin.
          </p>
          <div className="flex flex-wrap justify-center gap-md">
            <Link to="/biznes/register" className="px-xl py-4 bg-background text-primary rounded-xl font-sans text-label-md hover:shadow-xl transition-all">
              Restoranını qeydiyyatdan keçir
            </Link>
            <Link to="/qiymetler" className="px-xl py-4 border border-on-primary-container text-on-primary-container rounded-xl font-sans text-label-md hover:bg-white/10 transition-all">
              Paketlərə bax
            </Link>
          </div>
        </div>
      </section>

      <Footer />
    </div>
  );
}
