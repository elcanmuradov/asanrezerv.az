export default function ErrorAlert({ error, className = '' }) {
  if (!error) return null;
  const message =
    typeof error === 'string'
      ? error
      : error.response?.data?.message || 'Xəta baş verdi. Yenidən cəhd edin.';
  return (
    <div className={`bg-error-container text-on-error-container rounded-lg px-md py-sm text-body-md flex items-center gap-base ${className}`}>
      <span className="material-symbols-outlined text-[20px]">error</span>
      {message}
    </div>
  );
}
