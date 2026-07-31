import { useEffect, useState } from 'react';
import Spinner from '../../components/Spinner';
import EmptyState from '../../components/EmptyState';
import ErrorAlert from '../../components/ErrorAlert';
import { getMyBranch, getWaiterTables, updateTableStatus } from '../../api/waiter';
import { createManualReservation, getBranchReservations } from '../../api/reservations';

// Masa statusları — dizayndakı rəng kodları ilə
const STATUSES = {
  AVAILABLE: { label: 'Boş', dot: 'bg-green-500', card: 'bg-surface-container-low border border-outline-variant', icon: 'check_circle' },
  OCCUPIED: { label: 'Dolu', dot: 'bg-primary', card: 'bg-primary-container border-2 border-primary text-on-primary-container', icon: 'person' },
  RESERVED: { label: 'Rezerv', dot: 'bg-orange-400', card: 'bg-surface-container-high border-2 border-orange-400', icon: 'event' },
  CLEANING: { label: 'Təmizlənir', dot: 'bg-outline', card: 'bg-surface-container border border-outline', icon: 'cleaning_services' },
};

const emptyManualForm = { guestName: '', guestPhone: '', guestCount: 2, date: '', startTime: '', tableId: '', note: '' };

// Masa forması — backend-dəki TableType-a uyğun vizual sinif
const shapeClass = (type) => {
  if (type === 'CIRCLE') return 'rounded-full';
  if (type === 'RECTANGLE') return 'rounded-2xl';
  return 'rounded-xl';
};

