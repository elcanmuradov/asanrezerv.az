export default function Footer() {
  return (
    <footer className="bg-surface-container-lowest border-t border-outline-variant mt-auto">
      <div className="w-full py-lg px-gutter max-w-container-max mx-auto flex flex-col md:flex-row justify-between items-center gap-md">
        <div className="flex flex-col items-center md:items-start">
          <span className="font-serif text-headline-md text-primary font-bold mb-2">Rezervet</span>
          <p className="font-sans text-caption text-on-surface-variant max-w-xs text-center md:text-left">
            © {new Date().getFullYear()} Rezervet.az. Restoranlar üçün onlayn rezervasiya platforması.
          </p>
        </div>
        <div className="flex flex-wrap justify-center gap-lg">
          <a className="font-sans text-body-md text-on-surface-variant hover:text-primary transition-colors" href="#">Məxfilik</a>
          <a className="font-sans text-body-md text-on-surface-variant hover:text-primary transition-colors" href="#">Şərtlər</a>
          <a className="font-sans text-body-md text-on-surface-variant hover:text-primary transition-colors" href="/qiymetler">Restoran portalı</a>
          <a className="font-sans text-body-md text-on-surface-variant hover:text-primary transition-colors" href="#">Dəstək</a>
        </div>
      </div>
    </footer>
  );
}
