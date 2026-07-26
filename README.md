# Rezervet.az

Restoranlar üçün onlayn rezervasiya platforması — mikroservis backend (Spring Boot) + React frontend.

## Sürətli başlanğıc (Docker)

```bash
cp .env.example .env      # dəyərləri yoxla/dəyiş
docker compose up --build
```

Sonra:
- **Frontend:** http://localhost:5173
- **API Gateway:** http://localhost:8080 (birbaşa API testi üçün, məs. Postman)

Frontend nginx `/api/*` sorğularını daxili şəbəkədən api-gateway-ə proxy edir — brauzer üçün hər şey eyni origin-dədir, CORS lazım deyil.

## Arxitektura

```
  Brauzer
    │
    ▼
  frontend (nginx :5173)  ──/api──►  api-gateway (:8080)
                                        │  JWT imzasını yoxlayır,
                                        │  X-User-Id / X-User-Role header əlavə edir
                    ┌───────────────────┼───────────────────┐
                    ▼                   ▼                   ▼
              auth-service       restaurant-service    (reservation, subscription... - gələcək)
                    │                   │
                 Postgres (auth_db)  Postgres (restaurant_db)
                    │
                  Redis (OTP, tempToken, refresh token, blacklist)
```

**Təhlükəsizlik modeli:** yalnız `api-gateway` və `frontend` host-a açılır. `auth-service` və `restaurant-service` yalnız daxili Docker şəbəkəsindən əlçatandır — kənardan saxta `X-User-Id` header ilə birbaşa vurmaq mümkün deyil. Gateway gələn `X-User-*` header-lərini həmişə təmizləyir və yalnız imzalanmış JWT-dən çıxardığını qoyur.

## Servislər və portlar (Docker daxili)

| Servis | Daxili port | Host-a açıq? |
|---|---|---|
| frontend (nginx) | 80 | ✅ 5173 |
| api-gateway | 8080 | ✅ 8080 |
| auth-service | 8080 | ❌ yalnız daxili |
| restaurant-service | 8080 | ❌ yalnız daxili |
| postgres | 5432 | ⚠️ 5432 (yalnız debug) |
| redis | 6379 | ⚠️ 6379 (yalnız debug) |

## ⚠️ Bilinməli məqamlar

- **Spring Cloud versiyası:** `api-gateway/build.gradle`-də `springCloudVersion = 2025.1.0` təxminidir. Spring Boot 4.1 çox yenidir; uyğun gəlməzsə [start.spring.io](https://start.spring.io)-da "Gateway" seçib düzgün BOM versiyasını və starter adını götür (reactive gateway starter bəzi versiyalarda `spring-cloud-starter-gateway-server-webflux` adlanır).
- **Java 26 image:** Dockerfile-lar `eclipse-temurin:26-jdk` istifadə edir. Əgər bu tag hələ mövcud deyilsə `25-jdk`-ya endir (build.gradle toolchain-i də).
- `.env` faylındakı `JWT_SECRET` **auth-service və api-gateway-də eyni** olmalıdır (compose bunu avtomatik hər ikisinə ötürür).
```
