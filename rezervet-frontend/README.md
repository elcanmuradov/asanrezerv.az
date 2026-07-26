# Rezervet.az — Frontend

React + Vite + Tailwind CSS ilə yazılmış frontend. Bütün UI Azərbaycan dilindədir. Mock data **yoxdur** — bütün siyahılar API-dən gəlir, backend hazır olana qədər boş görünür.

## Quraşdırma

```bash
npm install
npm run dev        # http://localhost:5173
```

`/api` sorğuları Vite proxy ilə `http://localhost:8080`-ə (Spring API Gateway) yönlənir — bax: `vite.config.js`. Fərqli ünvan üçün `.env` faylında `VITE_API_BASE_URL` təyin et.

## Struktur

```
src/
├── api/           # Bütün backend çağırışları (endpoint müqavilələri burada)
├── context/       # AuthContext — JWT saxlama, login/logout, rol
├── components/    # Ortaq komponentlər (Navbar, PanelLayout, EmptyState...)
└── pages/
    ├── public/    # Home (restoran listi), RestaurantDetail, Pricing
    ├── auth/      # Login, Register (3 addımlı OTP axını)
    ├── user/      # MyReservations
    ├── manager/   # Dashboard, Branches, Tables, Reservations, Staff, Subscription
    ├── waiter/    # FloorPlan (masa statusları + əl ilə rezerv)
    └── admin/     # Dashboard, Restaurants, Subscriptions
```

## Backend-in gözlədiyi endpoint-lər

Bütün endpoint-lər `src/api/*.js` fayllarında bir yerdə toplanıb — backend hazır olduqda yalnız bu faylları backend-in real müqaviləsinə uyğunlaşdırmaq kifayətdir.

### Auth (`api/auth.js`)
| Metod | Endpoint | Body / Cavab |
|---|---|---|
| POST | `/auth/register/initiate` | `{ email }` |
| POST | `/auth/register/verify` | `{ email, code }` → `{ verificationToken }` |
| POST | `/auth/register/finish` | `{ verificationToken, email, fullName, phone, password, accountType, restaurantName? }` |
| POST | `/auth/login` | `{ email, password }` → `{ accessToken, refreshToken, user }` |
| POST | `/auth/refresh` | `{ refreshToken }` → `{ accessToken }` |
| GET | `/auth/me` | → `{ id, email, fullName, role }` (role: ADMIN\|MANAGER\|WAITER\|USER) |

### Public (`api/restaurants.js`, `api/subscriptions.js`)
- `GET /restaurants?search=` → `[{ id, name, cuisine, city, rating, description, coverImageUrl, branchCount, featured }]`
- `GET /restaurants/{id}`, `GET /restaurants/{id}/branches`
- `GET /branches/{id}/availability?date&time&partySize` → `[{ tableId, tableName, capacity, zone, time }]`
- `GET /subscriptions/plans` → `[{ id, name, monthlyPrice, description, features[], popular }]`

### User (`api/reservations.js`)
- `POST /reservations` — `{ branchId, tableId, date, time, partySize, note }`
- `GET /reservations/my`, `PATCH /reservations/{id}/cancel`

### Manager (`api/manager.js`)
- Filial CRUD: `/manager/branches`
- Masa CRUD: `/manager/branches/{id}/tables`, `/manager/tables/{id}`
- İşçi: `/manager/staff` (POST: `{ fullName, email, branchIds[] }`)
- Rezervlər: `GET /manager/reservations?branchId&date`
- Analitika: `GET /manager/analytics/summary`, `GET /manager/analytics/reservations-monthly`
- Abunə: `GET /subscriptions/current`, `POST /subscriptions/checkout` → `{ paymentUrl }` (epoint)

### Waiter (`api/waiter.js`)
- `GET /waiter/branches`, `GET /waiter/branches/{id}/tables`
- `PATCH /waiter/tables/{id}/status` — `{ status: AVAILABLE|OCCUPIED|RESERVED|CLEANING }`
- `POST /waiter/reservations` (əl ilə/zənglə rezerv), `GET /waiter/branches/{id}/reservations/today`

### Admin (`api/admin.js`)
- `GET /admin/stats`, `GET /admin/restaurants`, `PATCH /admin/restaurants/{id}/status`
- `GET /admin/subscriptions`, `GET /admin/users`

## Auth səhifələri — istifadəçi və restoran ayrı

| Route | Kim üçün | Nəticə rol |
|---|---|---|
| `/login`, `/register` | Adi istifadəçilər | `USER` (login: USER, ADMIN qəbul edir) |
| `/biznes/login`, `/biznes/register` | Restoranlar | `MANAGER` avtomatik (login: MANAGER, WAITER qəbul edir) |

Yanlış tipli hesabla giriş edildikdə sessiya bağlanır və düzgün giriş səhifəsinə yönləndirici mesaj göstərilir. Qorunan panel route-ları (`/manager`, `/waiter`) login olmayanı `/biznes/login`-ə yönləndirir.

## Qeydiyyat axını (3 addım)
1. E-poçt daxil edilir → `initiate`
2. 6 rəqəmli OTP kod → `verify` → `verificationToken`
3. Ad, telefon, şifrə → `finish`

Biznes qeydiyyatında (`/biznes/register`) əlavə `restaurantName` sahəsi göstərilir və `accountType: 'MANAGER'` göndərilir — backend MANAGER rolu ilə hesab + restoran yaradır. Adi qeydiyyatda `accountType: 'USER'` gedir.
