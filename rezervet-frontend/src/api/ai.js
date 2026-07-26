import client from './client';

// Frontend nəyi göstərəcəyini bilsin: { level, canBasic, canAdvanced, features[] }
export const getAiAccess = () => client.get('/ai/access');

// Level >= 1: { totalReservations, totalCapacity, avgOccupancyPercent, peakHours, channelSplit, noShowRate, monthlyTrend[] }
export const getAiSummary = () => client.get('/ai/summary');

// Level >= 2: { days: [{ date, weekday, expectedReservations }] }
export const getAiForecast = () => client.get('/ai/forecast');

// Level >= 2: { narrative, llmGenerated, recommendations[] }
export const getAiInsights = () => client.get('/ai/insights');
