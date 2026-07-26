import { useEffect, useState } from 'react';
import Spinner from '../../components/Spinner';
import EmptyState from '../../components/EmptyState';
import ErrorAlert from '../../components/ErrorAlert';
import { getStaff, createWaiter, deleteWaiter } from '../../api/manager';
import { getBranches } from '../../api/restaurants';

const emptyForm = { fullName: '', email: '', branchId: '' };

// Ofisiant (waiter) hesablarının idarəsi
export default function Staff() {
  const [staff, setStaff] = useState([]);
  const [branches, setBranches] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [modalOpen, setModalOpen] = useState(false);
  const [form, setForm] = useState(emptyForm);
  const [saving, setSaving] = useState(false);
  const [formError, setFormError] = useState(null);

  const load = () => {
    setLoading(true);
    Promise.all([getStaff(), getBranches()])
      .then(([s, b]) => {
        setStaff(s.data?.content ?? s.data ?? []);
        setBranches(b.data?.content ?? b.data ?? []);
      })
      .catch((err) => setError(err))
      .finally(() => setLoading(false));
  };

  useEffect(load, []);

  const onSave = async (e) => {
    e.preventDefault();
    setFormError(null);
    if (!form.branchId) {
      setFormError('Filial seçin.');
      return;
    }
    setSaving(true);
    try {
      await createWaiter(form);
      setModalOpen(false);
      setForm(emptyForm);
      load();
    } catch (err) {
      setFormError(err);
    } finally {
      setSaving(false);
    }
  };

  const onDelete = async (member) => {
    if (!window.confirm(`"${member.fullName}" işçisini silmək istədiyinizə əminsiniz?`)) return;
    try {
      await deleteWaiter(member.id);
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
            <span className="font-sans text-label-md">İşçilər</span>
          </nav>
          <h2 className="font-serif text-headline-md text-on-background">Ofisiantlar</h2>
        </div>
        <button
          onClick={() => { setForm(emptyForm); setFormError(null); setModalOpen(true); }}
          className="bg-primary text-on-primary font-sans text-label-md h-12 px-6 rounded-xl flex items-center gap-2 elevation-step-1 hover:opacity-90 transition-opacity active:scale-95"
        >
          <span className="material-symbols-outlined">person_add</span>
          Ofisiant əlavə et
        </button>
      </header>

      <ErrorAlert error={error} />

      {loading ? (
        <Spinner />
      ) : staff.length === 0 ? (
        <EmptyState
          icon="groups"
          title="Hələ ofisiant yoxdur"
          subtitle="Ofisiant hesabı yaradın — o, öz panelindən masa statuslarını idarə edəcək."
        />
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-md">
          {staff.map((m) => (
            <div key={m.id} className="bg-surface-container-lowest border border-outline-variant rounded-xl p-md flex items-start justify-between elevation-step-1">
              <div className="flex items-start gap-md">
                <div className="w-12 h-12 rounded-full bg-primary-fixed flex items-center justify-center text-primary font-bold uppercase">
                  {(m.fullName || m.email || '?').charAt(0)}
                </div>
                <div>
                  <h4 className="font-sans text-title-lg text-on-surface">{m.fullName || m.email || 'Ofisiant'}</h4>
                  {m.fullName && <p className="font-sans text-body-md text-on-surface-variant">{m.email}</p>}
                  {(m.branch?.name || m.branches?.length > 0) && (
                    <p className="font-sans text-caption text-on-surface-variant mt-xs">
                      {m.branch?.name ?? m.branches.map((b) => b.name ?? b).join(', ')}
                    </p>
                  )}
                </div>
              </div>
              <button
                onClick={() => onDelete(m)}
                className="w-9 h-9 rounded-full flex items-center justify-center text-error hover:bg-error-container transition-colors"
              >
                <span className="material-symbols-outlined text-[20px]">delete</span>
              </button>
            </div>
          ))}
        </div>
      )}

      {/* Ofisiant əlavə et modalı */}
      {modalOpen && (
        <div className="fixed inset-0 z-[100] flex items-center justify-center p-md">
          <div className="absolute inset-0 bg-on-background/40 backdrop-blur-sm" onClick={() => setModalOpen(false)} />
          <div className="relative bg-surface w-full max-w-md rounded-xl shadow-2xl overflow-hidden">
            <div className="p-md border-b border-outline-variant flex items-center justify-between bg-surface-container-low">
              <h2 className="font-sans text-title-lg">Yeni ofisiant</h2>
              <button className="p-xs hover:bg-surface-container-highest rounded-full transition-colors" onClick={() => setModalOpen(false)}>
                <span className="material-symbols-outlined">close</span>
              </button>
            </div>
            <form onSubmit={onSave} className="p-md space-y-md">
              <ErrorAlert error={formError} />
              <div className="space-y-xs">
                <label className="font-sans text-label-md text-on-surface-variant">Ad Soyad</label>
                <input type="text" required value={form.fullName} onChange={(e) => setForm({ ...form, fullName: e.target.value })} className={inputClass} />
              </div>
              <div className="space-y-xs">
                <label className="font-sans text-label-md text-on-surface-variant">E-poçt</label>
                <input type="email" required value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} className={inputClass} />
                <p className="font-sans text-caption text-on-surface-variant">Dəvət və giriş məlumatları bu ünvana göndəriləcək.</p>
              </div>
              <div className="space-y-xs">
                <label className="font-sans text-label-md text-on-surface-variant">Təyin olunduğu filial</label>
                {branches.length === 0 ? (
                  <p className="font-sans text-body-md text-on-surface-variant">Əvvəlcə filial yaradın.</p>
                ) : (
                  <select
                    required
                    value={form.branchId}
                    onChange={(e) => setForm({ ...form, branchId: e.target.value })}
                    className={inputClass}
                  >
                    <option value="">Filial seçin...</option>
                    {branches.map((b) => (
                      <option key={b.id} value={b.id}>{b.name}</option>
                    ))}
                  </select>
                )}
              </div>
              <div className="pt-md flex gap-sm">
                <button type="button" onClick={() => setModalOpen(false)} className="flex-1 py-sm border border-outline text-on-surface font-sans text-label-md rounded-xl hover:bg-surface-container transition-colors">
                  Ləğv et
                </button>
                <button type="submit" disabled={saving} className="flex-1 py-sm bg-primary text-on-primary font-sans text-label-md rounded-xl hover:opacity-90 transition-all disabled:opacity-50">
                  {saving ? 'Yaradılır...' : 'Yarat'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
