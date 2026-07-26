import client from './client';

export const getAdminStats = () => client.get('/admin/stats');

export const getAdminRestaurants = (params = {}) =>
  client.get('/admin/restaurants', { params });
export const updateRestaurantStatus = (id, status) =>
  client.patch(`/admin/restaurants/${id}/status`, { status }); // ACTIVE | SUSPENDED

export const getAdminSubscriptions = (params = {}) =>
  client.get('/admin/subscriptions', { params });

export const getAdminPlans = () => client.get('/admin/plans');
// payload: { name, monthlyPrice, maxBranches, maxTablesPerBranch, visibilityLevel, description, features[], popular }
export const createPlan = (payload) => client.post('/admin/plans', payload);
export const updatePlan = (id, payload) => client.put(`/admin/plans/${id}`, payload);

export const getAdminUsers = (params = {}) =>
  client.get('/admin/users', { params });
