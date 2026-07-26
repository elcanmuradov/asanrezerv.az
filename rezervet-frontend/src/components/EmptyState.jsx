// Boş siyahılar üçün ümumi görünüş — mock data əvəzinə istifadə olunur
export default function EmptyState({ icon = 'inbox', title, subtitle }) {
  return (
    <div className="flex flex-col items-center justify-center text-center py-xl px-md">
      <div className="w-16 h-16 bg-surface-container-high rounded-full flex items-center justify-center mb-md">
        <span className="material-symbols-outlined text-primary text-3xl">{icon}</span>
      </div>
      <h3 className="font-serif text-title-lg text-on-surface">{title}</h3>
      {subtitle && (
        <p className="text-on-surface-variant text-body-md mt-xs max-w-md">{subtitle}</p>
      )}
    </div>
  );
}
