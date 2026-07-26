import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import PublicNavbar from '../../components/PublicNavbar';
import Footer from '../../components/Footer';
import Spinner from '../../components/Spinner';
import EmptyState from '../../components/EmptyState';
import ErrorAlert from '../../components/ErrorAlert';
import { getRestaurant, getRestaurantBranches } from '../../api/restaurants';
import { checkAvailability, reserveTable } from '../../api/reservations';
import { useAuth } from '../../context/AuthContext';

// Restoran səhifəsi: məlumat + filiallar + uyğunluq yoxlaması + rezerv
export default function RestaurantDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();

  const [restaurant, setRestaurant] = useState(null);
  const [branches, setBranches] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Uyğunluq axtarışı
  const [selectedBranch, setSelectedBranch] = useState('');
  const [date, setDate] = useState('');
  const [time, setTime] = useState('19:00');
  const [partySize, setPartySize] = useState(2);
  const [searched, setSearched] = useState(false);       // yoxlama edilibmi
  const [availableTableId, setAvailableTableId] = useState(null); // uyğun masanın UUID-si (yoxdursa null)
  const [searching, setSearching] = useState(false);
  const [searchError, setSearchError] = useState(null);

  // Rezerv göndərişi
  const [note, setNote] = useState('');
  const [reserving, setReserving] = useState(false);
  const [reserveError, setReserveError] = useState(null);
  const [reserved, setReserved] = useState(false);

  useEffect(() => {
    setLoading(true);
    Promise.all([getRestaurant(id), getRestaurantBranches(id)])
      .then(([r, b]) => {
        setRestaurant(r.data);
        const list = b.data?.content ?? b.data ?? [];
        setBranches(list);
        if (list.length > 0) setSelectedBranch(String(list[0].id));
      })
      .catch((err) => setError(err))
      .finally(() => setLoading(false));
  }, [id]);

  const onCheckAvailability = async (e) => {
    e.preventDefault();
    setSearchError(null);
    setReserveError(null);
    setReserved(false);
    setAvailableTableId(null);
    setSearching(true);
    try {
      // Backend uyğun masanın UUID-sini qaytarır (yoxdursa null/boş)
      const { data } = await checkAvailability({
        guestName: user?.fullName || 'Qonaq',
        guestCount: Number(partySize),
        duration: 120, // default duration (minutes)
        restaurantId: id,
        branchId: selectedBranch,
        tableId: null,
        date: date,
        startTime: time,
      });
      setAvailableTableId(data ?? null);
      setSearched(true);
    } catch (err) {
      setSearchError(err);
      setSearched(true);
    } finally {
      setSearching(false);
    }
  };

  const onReserve = async () => {
    if (!user) {
      navigate('/login', { state: { from: { pathname: `/restoran/${id}` } } });
      return;
    }
    if (!availableTableId) return;
    setReserveError(null);
    setReserving(true);
    try {
      await reserveTable({
        guestName: user?.fullName || 'Qonaq',
        guestCount: Number(partySize),
        duration: 120, // default duration (minutes)
        restaurantId: id,
        branchId: selectedBranch,
        tableId: availableTableId,
        date: date,
        startTime: time,
      });
      setReserved(true);
    } catch (err) {
      setReserveError(err);
    } finally {
      setReserving(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen flex flex-col bg-background">
        <PublicNavbar />
        <Spinner full />
      </div>
    );
  }

  if (error || !restaurant) {
    return (
      <div className="min-h-screen flex flex-col bg-background">
        <PublicNavbar />
        <div className="max-w-container-max mx-auto px-gutter py-xl w-full">
          <ErrorAlert error={error || 'Restoran tapılmadı.'} />
        </div>
        <Footer />
      </div>
    );
  }

  return (
    <div className="min-h-screen flex flex-col bg-background">
      <PublicNavbar />

      {/* Başlıq / örtük */}
      <section className="relative">
        <div className="h-72 bg-surface-container-highest overflow-hidden">
          {(restaurant.bannerUrl || restaurant.mediaAssets?.bannerUrl || restaurant.coverImageUrl) ? (
            <img src={restaurant.bannerUrl || restaurant.mediaAssets?.bannerUrl || restaurant.coverImageUrl} alt={restaurant.name} className="w-full h-full object-cover" />
          ) : (
            <div className="w-full h-full flex items-center justify-center">
              <span className="material-symbols-outlined text-primary/30 text-[96px]">restaurant</span>
            </div>
          )}
        </div>
        <div className="max-w-container-max mx-auto px-gutter">
          <div className="bg-surface-container-lowest border border-outline-variant rounded-xl p-md -mt-16 relative z-10 elevation-step-1">
            <div className="flex flex-col md:flex-row md:items-end justify-between gap-md">
              <div>
                <h1 className="font-serif text-headline-md text-on-background">{restaurant.name}</h1>
                <div className="flex flex-wrap items-center gap-base text-on-surface-variant text-body-md mt-xs">
                  {restaurant.rating != null && (
                    <span className="flex items-center gap-xs">
                      <span className="material-symbols-outlined filled text-primary text-[18px]">star</span>
                      {restaurant.rating} {restaurant.reviewCount != null && `(${restaurant.reviewCount} rəy)`}
                    </span>
                  )}
                  {restaurant.cuisine && <span>• {restaurant.cuisine}</span>}
                  {restaurant.city && <span>• {restaurant.city}</span>}
                </div>
                {restaurant.description && (
                  <p className="text-on-surface-variant text-body-md mt-sm max-w-2xl">{restaurant.description}</p>
                )}
              </div>
              <div className="flex items-center gap-base text-on-surface-variant text-body-md">
                {restaurant.phone && (
                  <span className="flex items-center gap-xs">
                    <span className="material-symbols-outlined text-primary text-[20px]">call</span>
                    {restaurant.phone}
                  </span>
                )}
              </div>
            </div>
          </div>
        </div>
      </section>

      <main className="max-w-container-max mx-auto px-gutter py-lg w-full flex-1 space-y-lg">
        {/* Uyğunluq axtarışı */}
        <section className="bg-surface-container-low rounded-xl border border-outline-variant p-md shadow-sm">
          <h2 className="font-serif text-title-lg text-on-surface mb-md">Masa rezerv et</h2>
          <form onSubmit={onCheckAvailability} className="grid grid-cols-1 md:grid-cols-4 gap-md items-end">
            <div className="flex flex-col gap-xs">
              <label className="font-sans text-label-md text-on-surface-variant px-xs">Filial</label>
              <div className="relative">
                <span className="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-outline">store</span>
                <select
                  required
                  value={selectedBranch}
                  onChange={(e) => setSelectedBranch(e.target.value)}
                  className="w-full pl-10 pr-md py-sm bg-surface-container rounded-lg border-none focus:ring-2 focus:ring-primary text-body-md text-on-surface"
                >
                  {branches.length === 0 && <option value="">Filial yoxdur</option>}
                  {branches.map((b) => (
                    <option key={b.id} value={b.id}>{b.branchName}</option>
                  ))}
                </select>
              </div>
            </div>
            <div className="flex flex-col gap-xs">
              <label className="font-sans text-label-md text-on-surface-variant px-xs">Tarix</label>
              <div className="relative">
                <span className="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-outline">calendar_today</span>
                <input
                  type="date"
                  required
                  min={new Date().toISOString().split('T')[0]}
                  value={date}
                  onChange={(e) => setDate(e.target.value)}
                  className="w-full pl-10 pr-md py-sm bg-surface-container rounded-lg border-none focus:ring-2 focus:ring-primary text-body-md text-on-surface"
                />
              </div>
            </div>
            <div className="flex flex-col gap-xs">
              <label className="font-sans text-label-md text-on-surface-variant px-xs">Saat və qonaq sayı</label>
              <div className="grid grid-cols-2 gap-sm">
                <input
                  type="time"
                  required
                  value={time}
                  onChange={(e) => setTime(e.target.value)}
                  className="w-full px-sm py-sm bg-surface-container rounded-lg border-none focus:ring-2 focus:ring-primary text-body-md text-on-surface"
                />
                <select
                  value={partySize}
                  onChange={(e) => setPartySize(e.target.value)}
                  className="w-full px-sm py-sm bg-surface-container rounded-lg border-none focus:ring-2 focus:ring-primary text-body-md text-on-surface"
                >
                  {[1, 2, 3, 4, 5, 6, 8, 10, 12].map((n) => (
                    <option key={n} value={n}>{n} qonaq</option>
                  ))}
                </select>
              </div>
            </div>
            <button
              type="submit"
              disabled={searching || branches.length === 0}
              className="w-full h-[52px] bg-primary text-on-primary rounded-lg font-sans text-title-lg hover:opacity-90 transition-all flex items-center justify-center gap-sm shadow-md active:scale-95 disabled:opacity-50"
            >
              {searching ? 'Axtarılır...' : 'Uyğunluğu yoxla'}
            </button>
          </form>

          <ErrorAlert error={searchError} className="mt-md" />

          {/* Nəticə */}
          {searched && (
            <div className="mt-md border-t border-outline-variant pt-md">
              {reserved ? (
                <div className="bg-surface-container-high rounded-xl p-md flex items-center gap-md">
                  <span className="material-symbols-outlined filled text-primary text-[40px]">check_circle</span>
                  <div>
                    <h3 className="font-serif text-title-lg text-on-surface">Rezerviniz qeydə alındı!</h3>
                    <p className="text-on-surface-variant text-body-md">
                      Təsdiq barədə bildiriş alacaqsınız. Rezervlərinizi{' '}
                      <button onClick={() => navigate('/rezervlerim')} className="text-primary font-sans text-label-md hover:underline">buradan</button>{' '}
                      izləyə bilərsiniz.
                    </p>
                  </div>
                </div>
              ) : !availableTableId ? (
                <EmptyState
                  icon="event_busy"
                  title="Uyğun masa tapılmadı"
                  subtitle="Başqa tarix, saat və ya qonaq sayı ilə yenidən yoxlayın."
                />
              ) : (
                <div className="bg-surface rounded-xl border border-outline-variant p-md space-y-md">
                  <div className="flex items-center gap-md">
                    <span className="material-symbols-outlined filled text-primary text-[32px]">event_available</span>
                    <div>
                      <h3 className="font-serif text-title-lg text-on-surface">Uyğun masa var!</h3>
                      <p className="text-on-surface-variant text-body-md">
                        {date} • {time} • {partySize} qonaq
                      </p>
                    </div>
                  </div>

                  <ErrorAlert error={reserveError} />
                  {!user && (
                    <p className="text-on-surface-variant text-body-md">
                      Rezerv etmək üçün <span className="text-primary font-bold">daxil olmalısınız</span> — "Rezerv et" düyməsi sizi giriş səhifəsinə yönləndirəcək.
                    </p>
                  )}

                  <div className="space-y-xs">
                    <label className="font-sans text-label-md text-on-surface-variant">Qeyd (istəyə bağlı)</label>
                    <textarea
                      rows={2}
                      value={note}
                      onChange={(e) => setNote(e.target.value)}
                      placeholder="Allergiya, ad günü, xüsusi istək..."
                      className="w-full px-md py-sm bg-surface-container rounded-lg border border-outline-variant focus:ring-2 focus:ring-primary outline-none text-body-md"
                    />
                  </div>

                  <button
                    onClick={onReserve}
                    disabled={reserving}
                    className="w-full py-sm bg-primary text-on-primary rounded-lg font-sans text-title-lg hover:opacity-90 active:scale-95 transition-all disabled:opacity-50"
                  >
                    {reserving ? 'Göndərilir...' : 'Rezerv et'}
                  </button>
                </div>
              )}
            </div>
          )}
        </section>

        {/* Filiallar */}
        <section>
          <h2 className="font-serif text-headline-md text-on-background mb-md">Filiallar</h2>
          {branches.length === 0 ? (
            <EmptyState icon="storefront" title="Filial məlumatı yoxdur" />
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-md">
              {branches.map((b) => (
                <div key={b.id} className="bg-surface-container-low rounded-xl border border-outline-variant p-md flex items-start gap-md">
                  <div className="w-12 h-12 rounded-lg bg-primary-fixed flex items-center justify-center shrink-0">
                    <span className="material-symbols-outlined text-primary">storefront</span>
                  </div>
                  <div>
                    <h3 className="font-serif text-title-lg text-on-surface">{b.branchName}</h3>
                    {(b.district || b.address) && (
                      <p className="text-on-surface-variant text-body-md mt-xs">
                        {[b.district, b.address].filter(Boolean).join(', ')}
                      </p>
                    )}
                    <div className="flex flex-wrap gap-md text-on-surface-variant text-caption mt-sm">
                      {b.workingHours && (
                        <span className="flex items-center gap-xs">
                          <span className="material-symbols-outlined text-[16px]">schedule</span>{b.workingHours}
                        </span>
                      )}
                      {!!(b.minTableCapacity || b.maxTableCapacity) && (
                        <span className="flex items-center gap-xs">
                          <span className="material-symbols-outlined text-[16px]">table_restaurant</span>
                          {b.minTableCapacity}–{b.maxTableCapacity} nəfərlik
                        </span>
                      )}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </section>
      </main>

      <Footer />
    </div>
  );
}
