import client from './client';

// ---- Qeydiyyat (3 addım) ----
// 1) Email-ə OTP göndər
export const sendOtp = (email) =>
  client.post('/auth/register/initiate', { email });

// 2) OTP kodu yoxla -> tempToken qaytarır
export const verifyOtp = (email, code) =>
  client.post('/auth/register/verify', { email, otpCode: code });

// 3) Şifrə və digər məlumatlarla qeydiyyatı tamamla
// payload: { tempToken, email, fullName, password, accountType: 'USER'|'MANAGER' }
// auth-service yalnız identity saxlayır. Restoran (ad, filial, masa) restaurant-service-də yaradılır.
export const completeRegister = (payload) =>
  client.post('/auth/register/finish', payload);

// ---- Giriş / sessiya ----
export const login = (email, password) =>
  client.post('/auth/login', { email, password });

export const getMe = () => client.get('/auth/me');

export const logoutApi = () => client.post('/auth/logout');
