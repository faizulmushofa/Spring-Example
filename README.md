# 🚀 Emuy: Personal Cloud Drive & Sandboxing Project

Selamat datang di project **Emuy**, sebuah aplikasi Manajemen File (Personal Cloud Drive) yang dibangun menggunakan **Spring Boot 3** (Backend) dan **Vanilla HTML/JS/CSS** (Frontend). Project ini digunakan sebagai media pembelajaran (Sandboxing) untuk menguasai berbagai konsep Backend Engineering.

---

## 🛠️ Sandboxing Roadmap

Project ini dibagi menjadi beberapa fase pengembangan untuk memastikan fondasi yang kuat:

### 🟢 Phase 1: Security & Identity (HARUS FIX)
Fokus pada pengamanan aplikasi dan pemisahan data antar pengguna.
- [x] **Auth + Session**: Implementasi Sesi berbasis `JSESSIONID` dan proteksi CSRF.
- [x] **Role Security**: Pembedaan akses antara `ADMIN` dan `MEMBER`.
- [ ] **User Isolation**: Menjamin user hanya bisa melihat dan mengelola file miliknya sendiri. (📌 *Sedang dikerjakan*)

### 🔵 Phase 2: Core Features
Fungsionalitas utama aplikasi.
- [x] **File Upload**: Mendukung upload file melalui multipart request.
- [x] **File Download**: Sistem download file yang aman.
- [x] **Media Streaming**: Support HTTP Range requests untuk streaming video/audio.
- [ ] **File Preview**: Penampilan preview file di sisi frontend.

### 🟡 Phase 3: Data Layer Optimization
Membersihkan dan merapikan interaksi dengan database.
- [ ] **JPA vs SQL Consistency**: Sinkronisasi antara Entity JPA dan query SQL native.
- [ ] **Repository Refactoring**: Menggunakan best practice dalam pengambilan data.

### 🟠 Phase 4: API & Error Handling
Standardisasi komunikasi antara Frontend dan Backend.
- [ ] **API Standardization**: Format response yang konsisten (`success`, `message`, `data`).
- [ ] **Global Error Handling**: Penanganan exception yang rapi menggunakan `@RestControllerAdvice`.

### 🟣 Phase 5: Polish & Refactor (Optional)
Sentuhan akhir untuk performa dan estetika.
- [ ] **Optimization**: Caching dan optimasi query.
- [ ] **Code Refactor**: Pembersihan code smell.
- [ ] **UI/UX Polish**: Mempercantik tampilan frontend.

---

## 🏗️ Tech Stack

### Backend
- **Framework**: Spring Boot 3.4.1
- **Security**: Spring Security (Session-based)
- **Database**: MySQL / MariaDB
- **ORM**: Spring Data JPA
- **Build Tool**: Maven

### Frontend
- **Structure**: Semantic HTML5
- **Logic**: Vanilla JavaScript (Modern ES6+)
- **Styling**: CSS3 (Modern techniques)

---

## 📖 Dokumentasi Penting

- [Alur Spring Security (Login)](./emuy-backend/SPRING_SECURITY_FLOW.md) - Penjelasan mendalam tentang bagaimana session dan login bekerja di project ini.

---

## 🚀 Cara Menjalankan Project

### Backend
1. Masuk ke folder `emuy-backend`.
2. Pastikan database MySQL sudah menyala.
3. Jalankan perintah:
   ```bash
   ./mvnw spring-boot:run
   ```

### Frontend
1. Buka file `index.html` (atau file login utama) di folder `emuy-frontend` menggunakan Live Server atau langsung di browser.

---

## 📂 Struktur Folder
- `emuy-backend/`: Source code Spring Boot.
- `emuy-frontend/`: Source code web frontend.
- `mydrive/`: Folder lokal tempat penyimpanan file yang di-upload (diatur di backend).

---

> [!TIP]
> Project ini dibuat untuk tujuan belajar. Jangan ragu untuk bereksperimen dengan mengubah alur security atau menambahkan fitur baru di setiap fasenya!
