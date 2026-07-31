import { useEffect, useState } from 'react';
import Spinner from '../../components/Spinner';
import EmptyState from '../../components/EmptyState';
import ErrorAlert from '../../components/ErrorAlert';
import { getBranches, getTables, createTable, updateTable, deleteTable } from '../../api/restaurants';

const emptyForm = { name: '', capacity: 4, zone: '', type: 'SQUARE' };

const TABLE_TYPES = [
  { value: 'SQUARE', label: 'Kvadrat', icon: 'crop_square' },
  { value: 'CIRCLE', label: 'Dairəvi', icon: 'circle' },
  { value: 'RECTANGLE', label: 'Düzbucaqlı', icon: 'crop_landscape' },
];

// Masa forması üçün vizual sinif (kart görünüşü)
const shapeClass = (type) => {
  if (type === 'CIRCLE') return 'rounded-full aspect-square';
  if (type === 'RECTANGLE') return 'rounded-lg aspect-[2/1]';
  return 'rounded-lg aspect-square';
};

// Masaların idarəsi — filial seçilir, masalar say/ölçü/zona ilə əlavə olunur
export default function Tables() {
  const [branches, setBranches] = useState([]);
  const [branchId, setBranchId] = useState('');
  const [tables, setTables] = useState([]);
  const [loadingBranches, setLoadingBranches] = useState(true);
  const [loadingTables, setLoadingTables] = useState(false);
  const [error, setError] = useState(null);

  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState(emptyForm);
  const [saving, setSaving] = useState(false);
  const [formError, setFormError] = useState(null);

  useEffect(() => {
    getBranches()
      .then((res) => {
        const list = res.data?.content ?? res.data ?? [];
        setBranches(list);
        if (list.length > 0) setBranchId(String(list[0].id));
      })
      .catch((err) => setError(err))
      .finally(() => setLoadingBranches(false));
  }, []);

  const loadTables = (id) => {
    if (!id) return;
    setLoadingTables(true);
    setError(null);
    getTables(id)
      .then((res) => setTables(res.data?.content ?? res.data ?? []))
      .catch((err) => setError(err))
      .finally(() => setLoadingTables(false));
  };

  useEffect(() => { loadTables(branchId); }, [branchId]);

  const openCreate = () => {
    setEditing(null);
    setForm(emptyForm);
    setFormError(null);
    setModalOpen(true);
  };

  const openEdit = (table) => {
    setEditing(table);
    setForm({ name: table.name ?? '', capacity: table.capacity ?? 4, zone: table.zone ?? '', type: table.type ?? 'SQUARE' });
    setFormError(null);
    setModalOpen(true);
  };

  const onSave = async (e) => {
    e.preventDefault();
    setSaving(true);
    setFormError(null);
    try {
      const payload = { ...form, capacity: Number(form.capacity) };
      if (editing) await updateTable(editing.id, payload);
      else await createTable(branchId, payload);
      setModalOpen(false);
      loadTables(branchId);
    } catch (err) {
      setFormError(err);
    } finally {
      setSaving(false);
    }
  };

  const onDelete = async (table) => {
    if (!window.confirm(`"${table.name}" masasını silmək istədiyinizə əminsiniz?`)) return;
    try {
      await deleteTable(table.id);
      loadTables(branchId);
    } catch (err) {
      setError(err);
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
            <span className="font-sans text-label-md">Masalar</span>
          </nav>
          <h2 className="font-serif text-headline-md text-on-background">Masa konfiqurasiyası</h2>
        </div>
        <div className="flex items-end gap-md">
          <div className="flex flex-col gap-xs">
            <label className="font-sans text-label-md text-on-surface-variant">Filial</label>
            <select
              value={branchId}
              onChange={(e) => setBranchId(e.target.value)}
              className="px-md py-sm bg-surface-container rounded-lg border border-outline-variant focus:ring-2 focus:ring-primary text-body-md min-w-[200px]"
            >
              {branches.length === 0 && <option value="">Filial yoxdur</option>}
              {branches.map((b) => (
                <option key={b.id} value={b.id}>{b.name}</option>
              ))}
            </select>
          </div>
          <button
            onClick={openCreate}
            disabled={!branchId}
            className="bg-primary text-on-primary font-sans text-label-md h-12 px-6 rounded-xl flex items-center gap-2 elevation-step-1 hover:opacity-90 transition-opacity active:scale-95 disabled:opacity-50"
          >
            <span className="material-symbols-outlined">add</span>
            Masa əlavə et
          </button>
        </div>
      </header>

      <ErrorAlert error={error} />

      {loadingBranches ? (
        <Spinner />
      ) : branches.length === 0 ? (
        <EmptyState
          icon="storefront"
          title="Əvvəlcə filial yaradın"
          subtitle="Masa əlavə etmək üçün ən azı bir filialınız olmalıdır."
        />
      ) : (
        <div className="bg-surface-container-lowest border border-outline-variant rounded-xl p-6 elevation-step-1">
          <div className="flex items-center gap-2 mb-6">
            <span className="material-symbols-outlined text-primary">table_restaurant</span>
            <h3 className="font-sans text-title-lg text-on-surface">Masalar</h3>
            {!loadingTables && (
              <span className="bg-secondary-container text-on-secondary-container px-3 py-1 rounded-full font-sans text-label-md ml-auto">
                {tables.length} masa
              </span>
            )}
          </div>

          {loadingTables ? (
            <Spinner />
          ) : tables.length === 0 ? (
            <EmptyState
              icon="table_restaurant"
              title="Bu filialda masa yoxdur"
              subtitle="Masa sayı, tutumu və zonasını qeyd edərək əlavə edin."
            />
          ) : (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-md">
              {tables.map((t) => (
                <div key={t.id} className="flex items-center justify-between p-4 bg-surface rounded-lg border border-outline-variant">
                  <div className="flex items-center gap-md">
                    <div className={`w-12 h-12 shrink-0 bg-primary-fixed flex items-center justify-center text-primary ${shapeClass(t.type)}`}>
                      <span className="material-symbols-outlined text-[20px]">table_restaurant</span>
                    </div>
                    <div>
                      {t.zone && <span className="font-sans text-label-md text-primary">{t.zone}</span>}
                      <h4 className="font-sans text-body-lg font-bold">{t.name}</h4>
                      <p className="font-sans text-caption text-on-surface-variant">{t.capacity} nəfərlik</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-1">
                    <button
                      onClick={() => openEdit(t)}
                      className="w-9 h-9 rounded-full flex items-center justify-center text-on-surface-variant hover:bg-surface-container-high transition-colors"
                    >
                      <span className="material-symbols-outlined text-[20px]">edit</span>
                    </button>
                    <button
                      onClick={() => onDelete(t)}
                      className="w-9 h-9 rounded-full flex items-center justify-center text-error hover:bg-error-container transition-colors"
                    >
                      <span className="material-symbols-outlined text-[20px]">delete</span>
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* Masa yarat/redaktə modalı */}
      {modalOpen && (
        <div className="fixed inset-0 z-[100] flex items-center justify-center p-md">
          <div className="absolute inset-0 bg-on-background/40 backdrop-blur-sm" onClick={() => setModalOpen(false)} />
          <div className="relative bg-surface w-full max-w-md rounded-xl shadow-2xl overflow-hidden">
            <div className="p-md border-b border-outline-variant flex items-center justify-between bg-surface-container-low">
              <h2 className="font-sans text-title-lg">{editing ? 'Masanı redaktə et' : 'Yeni masa'}</h2>
              <button className="p-xs hover:bg-surface-container-highest rounded-full transition-colors" onClick={() => setModalOpen(false)}>
                <span className="material-symbols-outlined">close</span>
              </button>
            </div>
            <form onSubmit={onSave} className="p-md space-y-md">
              <ErrorAlert error={formError} />
              <div className="space-y-xs">
                <label className="font-sans text-label-md text-on-surface-variant">Masa adı / nömrəsi</label>
                <input type="text" required value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} placeholder="Məs: M1, VIP-2" className={inputClass} />
              </div>
              <div className="grid grid-cols-2 gap-md">
                <div className="space-y-xs">
                  <label className="font-sans text-label-md text-on-surface-variant">Tutum (nəfər)</label>
                  <input type="number" required min={1} max={50} value={form.capacity} onChange={(e) => setForm({ ...form, capacity: e.target.value })} className={inputClass} />
                </div>
                <div className="space-y-xs">
                  <label className="font-sans text-label-md text-on-surface-variant">Zona</label>
                  <input type="text" value={form.zone} onChange={(e) => setForm({ ...form, zone: e.target.value })} placeholder="Zal, terras, VIP..." className={inputClass} />
                </div>
              </div>
              <div className="space-y-xs">
                <label className="font-sans text-label-md text-on-surface-variant">Masa forması</label>
                <div className="grid grid-cols-3 gap-xs">
                  {TABLE_TYPES.map((tt) => (
                    <button
                      key={tt.value}
                      type="button"
                      onClick={() => setForm({ ...form, type: tt.value })}
                      className={`py-sm rounded-lg border flex flex-col items-center gap-xs transition-colors ${
                        form.type === tt.value
                          ? 'border-primary bg-primary-fixed text-primary'
                          : 'border-outline-variant text-on-surface-variant hover:bg-surface-container'
                      }`}
                    >
                      <span className="material-symbols-outlined text-[22px]">{tt.icon}</span>
                      <span className="text-caption">{tt.label}</span>
                    </button>
                  ))}
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
