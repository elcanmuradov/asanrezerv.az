import client from './client';

// Public: paket kataloqu (Starter / Standart / Premium)
export const getPlans = () => client.get('/subscriptions/plans');

// Manager: cari abunə — backend `restaurant-id` query param tələb edir
export const getCurrentSubscription = (restaurantId) =>
  client.get('/subscriptions/current', { params: { 'restaurant-id': restaurantId } });

// Manager: paket seçimi -> yaradılmış/yenilənmiş SubscriptionDto qaytarır
// Backend query param gözləyir: ?restaurant-id=<uuid>&plan-id=<uuid>&auto-renew=<boolean> (body yoxdur)
export const startCheckout = (restaurantId, planId, autoRenew) =>
  client.post(
    `/subscriptions/checkout?restaurant-id=${restaurantId}&plan-id=${planId}&auto-renew=${autoRenew}`
  );
