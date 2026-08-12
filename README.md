# Rezervet.az (asanrezerv.az)

Restoranlar üçün onlayn rezervasiya və idarəetmə platforması. Bu layihə mikroservis memarlığına əsaslanan **Spring Boot** backend servisləri və modern **React** frontend tətbiqindən ibarətdir.

---

## 🚀 Sürətli Başlanğıc (Docker ilə)

Bütün infrastrukturu və mikroservisləri Docker Compose vasitəsilə tək bir komanda ilə ayağa qaldıra bilərsiniz:

```bash
# 1. Ətraf mühit dəyişənlərini nüsxələyin və ehtiyac olduqda dəyişdirin
cp .env.example .env

# 2. Bütün servisləri tikib işə salın
docker compose up --build
```

### Giriş Nöqtələri:
* **Müştəri Paneli & Veb Sayt (Frontend):** [http://localhost:5173](http://localhost:5173)
* **API Gateway (Arxa keçid):** [http://localhost:8080](http://localhost:8080) (Postman və ya digər client-lər üçün)
* **Grafana (Monitorinq):** [http://localhost:3000](http://localhost:3000) (İstifadəçi: `admin`, Şifrə: `.env` daxilindəki dəyər və ya `admin`)
* **Kibana (Elastisearch axtarış paneli):** [http://localhost:5601](http://localhost:5601)

> [!NOTE]
> Frontend layihəsi Nginx vasitəsilə `/api/*` sorğularını daxili şəbəkədə birbaşa `api-gateway`-ə yönləndirir. Bu səbəbdən brauzer eyni origin-dən çıxış edir və əlavə CORS sazlamalarına ehtiyac qalmır.

---

## 🏗️ Sistem Arxitekturası

Sistem təhlükəsizlik və performans üçün yalnız **API Gateway** və **Frontend** servislərini kənara açır. Digər daxili servislər yalnız daxili Docker şəbəkəsində (`asanrezerv-net`) bir-biri ilə əlaqə saxlayır.

```mermaid
graph TD
    Client[Brauzer / Mobil Client] -->|Port 5173| Frontend[Frontend Nginx]
    Frontend -->|/api/*| Gateway[API Gateway :8080]
    Client -->|Birbaşa Sorğu| Gateway
    
    subgraph Daxili Şəbəkə (asanrezerv-net)
        Gateway --> Auth[auth-service]
        Gateway --> Rest[restaurant-service]
        Gateway --> Staff[staff-service]
        Gateway --> Sub[subscription-service]
        Gateway --> Res[reservation-service]
        Gateway --> AI[ai-analysis-service]
        Gateway --> Search[search-service]
        Gateway --> Media[media-service]
        
        %% Məlumat anbarları və köməkçi servislər
        Auth --> Postgres[(PostgreSQL)]
        Auth --> Redis[(Redis Cache/OTP)]
        
        Rest --> Postgres
        Rest --> Redis
        Rest --> Kafka{Apache Kafka}
        
        Res --> Postgres
        Res --> Redis
        
        Search --> ES[(Elasticsearch)]
        Search --> Kafka
        
        Sub --> Postgres
    end
```

**Təhlükəsizlik Modeli:** Kənardan gələn sorğularda saxta `X-User-Id` və ya `X-User-Role` başlıqlarının (headers) göndərilməsinin qarşısı Gateway tərəfindən alınır. Gateway daxil olan JWT tokenini yoxlayır, şifrəsini açır və yalnız etibarlı istifadəçi məlumatlarını daxili servislərə ötürür.

---

## 🛠️ Texnologiya Steki

### Backend (Mikroservislər)
* **Java 21 / 25 / 26** & **Spring Boot 3.x / 4.x**
* **Spring Cloud Gateway** — Mərkəzi marşrutlaşdırma və JWT validasiyası.
* **Spring Data JPA** & **PostgreSQL 17** — Əsas relyasiyalı məlumat bazası.
* **Redis 7** — Müvəqqəti tokenlər, OTP kodları, keşləmə və masaların dublikat rezervasiyasını önləmək üçün **Distributed Lock (Redisson)**.
* **Apache Kafka 3.8** — Servislərarası asinxron, hadisəyə əsaslanan (event-driven) kommunikasiya.
* **Elasticsearch 9 & Kibana** — Sürətli restoran axtarışı və filtrləmə.
* **Grafana & Prometheus** — Sistem göstəricilərinin vizuallaşdırılması və monitorinqi.

### Frontend
* **React** (Vite ilə sürətli yığım)
* **Tailwind CSS** — Modern UI və dizayn sistemi
* **React Context API** — Qlobal sessiya və rol idarəetməsi

---

## 📂 Servislər və Şəbəkə Portları

| Servis | Daxili Port | Xarici Port (Host) | Açıqlama |
| :--- | :---: | :---: | :--- |
| **frontend** | 80 | `5173` | Veb UI və Nginx Proxy |
| **api-gateway** | 8080 | `8080` | Mərkəzi Giriş Nöqtəsi (Gateway) |
| **auth-service** | 8080 | *Yalnız daxili* | İstifadəçi Qeydiyyatı, Giriş və OTP |
| **restaurant-service** | 8080 | *Yalnız daxili* | Restoran və Filial idarəetməsi |
| **staff-service** | 8080 | *Yalnız daxili* | İşçi heyəti idarəetməsi |
| **subscription-service**| 8080 | *Yalnız daxili* | SaaS abunəlik və limit yoxlanışı |
| **reservation-service** | 8080 | *Yalnız daxili* | Masaların rezervasiya sistemi (Redis Lock ilə) |
| **ai-analysis-service** | 8080 | *Yalnız daxili* | AI əsaslı analitika və hesabatlılıq |
| **search-service** | 8080 | *Yalnız daxili* | Restoran axtarışı (Elasticsearch ilə) |
| **media-service** | 8080 | *Yalnız daxili* | Şəkil və fayl yükləmə xidməti |
| **postgres** | 5432 | `5432` *(Yalnız debug)* | Mərkəzi PostgreSQL bazası |
| **redis** | 6379 | `6379` *(Yalnız debug)* | Keş və Paylaşılan Kilidlər |
| **kafka** | 9092 | `29092` *(Yalnız debug)*| Event Broker |
| **elasticsearch** | 9200 | `9200` *(Yalnız debug)*| Axtarış Mühərriki |
| **grafana** | 3000 | `3000` | Monitorinq paneli |

---

## 📝 Önəmli Qeydlər və İpucuları

* **JWT Təhlükəsizliyi:** `.env` faylındakı `JWT_SECRET` dəyişəni həm `auth-service`, həm də `api-gateway` üçün eyni olmalıdır (Docker compose bunu avtomatik idarə edir).
* **Double-Booking Profilaktikası:** Rezervasiya zamanı eyni masanın eyni vaxtda iki şəxs tərəfindən rezerv edilməməsi üçün `reservation-service` Redis üzərində qurulmuş paylanmış kilidlərdən (Redisson Distributed Lock) istifadə edir.
* **Feature Gating (Limitlər):** Restoranın yeni filial və ya masa əlavə edib-edə bilməyəcəyi `subscription-service` tərəfindən müəyyən edilmiş limitlər çərçivəsində yoxlanılır.