export default function FloorPlan() {
  const [branch, setBranch] = useState(null); // ofisiant tək filiala təyin olunur
  const branchId = branch?.id ?? '';
  const [tables, setTables] = useState([]);
  const [todayReservations, setTodayReservations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [loadingTables, setLoadingTables] = useState(false);
  const [error, setError] = useState(null);

  const [selectedTable, setSelectedTable] = useState(null); // sağ panel
  const [statusSaving, setStatusSaving] = useState(false);

  const [manualOpen, setManualOpen] = useState(false);
  const [manualForm, setManualForm] = useState(emptyManualForm);
  const [manualSaving, setManualSaving] = useState(false);
  const [manualError, setManualError] = useState(null);

  useEffect(() => {
    getMyBranch()
      .then((res) => {
        // Cavab tək BranchDto obyektidir (siyahı deyil)
        setBranch(res.data ?? null);
      })
      .catch((err) => setError(err))
      .finally(() => setLoading(false));
  }, []);

  const loadBranch = (id) => {
    if (!id) return;
    setLoadingTables(true);
    setError(null);
    // Masalar əsas sorğudur
    getWaiterTables(id)
      .then((t) => setTables(t.data?.content ?? t.data ?? []))
      .catch((err) => setError(err))
      .finally(() => setLoadingTables(false));
    // Bugünkü rezervlər — reservation-service-dən filialın rezervlərini alıb bu günə filtr et.
    // Ayrıca (uğursuzluğu masaları bloklamasın).
    const today = new Date().toISOString().split('T')[0];
    getBranchReservations(id)
      .then((r) => {
        const list = r.data?.content ?? r.data ?? [];
        setTodayReservations(list.filter((x) => x.date === today));
      })
      .catch(() => setTodayReservations([]));
  };

  useEffect(() => { loadBranch(branchId); }, [branchId]);

  const onChangeStatus = async (table, status) => {
    setStatusSaving(true);
    setError(null);
    try {
      await updateTableStatus(table.id, status);
      setTables((prev) => prev.map((t) => (t.id === table.id ? { ...t, status } : t)));
      setSelectedTable((prev) => (prev ? { ...prev, status } : prev));
    } catch (err) {
      setError(err);
    } finally {
      setStatusSaving(false);
    }
  };

  const onManualSubmit = async (e) => {
    e.preventDefault();
    setManualError(null);
    setManualSaving(true);
    try {
      await createManualReservation({
        restaurantId: branch?.restaurantId,
        branchId,
        guestName: manualForm.guestName,
        guestPhone: manualForm.guestPhone,
        guestCount: Number(manualForm.guestCount),
        date: manualForm.date,
        startTime: manualForm.startTime,
        note: manualForm.note,
        tableId: manualForm.tableId || undefined,
        duration: 120,
      });
      setManualOpen(false);
      setManualForm(emptyManualForm);
      loadBranch(branchId);
    } catch (err) {
      setManualError(err);
    } finally {
      setManualSaving(false);
    }
  };

  const counts = Object.keys(STATUSES).reduce((acc, s) => {
    acc[s] = tables.filter((t) => t.status === s).length;
    return acc;
  }, {});

  const inputClass =
    'w-full px-md py-sm bg-surface-container rounded-lg border border-outline-variant focus:ring-2 focus:ring-primary outline-none text-body-md';

  if (loading) return <Spinner />;

  if (!branch) {
    return (
      <EmptyState
        icon="storefront"
        title="Sizə filial təyin olunmayıb"
        subtitle="Menecerinizdən sizi filiala təyin etməsini xahiş edin."
      />
    );
  }

  return (
    <div className="space-y-gutter">
      {/* Başlıq + status sayğacları */}
      <header className="flex flex-wrap items-center justify-between gap-md">
        <div className="flex items-center gap-md flex-wrap">
          <h2 className="font-sans text-title-lg">Masa xəritəsi</h2>
          <div className="flex gap-xs flex-wrap">
            {Object.entries(STATUSES).map(([key, s]) => (
              <span key={key} className="px-sm py-1 bg-surface-container-highest text-on-surface rounded-full text-caption flex items-center gap-xs">
                <span className={`w-2 h-2 rounded-full ${s.dot}`} /> {counts[key]} {s.label}
              </span>
            ))}
          </div>
        </div>
        <div className="flex items-end gap-md">
          <span className="px-md py-sm bg-surface-container rounded-lg border border-outline-variant text-body-md flex items-center gap-xs">
            <span className="material-symbols-outlined text-primary text-[20px]">storefront</span>
            {branch.name}
          </span>
          <button
            onClick={() => { setManualForm(emptyManualForm); setManualError(null); setManualOpen(true); }}
            className="bg-primary text-on-primary font-sans text-label-md h-11 px-md rounded-xl flex items-center gap-2 hover:opacity-90 active:scale-95 transition-all"
          >
            <span className="material-symbols-outlined text-[20px]">add_circle</span>
            Əl ilə rezerv
          </button>
        </div>
      </header>

      <ErrorAlert error={error} />

      <div className="flex flex-col xl:flex-row gap-gutter">
        {/* Masa şəbəkəsi */}
        <section className="flex-[3]">
          {loadingTables ? (
            <Spinner />
          ) : tables.length === 0 ? (
            <EmptyState
              icon="table_restaurant"
              title="Bu filialda masa yoxdur"
              subtitle="Menecer masaları əlavə etdikdən sonra burada görünəcək."
            />
          ) : (
            <div className="floor-plan-grid border border-outline-variant rounded-xl bg-surface p-md min-h-[400px]">
              <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-md">
                {tables.map((t) => {
                  const s = STATUSES[t.status] ?? STATUSES.AVAILABLE;
                  return (
                    <button
                      key={t.id}
                      onClick={() => setSelectedTable(t)}
                      className={`${t.type === 'RECTANGLE' ? 'aspect-[2/1] col-span-2' : 'aspect-square'} ${shapeClass(t.type)} flex flex-col items-center justify-center gap-xs shadow-sm hover:scale-105 transition-transform ${s.card} ${
                        selectedTable?.id === t.id ? 'ring-2 ring-primary ring-offset-2' : ''
                      }`}
                    >
                      <span className="font-sans text-title-lg">{t.name}</span>
                      <span className="text-caption">{t.capacity} nəfərlik</span>
                      <span className="text-caption flex items-center gap-xs">
                        <span className={`w-2 h-2 rounded-full ${s.dot}`} />
                        {s.label}
                      </span>
                    </button>
                  );
                })}
              </div>
            </div>
          )}
        </section>

        {/* Sağ panel: seçilmiş masa + bugünkü rezervlər */}
        <aside className="flex-1 space-y-md min-w-[280px]">
          {/* Seçilmiş masa — status dəyişmə */}
          {selectedTable && (
            <div className="bg-surface-container-lowest border border-outline-variant rounded-xl p-md shadow-sm">
              <div className="flex items-center justify-between mb-md">
                <h3 className="font-serif text-title-lg">{selectedTable.name}</h3>
                <button onClick={() => setSelectedTable(null)} className="p-xs hover:bg-surface-container rounded-full">
                  <span className="material-symbols-outlined">close</span>
                </button>
              </div>
              <p className="font-sans text-label-md text-on-surface-variant uppercase tracking-wider mb-sm">Statusu dəyiş</p>
              <div className="grid grid-cols-2 gap-xs p-1 bg-surface-container rounded-xl">
                {Object.entries(STATUSES).map(([key, s]) => (
                  <button
                    key={key}
                    disabled={statusSaving}
                    onClick={() => onChangeStatus(selectedTable, key)}
                    className={`p-base rounded-lg flex flex-col items-center gap-xs transition-all disabled:opacity-50 ${
                      selectedTable.status === key
                        ? 'bg-primary text-on-primary shadow-sm'
                        : 'hover:bg-surface'
                    }`}
                  >
                    <span className="material-symbols-outlined text-sm">{s.icon}</span>
                    <span className="text-[10px] font-bold">{s.label}</span>
                  </button>
                ))}
              </div>
            </div>
          )}

          {/* Bugünkü rezervlər */}
          <div className="bg-surface-container-lowest border border-outline-variant rounded-xl p-md shadow-sm">
            <h3 className="font-sans text-title-lg mb-md flex items-center gap-xs">
              <span className="material-symbols-outlined text-primary">calendar_today</span>
              Bu günün rezervləri
            </h3>
            {todayReservations.length === 0 ? (
              <p className="font-sans text-body-md text-on-surface-variant">Bu gün üçün rezerv yoxdur.</p>
            ) : (
              <div className="space-y-base">
                {todayReservations.map((r) => (
                  <div key={r.id} className="flex items-center gap-md py-sm border-b border-outline-variant last:border-0">
                    <div className="font-bold text-primary">{r.startTime}</div>
                    <div className="flex-1">
                      <p className="font-sans text-label-md">{r.guestName}</p>
                      <p className="font-sans text-caption text-on-surface-variant">
                        {r.guestCount} nəfər{r.tableName ? ` • ${r.tableName}` : ''}
                        {r.source === 'MANUAL' ? ' • Əl ilə' : ''}
                      </p>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </aside>
      </div>

      {/* Əl ilə rezerv modalı — dizayndakı "Manual Reservation" */}
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
                    min={branch?.openingTime}
                    max={branch?.closingTime}
                    value={manualForm.startTime}
                    onChange={(e) => setManualForm({ ...manualForm, startTime: e.target.value })}
                    className={inputClass}
                  />
                </div>
              </div>
              {branch?.openingTime && (
                <p className="font-sans text-caption text-on-surface-variant -mt-xs">
                  İş saatları: {branch.openingTime}–{branch.closingTime}
                </p>
              )}
              <div className="space-y-xs">
                <label className="font-sans text-label-md text-on-surface-variant">Masa (istəyə bağlı)</label>
                <select value={manualForm.tableId} onChange={(e) => setManualForm({ ...manualForm, tableId: e.target.value })} className={inputClass}>
                  <option value="">Avtomatik seçilsin</option>
                  {tables.map((t) => (
                    <option key={t.id} value={t.id}>{t.name} ({t.capacity} nəfərlik)</option>
                  ))}
                </select>
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
