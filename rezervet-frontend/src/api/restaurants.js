import client from './client';

// Public: restoranların siyahısı (search-service / Elasticsearch üzərindən oxunur, yalnız PUBLISHED olanlar)
// { search, city, page, size } — `search` varsa mətn axtarışı, yoxdursa sadə siyahı (hər ikisi 0-əsaslı səhifələmə).
// `city` filtri hazırda search-service-də dəstəklənmir (iqnor olunur).
export const getRestaurants = ({ search, page = 0, size = 20 } = {}) => {
  if (search) {
    return client.get('/search/restaurants/search', { params: { query: search, page, size } });
  }
  return client.get('/search/restaurants', { params: { page, size } });
};

export const getRestaurant = (id) => client.get(`/search/restaurants/${id}`);

export const getRestaurantBranches = (id) =>
  client.get(`/search/restaurants/${id}/branches`);

// Seçilmiş filial üçün uyğun masalar / slotlar
// { date, time, partySize }

// ---- Filiallar ----
export const getBranches = () => client.get('/restaurants/branches');
export const createBranch = (payload) => client.post('/restaurants/branches', payload); // { name, address, phone, openingTime, closingTime, workingHours }
export const updateBranch = (id, payload) => client.put(`/restaurants/branches/${id}`, payload);
export const deleteBranch = (id) => client.delete(`/restaurants/branches/${id}`);

// ---- Masalar ----
export const getTables = (branchId) => client.get(`/restaurants/branches/${branchId}/tables`);
export const createTable = (branchId, payload) =>
  client.post(`/restaurants/branches/${branchId}/tables`, payload); // { name, capacity, zone }
export const updateTable = (tableId, payload) => client.put(`/restaurants/tables/${tableId}`, payload);
export const deleteTable = (tableId) => client.delete(`/restaurants/tables/${tableId}`);

// ---- Restoran profili ----
export const getMyRestaurant = () => client.get('/restaurants/restaurant');
export const createMyRestaurant = (payload) => client.post('/restaurants/restaurant', payload); // { name, cuisine, city, phone, description }
export const updateMyRestaurant = (payload) => client.put('/restaurants/restaurant', payload);
// { name, cuisine, city, phone, description, bannerUrl, profileImageUrl }

// Restoranı DRAFT-dan PUBLISHED-ə keçirir (ana səhifədə görünməsi üçün)
export const publishRestaurant = (id) => client.patch(`/restaurants/${id}/publish`);

// Şəkil yükləmə: type = 'BANNER' | 'PROFILE' | 'GALLERY' | 'MENU_ITEM'. Multipart göndərir, yüklənmiş şəklin URL-ini qaytarır.
export const uploadRestaurantImage = (type, file) => {
  const form = new FormData();
  form.append('file', file);
  const photoType = typeof type === 'string' ? type.toUpperCase() : type;
  return client.post(`/restaurants/restaurant/image?type=${photoType}`, form, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
};