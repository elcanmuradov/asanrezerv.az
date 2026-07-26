import axios from 'axios';

// Bütün sorğular API Gateway-ə gedir. Endpoint-lər backend hazır olduqda
// buradakı baza ünvanla birləşir.
const client = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  headers: { 'Content-Type': 'application/json' },
});

client.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// Backend hər cavabı ApiResponse<T> zərfinə bükür: { data: T, success: boolean }.
// Bu funksiya zərfi açır ki, qalan kod birbaşa əsl payload ilə işləsin.
function unwrapEnvelope(payload) {
  if (payload && typeof payload === 'object' && 'success' in payload && 'data' in payload) {
    return payload.data;
  }
  return payload;
}

client.interceptors.response.use(
  (res) => {
    res.data = unwrapEnvelope(res.data);
    return res;
  },
  async (error) => {
    // Xəta halında da zərfi açırıq: { data: "mesaj", success: false } -> { message: "mesaj" }
    if (error.response?.data && typeof error.response.data === 'object' && 'success' in error.response.data) {
      const inner = unwrapEnvelope(error.response.data);
      error.response.data = { message: typeof inner === 'string' ? inner : inner?.message };
    }

    const original = error.config;
    // Access token bitibsə refresh token ilə yenilə, sonra sorğunu təkrarla
    if (error.response?.status === 401 && !original._retry) {
      original._retry = true;
      const refreshToken = localStorage.getItem('refreshToken');
      if (refreshToken) {
        try {
          const { data } = await axios.post(
            `${client.defaults.baseURL}/auth/refresh`,
            { refreshToken }
          );
          const tokens = unwrapEnvelope(data);
          localStorage.setItem('accessToken', tokens.jwtToken);
          if (tokens.refreshToken) localStorage.setItem('refreshToken', tokens.refreshToken);
          original.headers.Authorization = `Bearer ${tokens.jwtToken}`;
          return client(original);
        } catch {
          localStorage.removeItem('accessToken');
          localStorage.removeItem('refreshToken');
          window.location.href = '/login';
        }
      }
    }
    return Promise.reject(error);
  }
);

export default client;
