import client from './client';

// Login olmuş istifadəçinin rezerv yaratması
// payload: { restaurantId, branchId, tableId, date, startTime, guestCount, guestName, guestPhone, note, duration }
export const checkAvailability = (payload) =>
  client.post('/reservations/branch/availability', payload);

export const reserveTable = (payload) =>
  client.post('/reservations/branch/reserve', payload);

export const getMyReservations = () => client.get('/reservations/my');

export const cancelReservation = (id) =>
  client.patch(`/reservations/${id}/cancel`);

// status: 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'COMPLETED' | 'NO_SHOW'
export const updateReservationStatus = (id, status) =>
  client.patch(`/reservations/${id}/status`, null, { params: { status } });

export const getRestaurantReservations = (id) => client.get(`/reservations/restaurant/${id}`);

export const getBranchReservations = (id) => client.get(`/reservations/branch/${id}`);

// payload: { restaurantId, branchId, tableId (opsional), date, startTime, duration, guestCount, guestName, guestPhone, note }
export const createManualReservation = (payload) =>
  client.post('/reservations/manual', payload);

