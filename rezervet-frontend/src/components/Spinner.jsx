export default function Spinner({ full = false }) {
  const spinner = (
    <div className="w-8 h-8 border-4 border-primary-fixed border-t-primary rounded-full animate-spin" />
  );
  if (full) {
    return <div className="min-h-screen flex items-center justify-center">{spinner}</div>;
  }
  return <div className="flex justify-center py-lg">{spinner}</div>;
}
