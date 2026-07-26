import { useEffect, useState } from 'react';
import Spinner from '../../components/Spinner';
import EmptyState from '../../components/EmptyState';
import ErrorAlert from '../../components/ErrorAlert';
import { getBranches, createBranch, updateBranch, deleteBranch } from '../../api/restaurants';
import { formatAzPhoneInput } from '../../utils/phone';

const emptyForm = {
  name: '', address: '', phone: '',
  city: '', district: '', latitude: '', longitude: '',
  openingTime: '10:00', closingTime: '23:00',
};

// "10:00 - 23:00" -> { openingTime: '10:00', closingTime: '23:00' } (backend-dən köhnə formatda gəlsə)
const parseWorkingHours = (workingHours) => {
  const match = /^(\d{2}:\d{2})\s*-\s*(\d{2}:\d{2})$/.exec(workingHours ?? '');
  return match ? { openingTime: match[1], closingTime: match[2] } : { openingTime: '10:00', closingTime: '23:00' };
};

// Filiallar — dizayndakı "Branch Management" səhifəsi
export default function Branches() {
  const [branches, setBranches] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState(null); // null = yeni, obyekt = redaktə
  const [form, setForm] = useState(emptyForm);
  const [saving, setSaving] = useState(false);
  const [formError, setFormError] = useState(null);

  const load = () => {
    setLoading(true);
    getBranches()
      .then((res) => setBranches(res.data?.content ?? res.data ?? []))
      .catch((err) => setError(err))
      .finally(() => setLoading(false));
  };

  useEffect(load, []);

  const openCreate = () => {
    setEditing(null);
    setForm(emptyForm);
    setFormError(null);
    setModalOpen(true);
  };

  const openEdit = (branch) => {
    setEditing(branch);
    setForm({
      name: branch.name ?? '',
      address: branch.address ?? '',
      phone: branch.phone ?? '',
      city: branch.city ?? '',
      district: branch.district ?? '',
      latitude: branch.latitude ?? '',
      longitude: branch.longitude ?? '',
      ...parseWorkingHours(branch.workingHours),
    });
    setFormError(null);
    setModalOpen(true);
  };

  const onSave = async (e) => {
    e.preventDefault();
    setSaving(true);
    setFormError(null);
    try {
      const { openingTime, closingTime, latitude, longitude, ...rest } = form;
      const payload = {
        ...rest,
        latitude: latitude === '' ? null : Number(latitude),
        longitude: longitude === '' ? null : Number(longitude),
        openingTime,
        closingTime,
        // Köhnə/başqa yerlərdə göstərmək üçün mətn forması da göndərilir
        workingHours: `${openingTime} - ${closingTime}`,
      };
      if (editing) await updateBranch(editing.id, payload);
      else await createBranch(payload);
      setModalOpen(false);
      load();
    } catch (err) {
      setFormError(err);
    } finally {
      setSaving(false);
    }
  };

  const onDelete = async (branch) => {
    if (!window.confirm(`"${branch.name}" filialını silmək istədiyinizə əminsiniz?`)) return;
    setError(null);
    try {
      await deleteBranch(branch.id);
      load();
    } catch (err) {
      setError(err);
    }
  };

  const inputClass =
    'w-full px-md py-sm bg-surface-container rounded-lg border border-outline-variant focus:ring-2 focus:ring-primary outline-none text-body-md';

  return (
    <div className="space-y-gutter">
      <header className="flex justify-between items-end">
        <div className="space-y-base">
          <nav className="flex items-center gap-2 text-on-surface-variant opacity-60">
            <span className="font-sans text-label-md">Panel</span>
            <span className="material-symbols-outlined text-sm">chevron_right</span>
            <span className="font-sans text-label-md">Filiallar</span>
          </nav>
          <h2 className="font-serif text-headline-md text-on-background">Filial idarəetməsi</h2>
        </div>
        <button
          onClick={openCreate}
          className="bg-primary text-on-primary font-sans text-label-md h-12 px-6 rounded-xl flex items-center gap-2 elevation-step-1 hover:opacity-90 transition-opacity active:scale-95"
        >
          <span className="material-symbols-outlined">add</span>
          Filial əlavə et
        </button>
      </header>

      <ErrorAlert error={error} />

      <div className="bg-surface-container-low border border-outline-variant rounded-xl p-6 elevation-step-1">
        <div className="flex justify-between items-center mb-6">
          <h3 className="font-sans text-title-lg text-on-surface">Aktiv filiallar</h3>
          {!loading && (
            <span className="bg-secondary-container text-on-secondary-container px-3 py-1 rounded-full font-sans text-label-md">
              {branches.length} aktiv
            </span>
          )}
        </div>

        {loading ? (
          <Spinner />
        ) : branches.length === 0 ? (
          <EmptyState
            icon="storefront"
            title="Hələ filial yoxdur"
            subtitle="İlk filialınızı əlavə edin — masalar və rezervlər filiala bağlıdır."
          />
        ) : (
          <div className="space-y-4">
            {branches.map((b) => (
              <div
                key={b.id}
                className="group flex items-center justify-between p-4 bg-surface rounded-xl border border-transparent hover:border-outline-variant transition-all"
              >
                <div className="flex items-center gap-4">
                  <div className="w-16 h-16 rounded-lg bg-surface-container-highest flex items-center justify-center shrink-0">
                    <span className="material-symbols-outlined text-primary text-3xl">storefront</span>
                  </div>
                  <div>
                    <h4 className="font-sans text-title-lg text-on-surface">{b.name}</h4>
                    <p className="font-sans text-body-md text-on-surface-variant">
                      {[b.address, b.tableCount != null ? `${b.tableCount} masa` : null]
                        .filter(Boolean)
                        .join(' • ') || '—'}
                    </p>
                  </div>
                </div>
                <div className="flex items-center gap-2">
                  <button
                    onClick={() => openEdit(b)}
                    title="Redaktə et"
                    className="w-10 h-10 rounded-full flex items-center justify-center text-on-surface-variant hover:bg-surface-container-high transition-colors"
                  >
                    <span className="material-symbols-outlined">edit</span>
                  </button>
                  <button
                    onClick={() => onDelete(b)}
                    title="Sil"
                    className="w-10 h-10 rounded-full flex items-center justify-center text-error hover:bg-error-container transition-colors"
                  >
                    <span className="material-symbols-outlined">delete</span>
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Filial yarat/redaktə modalı */}
      {modalOpen && (
        <div className="fixed inset-0 z-[100] flex items-center justify-center p-md">
          <div className="absolute inset-0 bg-on-background/40 backdrop-blur-sm" onClick={() => setModalOpen(false)} />
          <div className="relative bg-surface w-full max-w-lg rounded-xl shadow-2xl overflow-hidden">
            <div className="p-md border-b border-outline-variant flex items-center justify-between bg-surface-container-low">
              <h2 className="font-sans text-title-lg">{editing ? 'Filialı redaktə et' : 'Yeni filial'}</h2>
              <button className="p-xs hover:bg-surface-container-highest rounded-full transition-colors" onClick={() => setModalOpen(false)}>
                <span className="material-symbols-outlined">close</span>
              </button>
            </div>
            <form onSubmit={onSave} className="p-md space-y-md">
              <ErrorAlert error={formError} />
              <div className="space-y-xs">
                <label className="font-sans text-label-md text-on-surface-variant">Filial adı</label>
                <input type="text" required value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} placeholder="Məs: Mərkəz filialı" className={inputClass} />
              </div>
              <div className="space-y-xs">
                <label className="font-sans text-label-md text-on-surface-variant">Ünvan</label>
                <input type="text" required value={form.address} onChange={(e) => setForm({ ...form, address: e.target.value })} placeholder="Küçə, bina, şəhər" className={inputClass} />
              </div>
              <div className="space-y-xs">
                <label className="font-sans text-label-md text-on-surface-variant">Telefon</label>
                <input
                  type="tel"
                  value={form.phone}
                  onChange={(e) => setForm({ ...form, phone: formatAzPhoneInput(e.target.value) })}
                  placeholder="+994 (50) 000 00 00"
                  className={inputClass}
                />
              </div>
              <div className="grid grid-cols-2 gap-md">
                <div className="space-y-xs">
                  <label className="font-sans text-label-md text-on-surface-variant">Şəhər</label>
                  <input type="text" value={form.city} onChange={(e) => setForm({ ...form, city: e.target.value })} placeholder="Məs: Bakı" className={inputClass} />
                </div>
                <div className="space-y-xs">
                  <label className="font-sans text-label-md text-on-surface-variant">Rayon</label>
                  <input type="text" value={form.district} onChange={(e) => setForm({ ...form, district: e.target.value })} placeholder="Məs: Nəsimi" className={inputClass} />
                </div>
              </div>
              <div className="space-y-xs">
                <div className="flex items-center justify-between">
                  <label className="font-sans text-label-md text-on-surface-variant">Koordinatlar (xəritədə axtarış üçün)</label>
                  <button
                    type="button"
                    onClick={() => {
                      if (!navigator.geolocation) return;
                      navigator.geolocation.getCurrentPosition(
                        (pos) => setForm((f) => ({ ...f, latitude: pos.coords.latitude, longitude: pos.coords.longitude })),
                        () => setFormError(new Error('Məkan alına bilmədi'))
                      );
                    }}
                    className="font-sans text-label-md text-primary hover:underline flex items-center gap-1"
                  >
                    <span className="material-symbols-outlined text-[16px]">my_location</span>
                    Mövcud məkan
                  </button>
                </div>
                <div className="grid grid-cols-2 gap-md">
                  <input type="number" step="any" value={form.latitude} onChange={(e) => setForm({ ...form, latitude: e.target.value })} placeholder="Enlik (latitude)" className={inputClass} />
                  <input type="number" step="any" value={form.longitude} onChange={(e) => setForm({ ...form, longitude: e.target.value })} placeholder="Uzunluq (longitude)" className={inputClass} />
                </div>
              </div>
              <div className="grid grid-cols-2 gap-md">
                <div className="space-y-xs">
                  <label className="font-sans text-label-md text-on-surface-variant">Açılış saatı</label>
                  <input type="time" required value={form.openingTime} onChange={(e) => setForm({ ...form, openingTime: e.target.value })} className={inputClass} />
                </div>
                <div className="space-y-xs">
                  <label className="font-sans text-label-md text-on-surface-variant">Bağlanış saatı</label>
                  <input type="time" required value={form.closingTime} onChange={(e) => setForm({ ...form, closingTime: e.target.value })} className={inputClass} />
                </div>
              </div>
              <div className="pt-md flex gap-sm">
                <button type="button" onClick={() => setModalOpen(false)} className="flex-1 py-sm border border-outline text-on-surface font-sans text-label-md rounded-xl hover:bg-surface-container transition-colors">
                  Ləğv et
                </button>
                <button type="submit" disabled={saving} className="flex-1 py-sm bg-primary text-on-primary font-sans text-label-md rounded-xl hover:opacity-90 transition-all disabled:opacity-50">
                  {saving ? 'Yadda saxlanılır...' : 'Yadda saxla'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
