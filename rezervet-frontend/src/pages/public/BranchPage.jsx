import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import PublicNavbar from '../../components/PublicNavbar';
import Footer from '../../components/Footer';
import Spinner from '../../components/Spinner';
import ErrorAlert from '../../components/ErrorAlert';
import { getBranch } from '../../api/restaurants';

// Filialın öz (public) səhifəsi — şəkillər, ünvan, iş saatları, Google Maps linki.
// Login tələb olunmur — hər kəs görə bilər.
export default function BranchPage() {
  const { id } = useParams();
  const [branch, setBranch] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [activePhoto, setActivePhoto] = useState(0);

  useEffect(() => {
    setLoading(true);
    getBranch(id)
      .then((res) => setBranch(res.data ?? null))
      .catch((err) => setError(err))
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) return <Spinner full />;

  return (
    <div className="min-h-screen flex flex-col bg-background">
      <PublicNavbar />

      <main className="px-gutter py-lg max-w-container-max mx-auto w-full flex-1 space-y-lg">
        <ErrorAlert error={error} />

        {branch && (
          <>
            <nav className="flex items-center gap-2 text-on-surface-variant opacity-60 font-sans text-label-md">
              <Link to={`/restoran/${branch.restaurantId}`} className="hover:text-primary transition-colors">
                {branch.restaurantName}
              </Link>
              <span className="material-symbols-outlined text-sm">chevron_right</span>
              <span>{branch.branchName}</span>
            </nav>

            {/* Şəkil qalereyası */}
            {branch.photosUrl?.length > 0 ? (
              <div className="space-y-sm">
                <div className="h-72 md:h-96 rounded-xl overflow-hidden bg-surface-container-highest">
                  <img src={branch.photosUrl[activePhoto]} alt={branch.branchName} className="w-full h-full object-cover" />
                </div>
                {branch.photosUrl.length > 1 && (
                  <div className="flex gap-sm overflow-x-auto pb-xs">
                    {branch.photosUrl.map((url, i) => (
                      <button
                        key={i}
                        onClick={() => setActivePhoto(i)}
                        className={`w-20 h-20 shrink-0 rounded-lg overflow-hidden border-2 transition-colors ${
                          activePhoto === i ? 'border-primary' : 'border-transparent'
                        }`}
                      >
                        <img src={url} alt="" className="w-full h-full object-cover" />
                      </button>
                    ))}
                  </div>
                )}
              </div>
            ) : (
              <div className="h-72 md:h-96 rounded-xl bg-surface-container-highest flex items-center justify-center">
                <span className="material-symbols-outlined text-primary/30 text-[64px]">storefront</span>
              </div>
            )}

            <div className="grid grid-cols-1 md:grid-cols-3 gap-lg">
              <div className="md:col-span-2 space-y-md">
                <h1 className="font-serif text-headline-md text-on-background">{branch.branchName}</h1>

                {(branch.district || branch.address) && (
                  <p className="text-on-surface-variant text-body-lg flex items-start gap-xs">
                    <span className="material-symbols-outlined text-[20px] mt-0.5">location_on</span>
                    {[branch.district, branch.address].filter(Boolean).join(', ')}
                  </p>
                )}

                {branch.workingHours && (
                  <p className="text-on-surface-variant text-body-lg flex items-center gap-xs">
                    <span className="material-symbols-outlined text-[20px]">schedule</span>
                    {branch.workingHours}
                  </p>
                )}

                {!!(branch.minTableCapacity || branch.maxTableCapacity) && (
                  <p className="text-on-surface-variant text-body-lg flex items-center gap-xs">
                    <span className="material-symbols-outlined text-[20px]">table_restaurant</span>
                    {branch.minTableCapacity}–{branch.maxTableCapacity} nəfərlik masalar
                  </p>
                )}
              </div>

              <div className="space-y-md">
                {branch.location && (
                  <a
                    href={`https://www.google.com/maps?q=${branch.location.lat},${branch.location.lon}`}
                    target="_blank"
                    rel="noreferrer"
                    className="w-full flex items-center justify-center gap-xs px-lg py-sm bg-primary text-on-primary rounded-xl font-sans text-label-md hover:opacity-90 active:scale-95 transition-all"
                  >
                    <span className="material-symbols-outlined text-[20px]">map</span>
                    Google Maps-də aç
                  </a>
                )}
                <Link
                  to={`/restoran/${branch.restaurantId}`}
                  className="w-full flex items-center justify-center gap-xs px-lg py-sm border border-outline text-on-surface rounded-xl font-sans text-label-md hover:bg-surface-container transition-colors"
                >
                  Restoran səhifəsinə qayıt
                </Link>
              </div>
            </div>
          </>
        )}
      </main>

      <Footer />
    </div>
  );
}
