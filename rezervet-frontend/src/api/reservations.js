import client from './client';

// Login olmuş istifadəçinin rezerv yaratması
// payload: { branchId, tableId, date, time, partySize, note }
export const createReservation = (payload) =>
  client.post('/reservations/reserve', payload);

export const checkAvailability = (payload) =>
  client.post('/reservations/branch/availability', payload);

export const reserveTable = (payload) =>
  client.post('/reservations/branch/reserve', payload);

export const getMyReservations = () => client.get('/reservations/my');

export const cancelReservation = (id) =>
  client.patch(`/reservations/${id}/cancel`);

export const getRestaurantReservations = (id) => client.get(`/reservations/restaurant/${id}`);

export const getBranchReservations = (id) => client.get(`/reservations/branch/${id}`);

export const createManualReservation = (payload) =>
  client.post('/reservations/manual', payload);

