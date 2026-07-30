import { useEffect, useRef, useState } from 'react';
import Spinner from '../../components/Spinner';
import ErrorAlert from '../../components/ErrorAlert';
import { getMyRestaurant, updateMyRestaurant, uploadRestaurantImage, publishRestaurant } from '../../api/restaurants';
import { formatAzPhoneInput } from '../../utils/phone';

const MAX_IMAGE_MB = 5;

// Manager: restoran profili redaktəsi — məlumatlar + banner/profil şəkli yükləmə
export default function RestaurantSettings() {
  const [form, setForm] = useState({
    name: '', cuisine: '', city: '', phone: '', description: '',
    bannerUrl: '', profileImageUrl: '',
  });
  const [restaurantId, setRestaurantId] = useState(null);
  const [publicationStatus, setPublicationStatus] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [saving, setSaving] = useState(false);
  const [saved, setSaved] = useState(false);
  const [uploading, setUploading] = useState(null); // 'banner' | 'profile' | null
  const [publishing, setPublishing] = useState(false);

  const bannerInputRef = useRef(null);
  const profileInputRef = useRef(null);

  useEffect(() => {
    getMyRestaurant()
      .then((res) => {
        const r = res.data ?? {};
        const media = r.mediaAssets ?? {};
        setForm({
          name: r.name ?? '', cuisine: r.cuisine ?? '', city: r.city ?? '',
          phone: r.phone ?? '', description: r.description ?? '',
          bannerUrl: media.bannerUrl ?? r.bannerUrl ?? '',
          profileImageUrl: media.profilePhotoUrl ?? r.profileImageUrl ?? '',
        });
        setRestaurantId(r.id ?? null);
        setPublicationStatus(r.publicationStatus ?? 'DRAFT');
      })
      .catch((err) => setError(err))
      .finally(() => setLoading(false));
  }, []);

  const onPickImage = async (type, file) => {
    if (!file) return;
    setError(null);
    setSaved(false);
    if (!file.type.startsWith('image/')) {
      setError('Yalnız şəkil faylı seçin (JPG, PNG, WebP).');
      return;
    }
    if (file.size > MAX_IMAGE_MB * 1024 * 1024) {
      setError(`Şəkil ${MAX_IMAGE_MB} MB-dan böyük ola bilməz.`);
      return;
    }
    setUploading(type);
    try {
      const { data } = await uploadRestaurantImage(type, file);
      // Backend yüklənmiş şəklin URL-ini qaytarır (string və ya { url })
      const url = typeof data === 'string' ? data : data?.url;
      if (url) {
        setForm((f) => (type === 'banner' ? { ...f, bannerUrl: url } : { ...f, profileImageUrl: url }));
      }
    } catch (err) {
      setError(err);
    } finally {
      setUploading(null);
    }
  };

  const onPublish = async () => {
    if (!restaurantId || publishing) return;
    setError(null);
    setPublishing(true);
    try {
      await publishRestaurant(restaurantId);
      setPublicationStatus('PUBLISHED');
    } catch (err) {
      setError(err);
    } finally {
      setPublishing(false);
    }
  };

  const onSave = async (e) => {
    e.preventDefault();
    setError(null);
    setSaved(false);
    setSaving(true);
    try {
      await updateMyRestaurant(form);
      setSaved(true);
    } catch (err) {
      setError(err);
    } finally {
      setSaving(false);
    }
  };

  const inputClass =
    'w-full px-md py-sm bg-surface-container rounded-lg border border-outline-variant focus:ring-2 focus:ring-primary focus:border-primary outline-none text-body-md';

  if (loading) return <Spinner />;

  return (
    <div className="space-y-gutter">
      <header className="space-y-base">
        <nav className="flex items-center gap-2 text-on-surface-variant opacity-60">
          <span className="font-sans text-label-md">Panel</span>
          <span className="material-symbols-outlined text-sm">chevron_right</span>
          <span className="font-sans text-label-md">Restoran</span>
        </nav>
        <div className="flex items-center justify-between flex-wrap gap-md">
          <h2 className="font-serif text-headline-md text-on-background">Restoran profili</h2>
          <div className="flex items-center gap-sm">
            <span
              className={`px-sm py-xs rounded-full font-sans text-label-md flex items-center gap-xs ${
                publicationStatus === 'PUBLISHED'
                  ? 'bg-primary-fixed text-on-primary-fixed'
                  : 'bg-surface-container-high text-on-surface-variant'
              }`}
            >
              <span className="material-symbols-outlined text-[16px]">
                {publicationStatus === 'PUBLISHED' ? 'public' : 'visibility_off'}
              </span>
              {publicationStatus === 'PUBLISHED' ? 'Dərc olunub' : 'Qaralama (Draft)'}
            </span>
            {publicationStatus !== 'PUBLISHED' && (
              <button
                type="button"
                onClick={onPublish}
                disabled={!restaurantId || publishing}
                className="px-lg py-sm bg-primary text-on-primary rounded-xl font-sans text-label-md hover:opacity-90 active:scale-95 transition-all disabled:opacity-50 flex items-center gap-xs"
              >
                <span className="material-symbols-outlined text-[18px]">campaign</span>
                {publishing ? 'Dərc edilir...' : 'Dərc et'}
              </button>
            )}
          </div>
        </div>
      </header>

      <ErrorAlert error={error} />
      {saved && (
        <div className="bg-primary-fixed text-on-primary-fixed rounded-lg px-md py-sm text-body-md flex items-center gap-base">
          <span className="material-symbols-outlined text-[20px]">check_circle</span>
          Dəyişikliklər yadda saxlanıldı.
        </div>
      )}

      <form onSubmit={onSave} className="space-y-gutter">
        {/* ---- Şəkillər ---- */}
        <section className="bg-surface-container-lowest border border-outline-variant rounded-xl overflow-hidden elevation-step-1">
          {/* Banner önizləmə + yükləmə */}
          <div className="relative h-56 bg-surface-container-highest group">
            {form.bannerUrl ? (
              <img src={form.bannerUrl} alt="Banner" className="w-full h-full object-cover" />
            ) : (
              <div className="w-full h-full flex flex-col items-center justify-center text-on-surface-variant gap-xs">
                <span className="material-symbols-outlined text-[48px] text-primary/30">image</span>
                <span className="font-sans text-body-md">Banner şəkli yoxdur</span>
              </div>
            )}
            <div className="absolute inset-0 bg-on-background/0 group-hover:bg-on-background/30 transition-colors flex items-center justify-center">
              <button
                type="button"
                onClick={() => bannerInputRef.current?.click()}
                disabled={uploading !== null}
                className="opacity-0 group-hover:opacity-100 transition-opacity px-md py-2 bg-surface text-primary rounded-xl font-sans text-label-md flex items-center gap-xs shadow-lg disabled:opacity-50"
              >
                <span className="material-symbols-outlined text-[20px]">upload</span>
                {uploading === 'banner' ? 'Yüklənir...' : 'Banner yüklə'}
              </button>
            </div>
            <input
              ref={bannerInputRef}
              type="file"
              accept="image/*"
              className="hidden"
              onChange={(e) => { onPickImage('banner', e.target.files?.[0]); e.target.value = ''; }}
            />
          </div>

          {/* Profil şəkli — bannerin üstünə oturan dairə */}
          <div className="px-md pb-md">
            <div className="flex items-end gap-md -mt-12">
              <div className="relative group/avatar shrink-0">
                <div className="w-24 h-24 rounded-full border-4 border-surface bg-surface-container-highest overflow-hidden flex items-center justify-center">
                  {form.profileImageUrl ? (
                    <img src={form.profileImageUrl} alt="Profil" className="w-full h-full object-cover" />
                  ) : (
                    <span className="material-symbols-outlined text-primary/40 text-[40px]">restaurant</span>
                  )}
                </div>
                <button
                  type="button"
                  onClick={() => profileInputRef.current?.click()}
                  disabled={uploading !== null}
                  title="Profil şəkli yüklə"
                  className="absolute bottom-0 right-0 w-8 h-8 bg-primary text-on-primary rounded-full flex items-center justify-center shadow-md hover:opacity-90 transition-opacity disabled:opacity-50"
                >
                  <span className="material-symbols-outlined text-[18px]">
                    {uploading === 'profile' ? 'hourglass_top' : 'photo_camera'}
                  </span>
                </button>
                <input
                  ref={profileInputRef}
                  type="file"
                  accept="image/*"
                  className="hidden"
                  onChange={(e) => { onPickImage('profile', e.target.files?.[0]); e.target.value = ''; }}
                />
              </div>
              <div className="pb-xs">
                <h3 className="font-serif text-title-lg text-on-surface">{form.name || 'Restoran'}</h3>
                <p className="font-sans text-caption text-on-surface-variant">
                  Banner və profil şəkli üçün maks. {MAX_IMAGE_MB} MB (JPG/PNG/WebP)
                </p>
              </div>
            </div>
          </div>
        </section>

        {/* ---- Məlumatlar ---- */}
        <section className="bg-surface-container-lowest border border-outline-variant rounded-xl p-md space-y-md elevation-step-1">
          <h3 className="font-sans text-title-lg text-on-surface">Əsas məlumatlar</h3>

          <div className="space-y-xs">
            <label className="font-sans text-label-md text-on-surface-variant">Restoran adı *</label>
            <input type="text" required value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} className={inputClass} />
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-md">
            <div className="space-y-xs">
              <label className="font-sans text-label-md text-on-surface-variant">Mətbəx növü</label>
              <input type="text" value={form.cuisine} onChange={(e) => setForm({ ...form, cuisine: e.target.value })} placeholder="Milli, Avropa..." className={inputClass} />
            </div>
            <div className="space-y-xs">
              <label className="font-sans text-label-md text-on-surface-variant">Şəhər</label>
              <input type="text" value={form.city} onChange={(e) => setForm({ ...form, city: e.target.value })} placeholder="Bakı" className={inputClass} />
            </div>
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

          <div className="space-y-xs">
            <label className="font-sans text-label-md text-on-surface-variant">Təsvir</label>
            <textarea rows={3} value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} placeholder="Restoranınız haqqında..." className={inputClass} />
          </div>

          <button
            type="submit"
            disabled={saving || uploading !== null}
            className="w-full sm:w-auto px-lg py-sm bg-primary text-on-primary rounded-xl font-sans text-label-md hover:opacity-90 active:scale-95 transition-all disabled:opacity-50"
          >
            {saving ? 'Yadda saxlanılır...' : 'Yadda saxla'}
          </button>
        </section>
      </form>
    </div>
  );
}
