import client from './client';

// Ofisiantın təyin olunduğu filiallar
export const getMyBranch = () => client.get('/waiter/branch');

// Filialdakı masalar (statusları ilə)
export const getWaiterTables = (branchId) =>
  client.get(`/waiter/branches/${branchId}/tables`);

// Masa statusunu dəyiş: AVAILABLE | OCCUPIED | RESERVED | CLEANING
export const updateTableStatus = (tableId, status) =>
  client.patch(`/waiter/tables/${tableId}/status`, { status });

// Qeyd: rezerv çağırışları (əl ilə rezerv, bugünkü rezervlər) reservation-service-dədir
// və `api/reservations.js`-də saxlanılır — waiter.js yalnız masa/filial (staff-service) üçündür.
