import { useEffect, useState } from 'react';
import Spinner from '../../components/Spinner';
import EmptyState from '../../components/EmptyState';
import ErrorAlert from '../../components/ErrorAlert';
import { getRestaurantReservations, getBranchReservations, updateReservationStatus, createManualReservation } from '../../api/reservations';
import { getBranches, getMyRestaurant } from '../../api/restaurants';

const statusLabels = {
  PENDING: { text: 'Gözləyir', cls: 'bg-surface-container-high text-on-surface-variant' },
  CONFIRMED: { text: 'Təsdiqlənib', cls: 'bg-primary-fixed text-on-primary-fixed' },
  CANCELLED: { text: 'Ləğv edilib', cls: 'bg-error-container text-on-error-container' },
  COMPLETED: { text: 'Tamamlanıb', cls: 'bg-secondary-container text-on-secondary-container' },
  NO_SHOW: { text: 'Gəlmədi', cls: 'bg-error-container text-on-error-container' },
};

const emptyManualForm = { branchId: '', guestName: '', guestPhone: '', guestCount: 2, date: '', startTime: '', note: '' };

export default function ManagerReservations() {
  const [restaurantId, setRestaurantId] = useState(null);
  const [branches, setBranches] = useState([]);
  const [branchId, setBranchId] = useState('');
  const [date, setDate] = useState('');
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [savingStatusId, setSavingStatusId] = useState(null);

  const [manualOpen, setManualOpen] = useState(false);
  const [manualForm, setManualForm] = useState(emptyManualForm);
  const [manualSaving, setManualSaving] = useState(false);
  const [manualError, setManualError] = useState(null);

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

  const onChangeStatus = async (reservation, status) => {
    setSavingStatusId(reservation.id);
    setError(null);
    try {
      await updateReservationStatus(reservation.id, status);
      setReservations((prev) => prev.map((r) => (r.id === reservation.id ? { ...r, status } : r)));
    } catch (err) {
      setError(err);
    } finally {
      setSavingStatusId(null);
    }
  };

  const openManual = () => {
    setManualForm({ ...emptyManualForm, branchId: branchId || branches[0]?.id || '' });
    setManualError(null);
    setManualOpen(true);
  };

  const manualBranch = branches.find((b) => String(b.id) === String(manualForm.branchId));

  const onManualSubmit = async (e) => {
    e.preventDefault();
    setManualError(null);
    setManualSaving(true);
    try {
      await createManualReservation({
        restaurantId,
        branchId: manualForm.branchId,
        guestName: manualForm.guestName,
        guestPhone: manualForm.guestPhone,
        guestCount: Number(manualForm.guestCount),
        date: manualForm.date,
        startTime: manualForm.startTime,
        note: manualForm.note,
        duration: 120,
      });
      setManualOpen(false);
      load();
    } catch (err) {
      setManualError(err);
    } finally {
      setManualSaving(false);
    }
  };

  const inputClass =
    'w-full px-md py-sm bg-surface-container rounded-lg border border-outline-variant focus:ring-2 focus:ring-primary outline-none text-body-md';

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
        <div className="flex items-end gap-md flex-wrap">
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
          <button
            onClick={openManual}
            disabled={branches.length === 0}
            className="bg-primary text-on-primary font-sans text-label-md h-11 px-md rounded-xl flex items-center gap-2 hover:opacity-90 active:scale-95 transition-all disabled:opacity-50"
          >
            <span className="material-symbols-outlined text-[20px]">add_circle</span>
            Əl ilə rezerv
          </button>
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
                        {r.guestName ?? '—'}
                        {r.guestPhone && <div className="text-caption text-on-surface-variant">{r.guestPhone}</div>}
                      </td>
                      <td className="px-md py-sm text-body-md text-on-surface-variant">{r.branchName ?? '—'}</td>
                      <td className="px-md py-sm text-body-md text-on-surface-variant">{r.tableName ?? '—'}</td>
                      <td className="px-md py-sm text-body-md text-on-surface-variant whitespace-nowrap">{r.date}</td>
                      <td className="px-md py-sm text-body-md text-on-surface-variant">{r.startTime}</td>
                      <td className="px-md py-sm text-body-md text-on-surface-variant">{r.guestCount}</td>
                      <td className="px-md py-sm text-body-md text-on-surface-variant">
                        {r.source === 'MANUAL' ? 'Əl ilə' : 'Onlayn'}
                      </td>
                      <td className="px-md py-sm">
                        <select
                          value={r.status}
                          disabled={savingStatusId === r.id}
                          onChange={(e) => onChangeStatus(r, e.target.value)}
                          className={`px-sm py-1 rounded-full font-sans text-caption border-none outline-none cursor-pointer disabled:opacity-50 ${status.cls}`}
                        >
                          {Object.entries(statusLabels).map(([key, s]) => (
                            <option key={key} value={key}>{s.text}</option>
                          ))}
                        </select>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Əl ilə rezerv modalı */}
      {manualOpen && (
        <div className="fixed inset-0 z-[100] flex items-center justify-center p-md">
          <div className="absolute inset-0 bg-on-background/40 backdrop-blur-sm" onClick={() => setManualOpen(false)} />
          <div className="relative bg-surface w-full max-w-lg rounded-xl shadow-2xl overflow-hidden">
            <div className="p-md border-b border-outline-variant flex items-center justify-between bg-surface-container-low">
              <h2 className="font-sans text-title-lg">Əl ilə rezerv (zəng və s.)</h2>
              <button className="p-xs hover:bg-surface-container-highest rounded-full transition-colors" onClick={() => setManualOpen(false)}>
                <span className="material-symbols-outlined">close</span>
              </button>
            </div>
            <form onSubmit={onManualSubmit} className="p-md space-y-md">
              <ErrorAlert error={manualError} />
              <div className="space-y-xs">
                <label className="font-sans text-label-md text-on-surface-variant">Filial</label>
                <select
                  required
                  value={manualForm.branchId}
                  onChange={(e) => setManualForm({ ...manualForm, branchId: e.target.value })}
                  className={inputClass}
                >
                  <option value="">Filial seçin...</option>
                  {branches.map((b) => (
                    <option key={b.id} value={b.id}>{b.name}</option>
                  ))}
                </select>
              </div>
              <div className="grid grid-cols-2 gap-md">
                <div className="space-y-xs">
                  <label className="font-sans text-label-md text-on-surface-variant">Qonaq adı</label>
                  <input type="text" required value={manualForm.guestName} onChange={(e) => setManualForm({ ...manualForm, guestName: e.target.value })} placeholder="Ad Soyad" className={inputClass} />
                </div>
                <div className="space-y-xs">
                  <label className="font-sans text-label-md text-on-surface-variant">Telefon</label>
                  <input type="tel" required value={manualForm.guestPhone} onChange={(e) => setManualForm({ ...manualForm, guestPhone: e.target.value })} placeholder="+994 (50) 000-00-00" className={inputClass} />
                </div>
              </div>
              <div className="grid grid-cols-3 gap-md">
                <div className="space-y-xs">
                  <label className="font-sans text-label-md text-on-surface-variant">Qonaq sayı</label>
                  <input type="number" required min={1} max={50} value={manualForm.guestCount} onChange={(e) => setManualForm({ ...manualForm, guestCount: e.target.value })} className={inputClass} />
                </div>
                <div className="space-y-xs">
                  <label className="font-sans text-label-md text-on-surface-variant">Tarix</label>
                  <input type="date" required value={manualForm.date} onChange={(e) => setManualForm({ ...manualForm, date: e.target.value })} className={inputClass} />
                </div>
                <div className="space-y-xs">
                  <label className="font-sans text-label-md text-on-surface-variant">Saat</label>
                  <input
                    type="time"
                    required
                    min={manualBranch?.openingTime}
                    max={manualBranch?.closingTime}
                    value={manualForm.startTime}
                    onChange={(e) => setManualForm({ ...manualForm, startTime: e.target.value })}
                    className={inputClass}
                  />
                  {manualBranch?.openingTime && (
                    <p className="font-sans text-caption text-on-surface-variant">
                      İş saatları: {manualBranch.openingTime}–{manualBranch.closingTime}
                    </p>
                  )}
                </div>
              </div>
              <div className="space-y-xs">
                <label className="font-sans text-label-md text-on-surface-variant">Qeyd</label>
                <textarea rows={2} value={manualForm.note} onChange={(e) => setManualForm({ ...manualForm, note: e.target.value })} placeholder="Allergiya, ad günü, xüsusi istək..." className={inputClass} />
              </div>
              <div className="pt-xs flex gap-sm">
                <button type="button" onClick={() => setManualOpen(false)} className="flex-1 py-sm border border-outline text-on-surface font-sans text-label-md rounded-xl hover:bg-surface-container transition-colors">
                  Ləğv et
                </button>
                <button type="submit" disabled={manualSaving} className="flex-1 py-sm bg-primary text-on-primary font-sans text-label-md rounded-xl hover:opacity-90 transition-all disabled:opacity-50">
                  {manualSaving ? 'Yaradılır...' : 'Rezervi təsdiqlə'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
