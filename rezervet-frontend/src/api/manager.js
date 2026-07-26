import client from './client';



// ---- İşçilər (ofisiantlar) ----
export const getStaff = () => client.get('/manager/staff');
export const createWaiter = (payload) => client.post('/manager/staff', payload); // { fullName, email, branchId }
export const deleteWaiter = (id) => client.delete(`/manager/staff/${id}`);

// ---- Rezervlər / analitika ----
export const getAnalyticsSummary = (params = {}) =>
  client.get('/ai/summary', { params });
export const getMonthlyReservationStats = (params = {}) =>
  client.get('/ai/reservations-monthly', { params });


