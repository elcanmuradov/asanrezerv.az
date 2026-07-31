import client from './client';

// Ofisiantın təyin olunduğu filial — { id, restaurantId, name, openingTime, closingTime, ... }
export const getMyBranch = () => client.get('/waiter/branch');

// Filialdakı masalar (statusları ilə) — restaurant-service-dən birbaşa (staff-service-də bu
// marşrut yoxdur, sahiblik yoxlaması olmadığı üçün hər autentifikasiya olunmuş rol çağıra bilər)
export const getWaiterTables = (branchId) =>
  client.get(`/restaurants/branches/${branchId}/tables`);

// Masa statusunu dəyiş: AVAILABLE | OCCUPIED | RESERVED | CLEANING
// DİQQƏT: backend @RequestBody TableStatus gözləyir — çılpaq enum sətri (JSON obyekt yox).
export const updateTableStatus = (tableId, status) =>
  client.patch(`/waiter/tables/${tableId}/status`, JSON.stringify(status), {
    headers: { 'Content-Type': 'application/json' },
  });

// Qeyd: rezerv çağırışları (əl ilə rezerv, bugünkü rezervlər) reservation-service-dədir
// və `api/reservations.js`-də saxlanılır — waiter.js yalnız masa/filial (staff-service) üçündür.
