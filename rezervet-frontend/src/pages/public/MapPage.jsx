import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import L from 'leaflet';
import markerIcon2x from 'leaflet/dist/images/marker-icon-2x.png';
import markerIcon from 'leaflet/dist/images/marker-icon.png';
import markerShadow from 'leaflet/dist/images/marker-shadow.png';
import 'leaflet/dist/leaflet.css';
import PublicNavbar from '../../components/PublicNavbar';
import Footer from '../../components/Footer';
import Spinner from '../../components/Spinner';
import ErrorAlert from '../../components/ErrorAlert';
import EmptyState from '../../components/EmptyState';
import { getMapBranches } from '../../api/restaurants';

// Vite bundle-də leaflet-in default marker ikonu sınıq gəlir — şəkilləri əl ilə qoşuruq.
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: markerIcon2x,
  iconUrl: markerIcon,
  shadowUrl: markerShadow,
});

const BAKU_CENTER = [40.4093, 49.8671];

// Login tələb etmir — hər kəs bütün dərc olunmuş filialların xəritəsini görə bilər.
export default function MapPage() {
  const [branches, setBranches] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    getMapBranches()
      .then((res) => setBranches(res.data ?? []))
      .catch((err) => setError(err))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="min-h-screen flex flex-col bg-background">
      <PublicNavbar />

      <section className="px-gutter py-lg max-w-container-max mx-auto w-full flex-1 flex flex-col gap-md">
        <div className="flex items-center justify-between flex-wrap gap-sm">
          <div>
            <h1 className="font-serif text-headline-md text-on-background">Restoran xəritəsi</h1>
            <p className="text-on-surface-variant font-sans text-body-md mt-xs">
              Bütün filialların yerini xəritədə görün.
            </p>
          </div>
          {!loading && !error && (
            <span className="bg-secondary-container text-on-secondary-container px-sm py-xs rounded-full font-sans text-label-md">
              {branches.length} filial
            </span>
          )}
        </div>

        <ErrorAlert error={error} />

        {loading ? (
          <Spinner />
        ) : branches.length === 0 ? (
          <EmptyState
            icon="map"
            title="Xəritədə hələ filial yoxdur"
            subtitle="Menecerlər Google Maps linki əlavə edib restoranlarını dərc etdikcə burada görünəcək."
          />
        ) : (
          <div className="rounded-xl overflow-hidden border border-outline-variant elevation-step-1" style={{ height: '70vh' }}>
            <MapContainer center={BAKU_CENTER} zoom={12} scrollWheelZoom style={{ width: '100%', height: '100%' }}>
              <TileLayer
                attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> müəllifləri'
                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
              />
              {branches
                .filter((b) => b.location?.lat != null && b.location?.lon != null)
                .map((b) => (
                  <Marker key={b.id} position={[b.location.lat, b.location.lon]}>
                    <Popup>
                      <div className="space-y-1">
                        <p className="font-semibold">{b.restaurantName}</p>
                        <p className="text-sm">{b.branchName}</p>
                        {b.address && <p className="text-sm text-gray-600">{b.address}</p>}
                        <Link to={`/restoran/${b.restaurantId}`} className="text-primary text-sm underline">
                          Restorana bax
                        </Link>
                      </div>
                    </Popup>
                  </Marker>
                ))}
            </MapContainer>
          </div>
        )}
      </section>

      <Footer />
    </div>
  );
}
