# 🚀 Emuy: Backend Engineering Roadmap

Selamat datang di project **Emuy**, sebuah media pembelajaran (Sandboxing) untuk menguasai berbagai konsep **Backend Engineering** menggunakan **Spring Boot 3**. Project ini disusun berdasarkan fase pengembangan untuk memastikan pemahaman yang mendalam di setiap tahapnya.

---

## 🛠️ Sandboxing Roadmap

Setiap folder `Phase-X` berisi project atau latihan yang relevan dengan tahap tersebut.

### 🟢 Phase 1: Security & Identity
Fokus pada pengamanan aplikasi dan autentikasi.
- **Goal**: Implementasi Session-based Auth (`JSESSIONID`), Role Security (`ADMIN`/`MEMBER`), dan proteksi CSRF.
- **Project Folder**: `Phase-1-Security-Identity/spring_security_Tutorial/`

### 🔵 Phase 2: Core Features (Current Stage)
Implementasi fungsionalitas utama aplikasi manajemen file.
- **Goal**: Multipart File Upload, Secure Download, dan HTTP Range Requests untuk Media Streaming.
- **Project Folder**: `Phase-2-Core-Features/ConnectingToSql/`

### 🟡 Phase 3: Data Layer Optimization
Membersihkan dan merapikan interaksi dengan database.
- **Goal**: Sinkronisasi JPA Entity dengan Native SQL, Repository Refactoring, dan Audit Logging.

### 🟠 Phase 4: API & Error Handling
Standardisasi komunikasi dan penanganan error.
- **Goal**: `@RestControllerAdvice`, Global Exception Handling, dan Consistent Response Format.

### 🟣 Phase 5: Optimization & Refactor
Sentuhan akhir untuk performa dan kualitas kode.
- **Goal**: Caching (Redis/Spring Cache), Query Optimization, dan Code Refactoring.

---

## 📂 Struktur Folder Latihan

- **`Phase-1-Security-Identity/`**
  - `spring_security_Tutorial/`: Materi latihan dasar Spring Security.
- **`Phase-2-Core-Features/`**
  - `ConnectingToSql/`: Project utama Spring Boot yang sedang dikembangkan.
- **`Phase-3-Data-Layer/`**: Tempat latihan optimasi database.
- **`Phase-4-API-ErrorHandling/`**: Tempat latihan standarisasi API.
- **`Phase-5-Optimization-Refactor/`**: Tempat latihan optimasi performa.

---

## 🚀 Cara Menjalankan Latihan

1. Masuk ke folder project di dalam fase (misal: `Phase-2-Core-Features/ConnectingToSql`).
2. Pastikan database MySQL sudah menyala.
3. Jalankan aplikasi:
   ```bash
   ./mvnw spring-boot:run
   ```

---

> [!TIP]
> Fokus pada satu fase hingga benar-benar paham sebelum lanjut ke fase berikutnya. Selamat belajar!
