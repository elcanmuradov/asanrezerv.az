# AsanRezerv.az — Backend Microservices

> **AsanRezerv.az** — Azərbaycanlı restoran rezervasiya platforması üçün hazırlanmış müasir, scalable mikroservis arxitekturası.

---

## 📋 Mündəricat

- [Layihə haqqında](#layihə-haqqında)
- [Arxitektura](#arxitektura)
- [Servisler](#servisler)
- [Texnoloji Stack](#texnoloji-stack)
- [Başlamaq](#başlamaq)
- [Environment Dəyişənləri](#environment-dəyişənləri)
- [API Endpointlər](#api-endpointlər)
- [Layihə Strukturu](#layihə-strukturu)

---

## Layihə haqqında

AsanRezerv.az backend hissəsi tamamilə mikroservis arxitekturasına əsaslanır. Hər servis öz məsuliyyət sahəsini örtür, müstəqil deploy oluna bilər və Docker vasitəsilə idarə edilir. Bütün xarici sorğular **API Gateway** üzərindən keçir.

---

## Arxitektura

```
                        ┌─────────────────┐
                        │   API Gateway   │  :8080
                        │  (WebFlux + JWT)│
                        └────────┬────────┘
                                 │
          ┌──────────────────────┼───────────────────────┐
          │          │           │           │            │
   ┌──────▼──┐ ┌─────▼───┐ ┌────▼────┐ ┌───▼──────┐ ┌──▼────────┐
   │  Auth   │ │Restaurant│ │Reservat.│ │Subscript.│ │  Staff    │
   │ Service │ │ Service  │ │ Service │ │  Service │ │  Service  │
   └─────────┘ └─────────┘ └─────────┘ └──────────┘ └───────────┘
          │          │                       │
   ┌──────▼──┐ ┌─────▼───┐            ┌─────▼──────┐
   │  Search │ │  Media  │            │Notification│
   │ Service │ │ Service │            │  Service   │
   └─────────┘ └─────────┘            └────────────┘
                                       ┌────────────┐
                                       │ AI Analysis│
                                       │  Service   │
                                       └────────────┘

Infrastructure:
  PostgreSQL · Redis · Apache Kafka · Elasticsearch
```

---

## Servisler

| Servis | Təsvir |
|--------|--------|
| **api-gateway** | Bütün sorğuları yönləndirən şlüz; JWT yoxlaması, routing |
| **auth-service** | Qeydiyyat (OTP), login, logout, token refresh, Google OAuth2 |
| **restaurant-service** | Restoran, masa, menyu idarəetməsi |
| **reservation-service** | Rezervasiya yaratma, ləğv etmə, tarixçə |
| **staff-service** | Restoran işçiləri, masa statusu idarəetməsi |
| **subscription-service** | Premium abunəlik planları, ödəniş statusu |
| **search-service** | Elasticsearch əsaslı restoran axtarışı |
| **media-service** | Cloudinary üzərindən şəkil/media yükləmə |
| **notification-service** | E-poçt bildirişləri (Spring Mail) |
| **ai-analysis-service** | Stateless analitika servisi (OpenFeign + Redis cache) |

---

## Texnoloji Stack

### Core

| Texnologiya | Versiya |
|-------------|---------|
| Java | 26 |
| Spring Boot | 4.1.0 |
| Spring Cloud | 2025.1.x |
| Gradle | 8.x |

### Per-Servis Texnologiyalar

| Texnologiya | Hansı servislərdə istifadə olunur |
|-------------|----------------------------------|
| Spring Cloud Gateway (WebFlux) | api-gateway |
| Spring Security + JWT (JJWT 0.13) | api-gateway, auth-service |
| Spring Security OAuth2 Client (Google) | auth-service |
| Spring Data JPA + PostgreSQL | auth-service, restaurant-service, reservation-service, staff-service, subscription-service |
| Spring Data Redis | auth-service, restaurant-service, reservation-service, search-service, subscription-service, ai-analysis-service |
| Apache Kafka | auth-service, restaurant-service, search-service, subscription-service |
| Elasticsearch | search-service |
| Spring OpenFeign | restaurant-service, reservation-service, staff-service, subscription-service, ai-analysis-service |
| Cloudinary SDK | media-service |
| Spring Mail | notification-service |
| MapStruct | auth-service |
| Lombok | Bütün servisler |

---


## Environment Dəyişənləri

### API Gateway

| Dəyişən | Default | Təsvir |
|---------|---------|--------|
| `SPRING_SECURITY_SECRET_KEY` | *(uzun hex string)* | JWT imzalama açarı |
| `SERVICES_AUTH_URI` | `http://auth-service:8080` | Auth servisin URL-i |
| `SERVICES_RESTAURANT_URI` | `http://restaurant-service:8080` | Restaurant servisin URL-i |
| `SERVICES_RESERVATION_URI` | `http://reservation-service:8080` | Reservation servisin URL-i |
| `SERVICES_SUBSCRIPTION_URI` | `http://subscription-service:8080` | Subscription servisin URL-i |
| `SERVICES_STAFF_URI` | `http://staff-service:8080` | Staff servisin URL-i |
| `SERVICES_AI_URI` | `http://ai-analysis-service:8080` | AI Analysis servisin URL-i |
| `SERVICES_SEARCH_URI` | `http://search-service:8080` | Search servisin URL-i |

### Auth Service

| Dəyişən | Default | Təsvir |
|---------|---------|--------|
| `SPRING_SECURITY_SECRET_KEY` | *(uzun hex string)* | JWT imzalama açarı (Gateway ilə eyni olmalıdır) |
| `SPRING_DATA_REDIS_HOST` | `localhost` | Redis host |
| `SPRING_DATA_REDIS_PORT` | `6379` | Redis port |

> ⚠️ **Diqqət:** JWT `secret-key` hər iki servisdə mütləq eyni olmalıdır. Production-da bu dəyər environment dəyişəni kimi verilməlidir.

---

## API Endpointlər

Bütün sorğular `http://localhost:8080` (API Gateway) üzərindən keçir.

### Auth `/api/auth`

| Method | Endpoint | Auth | Təsvir |
|--------|----------|------|--------|
| `POST` | `/api/auth/register/initiate` | Yox | OTP kodu göndər (e-poçt) |
| `POST` | `/api/auth/register/verify` | Yox | OTP kodunu yoxla |
| `POST` | `/api/auth/register/finish` | Yox | Qeydiyyatı tamamla |
| `POST` | `/api/auth/login` | Yox | Giriş et, JWT al |
| `POST` | `/api/auth/logout` | ✅ Bearer | Çıxış et |
| `POST` | `/api/auth/refresh` | Yox | Refresh token ilə yeni JWT al |
| `GET`  | `/api/auth/me` | ✅ Bearer | Cari istifadəçi məlumatları |

---

## Layihə Strukturu

```
asan-backend/
├── api-gateway/                  # Spring Cloud Gateway (WebFlux)
├── auth-service/                 # Autentifikasiya servisi
├── restaurant-service/           # Restoran idarəetmə servisi
├── reservation-service/          # Rezervasiya servisi
├── staff-service/                # İşçi idarəetmə servisi (OkHttp + Feign PATCH)
├── subscription-service/         # Abunəlik servisi
├── search-service/               # Elasticsearch axtarış servisi
├── media-service/                # Cloudinary media servisi
├── notification-service/         # E-poçt bildiriş servisi
└── ai-analysis-service/          # Stateless AI analitika servisi (DB yox)
```

---

## Qeydlər

- **Spring Cloud uyğunluq yoxlaması** API Gateway-də söndürülüb (`compatibility-verifier: enabled: false`), çünki Spring Cloud verifier Boot 4.0.x tələb edir, lakin runtime-da 4.1 işləyir.
- **Staff Service** JDK-in default Feign client-i (`HttpURLConnection`) `PATCH` metodunu dəstəkləmədiyindən OkHttp istifadə edir.
- **AI Analysis Service** tamamilə stateless-dir — JPA və PostgreSQL asılılığı yoxdur, yalnız Redis cache istifadə edir.
- Bütün servisler **Java 26** toolchain tələb edir.

---

<p align="center">
  Built with ❤️ for Azerbaijan 🇦🇿
</p>
