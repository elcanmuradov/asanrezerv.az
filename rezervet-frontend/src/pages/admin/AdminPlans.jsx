import { useEffect, useState } from 'react';
import Spinner from '../../components/Spinner';
import EmptyState from '../../components/EmptyState';
import ErrorAlert from '../../components/ErrorAlert';
import { getAdminPlans, createPlan, updatePlan } from '../../api/admin';

// -1 = limitsiz konvensiyası (Premium üçün)
const UNLIMITED = -1;
const isUnlimited = (v) => v === UNLIMITED || v === null || v === undefined;
const limitLabel = (v) => (isUnlimited(v) ? 'Limitsiz' : v);

// AI analitika səviyyəsi: 0 = bağlı, 1 = aşağı səviyyə, 2 = yüksək səviyyə (+ proqnoz/LLM)
const AI_LEVEL_LABELS = { 0: 'Bağlı', 1: 'Aşağı səviyyə', 2: 'Yüksək səviyyə' };

const emptyForm = {
  name: '',
  monthlyPrice: 0,
  maxBranches: 1,
  maxTablesPerBranch: 20,
  visibilityLevel: 0,
  aiAnalysisLevel: 0,
  description: '',
  features: [],
  popular: false,
};

// Admin: paket (Starter/Standart/Premium) redaktəsi
export default function AdminPlans() {
  const [plans, setPlans] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState(null); // null = yeni paket, obyekt = redaktə
  const [form, setForm] = useState(emptyForm);
  const [saving, setSaving] = useState(false);
  const [formError, setFormError] = useState(null);

  const load = () => {
    setLoading(true);
    getAdminPlans()
      .then((res) => setPlans(res.data?.content ?? res.data ?? []))
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

  const openEdit = (plan) => {
    setEditing(plan);
    setForm({
      name: plan.name ?? '',
      monthlyPrice: plan.monthlyPrice ?? 0,
      maxBranches: plan.maxBranches ?? 1,
      maxTablesPerBranch: plan.maxTablesPerBranch ?? 20,
      visibilityLevel: plan.visibilityLevel ?? 0,
      aiAnalysisLevel: plan.aiAnalysisLevel ?? 0,
      description: plan.description ?? '',
      features: Array.isArray(plan.features) ? plan.features : [],
      popular: !!plan.popular,
    });
    setFormError(null);
    setModalOpen(true);
  };

  const closeModal = () => setModalOpen(false);

  // ---- features redaktoru ----
  const setFeature = (i, value) =>
    setForm((f) => ({ ...f, features: f.features.map((x, idx) => (idx === i ? value : x)) }));
  const addFeature = () => setForm((f) => ({ ...f, features: [...f.features, ''] }));
  const removeFeature = (i) =>
    setForm((f) => ({ ...f, features: f.features.filter((_, idx) => idx !== i) }));

  // ---- limit "limitsiz" toggle ----
  const toggleUnlimited = (field, checked) =>
    setForm((f) => ({ ...f, [field]: checked ? UNLIMITED : (field === 'maxBranches' ? 1 : 20) }));

  const onSave = async (e) => {
    e.preventDefault();
    setSaving(true);
    setFormError(null);
    try {
      const payload = {
        ...form,
        monthlyPrice: Number(form.monthlyPrice),
        maxBranches: Number(form.maxBranches),
        maxTablesPerBranch: Number(form.maxTablesPerBranch),
        visibilityLevel: Number(form.visibilityLevel),
        aiAnalysisLevel: Number(form.aiAnalysisLevel),
        features: form.features.map((x) => x.trim()).filter(Boolean),
      };
      if (editing) await updatePlan(editing.id, payload);
      else await createPlan(payload);
      closeModal();
      load();
    } catch (err) {
      setFormError(err);
    } finally {
      setSaving(false);
    }
  };

  const inputClass =
    'w-full px-md py-sm bg-surface-container rounded-lg border border-outline-variant focus:ring-2 focus:ring-primary outline-none text-body-md';

  return (
    <div className="space-y-gutter">
      <header className="flex flex-wrap justify-between items-end gap-md">
        <div className="space-y-base">
          <h2 className="font-serif text-headline-md text-on-background">Paketlər</h2>
          <p className="font-sans text-body-md text-on-surface-variant">
            Abunə paketlərinin qiymət, limit və məzmununu buradan idarə edin.
          </p>
        </div>
        <button
          onClick={openCreate}
          className="bg-primary text-on-primary font-sans text-label-md h-12 px-6 rounded-xl flex items-center gap-2 elevation-step-1 hover:opacity-90 transition-opacity active:scale-95"
        >
          <span className="material-symbols-outlined">add</span>
          Paket əlavə et
        </button>
      </header>

      <ErrorAlert error={error} />

      {loading ? (
        <Spinner />
      ) : plans.length === 0 ? (
        <EmptyState
          icon="sell"
          title="Hələ paket yoxdur"
          subtitle='Yuxarıdakı "Paket əlavə et" düyməsi ilə ilk paketi (məs. Starter) yaradın.'
        />
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-md items-stretch">
          {plans.map((plan) => (
            <div
              key={plan.id}
              className={`bg-surface-container-lowest p-lg rounded-xl flex flex-col relative ${plan.popular ? 'border-2 border-primary shadow-xl' : 'border border-outline-variant'
                }`}
            >
              {plan.popular && (
                <div className="absolute -top-4 left-1/2 -translate-x-1/2 bg-primary text-on-primary px-4 py-1 rounded-full font-sans text-caption uppercase tracking-wider">
                  Ən populyar
                </div>
              )}
              <div className="mb-md">
                <h3 className="font-sans text-title-lg mb-2">{plan.name}</h3>
                <div className="flex items-baseline gap-1">
                  <span className="font-serif text-headline-md text-on-background">
                    {plan.monthlyPrice != null ? `${plan.monthlyPrice} ₼` : '—'}
                  </span>
                  <span className="text-on-surface-variant font-sans text-body-md">/ay</span>
                </div>
                {plan.description && (
                  <p className="font-sans text-body-md text-on-surface-variant mt-sm">{plan.description}</p>
                )}
              </div>

              {/* Limitlər */}
              <div className="grid grid-cols-2 gap-sm mb-md">
                <div className="bg-surface-container rounded-lg p-sm">
                  <p className="font-sans text-caption text-on-surface-variant">Filial</p>
                  <p className="font-sans text-title-lg text-on-surface">{limitLabel(plan.maxBranches)}</p>
                </div>
                <div className="bg-surface-container rounded-lg p-sm">
                  <p className="font-sans text-caption text-on-surface-variant">Masa / filial</p>
                  <p className="font-sans text-title-lg text-on-surface">{limitLabel(plan.maxTablesPerBranch)}</p>
                </div>
                <div className="bg-surface-container rounded-lg p-sm col-span-2">
                  <p className="font-sans text-caption text-on-surface-variant">AI analitika</p>
                  <p className="font-sans text-title-lg text-on-surface">{AI_LEVEL_LABELS[plan.aiAnalysisLevel ?? 0]}</p>
                </div>
              </div>

              {/* Features */}
              <ul className="space-y-2 mb-md flex-grow">
                {(plan.features ?? []).map((f, i) => (
                  <li key={i} className="flex items-center gap-2 font-sans text-body-md text-on-surface-variant">
                    <span className="material-symbols-outlined text-primary text-[18px]">check_circle</span>
                    {f}
                  </li>
                ))}
              </ul>

              <div className="flex items-center justify-between">
                <span className="font-sans text-caption text-on-surface-variant">
                  Görünmə: {plan.visibilityLevel ?? 0}
                </span>
                <button
                  onClick={() => openEdit(plan)}
                  className="px-md py-2 border border-primary text-primary rounded-xl font-sans text-label-md hover:bg-primary-fixed transition-colors flex items-center gap-xs"
                >
                  <span className="material-symbols-outlined text-[18px]">edit</span>
                  Redaktə et
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Yarat / redaktə modalı */}
      {modalOpen && (
        <div className="fixed inset-0 z-[100] flex items-center justify-center p-md">
          <div className="absolute inset-0 bg-on-background/40 backdrop-blur-sm" onClick={closeModal} />
          <div className="relative bg-surface w-full max-w-lg rounded-xl shadow-2xl overflow-hidden max-h-[90vh] flex flex-col">
            <div className="p-md border-b border-outline-variant flex items-center justify-between bg-surface-container-low">
              <h2 className="font-sans text-title-lg">{editing ? `${editing.name} — redaktə` : 'Yeni paket'}</h2>
              <button className="p-xs hover:bg-surface-container-highest rounded-full transition-colors" onClick={closeModal}>
                <span className="material-symbols-outlined">close</span>
              </button>
            </div>

            <form onSubmit={onSave} className="p-md space-y-md overflow-y-auto">
              <ErrorAlert error={formError} />

              <div className="space-y-xs">
                <label className="font-sans text-label-md text-on-surface-variant">Ad</label>
                <input type="text" required value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} className={inputClass} />
              </div>

              <div className="grid grid-cols-2 gap-md">
                <div className="space-y-xs">
                  <label className="font-sans text-label-md text-on-surface-variant">Aylıq qiymət (₼)</label>
                  <input type="number" min={0} step="0.01" required value={form.monthlyPrice} onChange={(e) => setForm({ ...form, monthlyPrice: e.target.value })} className={inputClass} />
                </div>
                <div className="space-y-xs">
                  <label className="font-sans text-label-md text-on-surface-variant">Görünmə səviyyəsi</label>
                  <input type="number" min={0} value={form.visibilityLevel} onChange={(e) => setForm({ ...form, visibilityLevel: e.target.value })} className={inputClass} />
                </div>
              </div>

              {/* Limitlər + limitsiz toggle */}
              <div className="grid grid-cols-2 gap-md">
                <div className="space-y-xs">
                  <label className="font-sans text-label-md text-on-surface-variant">Filial limiti</label>
                  <input
                    type="number" min={0}
                    disabled={isUnlimited(form.maxBranches)}
                    value={isUnlimited(form.maxBranches) ? '' : form.maxBranches}
                    onChange={(e) => setForm({ ...form, maxBranches: e.target.value })}
                    className={`${inputClass} disabled:opacity-50`}
                  />
                  <label className="flex items-center gap-xs font-sans text-caption text-on-surface-variant cursor-pointer">
                    <input type="checkbox" checked={isUnlimited(form.maxBranches)} onChange={(e) => toggleUnlimited('maxBranches', e.target.checked)} className="rounded text-primary focus:ring-primary" />
                    Limitsiz
                  </label>
                </div>
                <div className="space-y-xs">
                  <label className="font-sans text-label-md text-on-surface-variant">Masa / filial limiti</label>
                  <input
                    type="number" min={0}
                    disabled={isUnlimited(form.maxTablesPerBranch)}
                    value={isUnlimited(form.maxTablesPerBranch) ? '' : form.maxTablesPerBranch}
                    onChange={(e) => setForm({ ...form, maxTablesPerBranch: e.target.value })}
                    className={`${inputClass} disabled:opacity-50`}
                  />
                  <label className="flex items-center gap-xs font-sans text-caption text-on-surface-variant cursor-pointer">
                    <input type="checkbox" checked={isUnlimited(form.maxTablesPerBranch)} onChange={(e) => toggleUnlimited('maxTablesPerBranch', e.target.checked)} className="rounded text-primary focus:ring-primary" />
                    Limitsiz
                  </label>
                </div>
              </div>

              {/* AI analitika səviyyəsi — limit deyil, 0/1/2 sabit dəyər */}
              <div className="space-y-xs">
                <label className="font-sans text-label-md text-on-surface-variant">AI analitika səviyyəsi</label>
                <select
                  value={form.aiAnalysisLevel}
                  onChange={(e) => setForm({ ...form, aiAnalysisLevel: e.target.value })}
                  className={inputClass}
                >
                  <option value={0}>0 — Bağlı</option>
                  <option value={1}>1 — Aşağı səviyyə</option>
                  <option value={2}>2 — Yüksək səviyyə (proqnoz + LLM)</option>
                </select>
              </div>

              <div className="space-y-xs">
                <label className="font-sans text-label-md text-on-surface-variant">Təsvir</label>
                <textarea rows={2} value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} className={inputClass} />
              </div>

              {/* Features redaktoru */}
              <div className="space-y-xs">
                <label className="font-sans text-label-md text-on-surface-variant">Xüsusiyyətlər</label>
                <div className="space-y-xs">
                  {form.features.map((f, i) => (
                    <div key={i} className="flex items-center gap-xs">
                      <input type="text" value={f} onChange={(e) => setFeature(i, e.target.value)} placeholder="Məs: AI analitika" className={inputClass} />
                      <button type="button" onClick={() => removeFeature(i)} className="p-xs text-error hover:bg-error-container rounded-lg shrink-0">
                        <span className="material-symbols-outlined text-[20px]">delete</span>
                      </button>
                    </div>
                  ))}
                  <button type="button" onClick={addFeature} className="flex items-center gap-xs text-primary font-sans text-label-md hover:underline">
                    <span className="material-symbols-outlined text-[18px]">add</span>
                    Xüsusiyyət əlavə et
                  </button>
                </div>
              </div>

              <label className="flex items-center gap-sm p-sm bg-surface-container rounded-lg cursor-pointer">
                <input type="checkbox" checked={form.popular} onChange={(e) => setForm({ ...form, popular: e.target.checked })} className="rounded text-primary focus:ring-primary" />
                <span className="font-sans text-body-md">"Ən populyar" kimi işarələ</span>
              </label>

              <div className="pt-xs flex gap-sm">
                <button type="button" onClick={closeModal} className="flex-1 py-sm border border-outline text-on-surface font-sans text-label-md rounded-xl hover:bg-surface-container transition-colors">
                  Ləğv et
                </button>
                <button type="submit" disabled={saving} className="flex-1 py-sm bg-primary text-on-primary font-sans text-label-md rounded-xl hover:opacity-90 transition-all disabled:opacity-50">
                  {saving ? 'Yadda saxlanılır...' : editing ? 'Yadda saxla' : 'Paketi yarat'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
