import { useRef, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { sendOtp, verifyOtp, completeRegister } from '../../api/auth';
import ErrorAlert from '../../components/ErrorAlert';

const OTP_LENGTH = 6;

// 3 addımlı qeydiyyat:
// 1) E-poçt -> OTP göndərilir
// 2) OTP kodun təsdiqi -> tempToken
// 3) Şifrə + digər məlumatlar -> hesab yaradılır
// mode: 'user' -> USER rolu | 'business' -> avtomatik MANAGER rolu + restoran adı
export default function Register({ mode = 'user' }) {
  const isBusiness = mode === 'business';
  const accountType = isBusiness ? 'MANAGER' : 'USER';
  const navigate = useNavigate();
  const [step, setStep] = useState(1);
  const [error, setError] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  const [email, setEmail] = useState('');
  const [otp, setOtp] = useState(Array(OTP_LENGTH).fill(''));
  const otpRefs = useRef([]);
  const [tempToken, setTempToken] = useState(null);

  const [fullName, setFullName] = useState('');
  const [password, setPassword] = useState('');
  const [passwordConfirm, setPasswordConfirm] = useState('');

  // ---- Addım 1: OTP göndər ----
  const handleSendOtp = async (e) => {
    e.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      await sendOtp(email);
      setStep(2);
    } catch (err) {
      setError(err);
    } finally {
      setSubmitting(false);
    }
  };

  // ---- Addım 2: OTP yoxla ----
  const handleOtpChange = (index, value) => {
    if (!/^\d?$/.test(value)) return;
    const next = [...otp];
    next[index] = value;
    setOtp(next);
    if (value && index < OTP_LENGTH - 1) otpRefs.current[index + 1]?.focus();
  };

  const handleOtpKeyDown = (index, e) => {
    if (e.key === 'Backspace' && !otp[index] && index > 0) {
      otpRefs.current[index - 1]?.focus();
    }
  };

  const handleVerifyOtp = async (e) => {
    e.preventDefault();
    setError(null);
    const code = otp.join('');
    if (code.length !== OTP_LENGTH) {
      setError(`${OTP_LENGTH} rəqəmli kodu tam daxil edin.`);
      return;
    }
    setSubmitting(true);
    try {
      const { data } = await verifyOtp(email, code);
      setTempToken(data.tempToken);
      setStep(3);
    } catch (err) {
      setError(err);
    } finally {
      setSubmitting(false);
    }
  };

  const handleResendOtp = async () => {
    setError(null);
    setSubmitting(true);
    try {
      await sendOtp(email);
      setOtp(Array(OTP_LENGTH).fill(''));
    } catch (err) {
      setError(err);
    } finally {
      setSubmitting(false);
    }
  };

  // ---- Addım 3: Qeydiyyatı tamamla ----
  const handleComplete = async (e) => {
    e.preventDefault();
    setError(null);
    if (password.length < 8) {
      setError('Şifrə ən azı 8 simvol olmalıdır.');
      return;
    }
    if (password !== passwordConfirm) {
      setError('Şifrələr eyni deyil.');
      return;
    }
    setSubmitting(true);
    try {
      await completeRegister({
        tempToken,
        email,
        fullName,
        password,
        accountType,
      });
      navigate(isBusiness ? '/biznes/login' : '/login', { state: { registered: true } });
    } catch (err) {
      setError(err);
    } finally {
      setSubmitting(false);
    }
  };

  const inputClass =
    'w-full px-md py-sm bg-surface-container rounded-lg border border-outline-variant focus:ring-2 focus:ring-primary focus:border-primary outline-none text-body-md';

  return (
    <div className="min-h-screen bg-background flex items-center justify-center px-gutter py-lg">
      <div className="w-full max-w-md">
        <div className="text-center mb-lg">
          <Link to="/" className="font-serif text-display-lg-mobile font-bold text-primary">AsanRezerv</Link>
          {isBusiness && (
            <div className="inline-flex items-center gap-xs bg-primary-container text-on-primary-container px-md py-1.5 rounded-full font-sans text-label-md mt-sm">
              <span className="material-symbols-outlined text-[18px]">storefront</span>
              Restoran qeydiyyatı
            </div>
          )}
          <p className="text-on-surface-variant text-body-md mt-xs">
            {isBusiness ? 'Restoranınızı platformada qeydiyyatdan keçirin' : 'Yeni hesab yaradın'}
          </p>
        </div>

        {/* Addım göstəricisi */}
        <div className="flex items-center justify-center gap-base mb-md">
          {[1, 2, 3].map((s) => (
            <div key={s} className="flex items-center gap-base">
              <div
                className={`w-8 h-8 rounded-full flex items-center justify-center font-sans text-label-md ${s < step
                    ? 'bg-primary text-on-primary'
                    : s === step
                      ? 'bg-primary-container text-on-primary-container'
                      : 'bg-surface-container text-on-surface-variant'
                  }`}
              >
                {s < step ? <span className="material-symbols-outlined text-[18px]">check</span> : s}
              </div>
              {s < 3 && <div className={`w-10 h-0.5 ${s < step ? 'bg-primary' : 'bg-outline-variant'}`} />}
            </div>
          ))}
        </div>

        <div className="bg-surface-container-lowest border border-outline-variant rounded-xl p-md elevation-step-1">
          <ErrorAlert error={error} className="mb-md" />

          {/* ---- Addım 1: E-poçt ---- */}
          {step === 1 && (
            <form onSubmit={handleSendOtp} className="space-y-md">
              <div className="text-center">
                <h2 className="font-serif text-title-lg text-on-surface">E-poçt ünvanınız</h2>
                <p className="text-on-surface-variant text-body-md mt-xs">
                  Təsdiq kodu bu ünvana göndəriləcək.
                </p>
              </div>
              <div className="space-y-xs">
                <label className="font-sans text-label-md text-on-surface-variant">E-poçt</label>
                <input
                  type="email"
                  required
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="numune@gmail.com"
                  className={inputClass}
                />
              </div>
              <button
                type="submit"
                disabled={submitting}
                className="w-full py-sm bg-primary text-on-primary rounded-xl font-sans text-title-lg hover:opacity-90 active:scale-95 transition-all disabled:opacity-50"
              >
                {submitting ? 'Göndərilir...' : 'Kod göndər'}
              </button>
            </form>
          )}

          {/* ---- Addım 2: OTP təsdiqi ---- */}
          {step === 2 && (
            <form onSubmit={handleVerifyOtp} className="space-y-md">
              <div className="text-center">
                <h2 className="font-serif text-title-lg text-on-surface">Kodu daxil edin</h2>
                <p className="text-on-surface-variant text-body-md mt-xs">
                  <span className="font-bold text-on-surface">{email}</span> ünvanına göndərilən{' '}
                  {OTP_LENGTH} rəqəmli kodu yazın.
                </p>
              </div>
              <div className="flex justify-center gap-base">
                {otp.map((digit, i) => (
                  <input
                    key={i}
                    ref={(el) => (otpRefs.current[i] = el)}
                    type="text"
                    inputMode="numeric"
                    maxLength={1}
                    value={digit}
                    onChange={(e) => handleOtpChange(i, e.target.value)}
                    onKeyDown={(e) => handleOtpKeyDown(i, e)}
                    className="w-12 h-14 text-center text-title-lg font-bold bg-surface-container rounded-lg border border-outline-variant focus:ring-2 focus:ring-primary focus:border-primary outline-none"
                  />
                ))}
              </div>
              <button
                type="submit"
                disabled={submitting}
                className="w-full py-sm bg-primary text-on-primary rounded-xl font-sans text-title-lg hover:opacity-90 active:scale-95 transition-all disabled:opacity-50"
              >
                {submitting ? 'Yoxlanılır...' : 'Təsdiqlə'}
              </button>
              <div className="flex justify-between text-body-md">
                <button type="button" onClick={() => { setStep(1); setError(null); }} className="text-on-surface-variant hover:text-primary transition-colors">
                  ← E-poçtu dəyiş
                </button>
                <button type="button" onClick={handleResendOtp} disabled={submitting} className="text-primary font-sans text-label-md hover:underline disabled:opacity-50">
                  Kodu yenidən göndər
                </button>
              </div>
            </form>
          )}

          {/* ---- Addım 3: Məlumatlar + şifrə ---- */}
          {step === 3 && (
            <form onSubmit={handleComplete} className="space-y-md">
              <div className="text-center">
                <h2 className="font-serif text-title-lg text-on-surface">Hesab məlumatları</h2>
                <p className="text-on-surface-variant text-body-md mt-xs">Son addım — məlumatlarınızı tamamlayın.</p>
              </div>

              <div className="space-y-xs">
                <label className="font-sans text-label-md text-on-surface-variant">Ad Soyad</label>
                <input type="text" required value={fullName} onChange={(e) => setFullName(e.target.value)} placeholder="Ad Soyad" className={inputClass} />
              </div>
              {isBusiness && (
                <div className="bg-surface-container-high rounded-lg px-md py-sm flex items-start gap-base">
                  <span className="material-symbols-outlined text-primary text-[20px]">info</span>
                  <p className="font-sans text-caption text-on-surface-variant">
                    Restoran məlumatlarını (ad, filiallar, masalar) qeydiyyatdan sonra panelinizdən əlavə edəcəksiniz.
                  </p>
                </div>
              )}
              <div className="space-y-xs">
                <label className="font-sans text-label-md text-on-surface-variant">Şifrə</label>
                <input type="password" required value={password} onChange={(e) => setPassword(e.target.value)} placeholder="Ən azı 8 simvol" className={inputClass} />
              </div>
              <div className="space-y-xs">
                <label className="font-sans text-label-md text-on-surface-variant">Şifrənin təkrarı</label>
                <input type="password" required value={passwordConfirm} onChange={(e) => setPasswordConfirm(e.target.value)} placeholder="Şifrəni təkrar yazın" className={inputClass} />
              </div>
              <button
                type="submit"
                disabled={submitting}
                className="w-full py-sm bg-primary text-on-primary rounded-xl font-sans text-title-lg hover:opacity-90 active:scale-95 transition-all disabled:opacity-50"
              >
                {submitting ? 'Yaradılır...' : 'Qeydiyyatı tamamla'}
              </button>
            </form>
          )}
        </div>

        <p className="text-center text-body-md text-on-surface-variant mt-md">
          Artıq hesabınız var?{' '}
          <Link to={isBusiness ? '/biznes/login' : '/login'} className="text-primary font-sans text-label-md hover:underline">Daxil olun</Link>
        </p>

        {/* Qarşı tərəfə keçid */}
        <div className="text-center mt-sm">
          <Link
            to={isBusiness ? '/register' : '/biznes/register'}
            className="inline-flex items-center gap-xs text-on-surface-variant hover:text-primary font-sans text-label-md transition-colors"
          >
            <span className="material-symbols-outlined text-[18px]">
              {isBusiness ? 'person' : 'storefront'}
            </span>
            {isBusiness ? 'İstifadəçi kimi qeydiyyat' : 'Restoran kimi qeydiyyat'}
          </Link>
        </div>
      </div>
    </div>
  );
}
