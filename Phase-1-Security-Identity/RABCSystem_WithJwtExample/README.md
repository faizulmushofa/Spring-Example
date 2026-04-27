# 🔐 RBAC with JWT — Spring Security Lab

> **Phase 1: Security & Identity** — Sandbox project untuk menguasai arsitektur **Role-Based Access Control** menggunakan Spring Boot 4, Spring Security, dan JWT sebagai mekanisme autentikasi stateless.
>
> **Tech Stack**: Java 21 · Spring Boot 4.0.6 · Spring Security · H2 Database (in-memory) · jjwt 0.13 · Lombok

---

## 🖼️ Screenshots


| Login | Admin Dashboard |
|---|---|
| ![Login Page](https://res.cloudinary.com/dnn0ybvpu/image/upload/v1777271849/Screenshot_2026-04-27_at_13.32.14_j5gccr.png) | ![Admin Dashboard](https://res.cloudinary.com/dnn0ybvpu/image/upload/v1777271849/Screenshot_2026-04-27_at_13.31.32_ohkw52.png) |

| Lecturer Dashboard | Student Dashboard |
|---|---|
| ![Lecturer Dashboard](https://res.cloudinary.com/dnn0ybvpu/image/upload/v1777272014/Screenshot_2026-04-27_at_13.39.05_w89jfz.png) | ![Student Dashboard](https://res.cloudinary.com/dnn0ybvpu/image/upload/v1777272013/Screenshot_2026-04-27_at_13.39.32_qvmtgr.png) |

---

## 📌 Apa yang Dipelajari?

Project ini **bukan** tentang JWT secara mendalam (tidak ada refresh token, token blacklist, dsb). JWT di sini hanya digunakan sebagai **kartu identitas stateless**. Fokus sebenarnya ada di:

- **Desain Model RBAC** — Relasi `User → Role → Permission` menggunakan join table
- **Permission-Level Authorization** — Otorisasi granular berbasis Permission, bukan Role
- **Spring Security Filter Chain** — Bagaimana request diproses dari filter hingga controller
- **Method Security** — `@PreAuthorize` dengan `hasAuthority()` di setiap endpoint

---

## 🏗️ Arsitektur RBAC

```
User ──(1:N)──> UserRole ──(N:1)──> Role ──(1:N)──> RolePermission ──(N:1)──> Permission
```

**Alur setiap request:**

```
Request masuk
    │
    ▼
JwtFilter (extract email dari token)
    │
    ▼
CustomUserDetailService (load Permission dari DB melalui chain Role → RolePermission → Permission)
    │
    ▼
SecurityContext (set authorities)
    │
    ▼
@PreAuthorize("hasAuthority('MAKE_COURSE')") → ✅ Allowed / ❌ 403 Forbidden
```

> **Kunci**: JWT hanya membawa identitas (email). **Setiap request**, sistem membaca ulang Permission terbaru dari database — sehingga perubahan Permission oleh Admin langsung berlaku tanpa perlu re-login.

---

## 👥 Role & Permission Default

| Role | Permission | Akses |
|---|---|---|
| `admin` | `MANAGE_ALL` | Full access — kelola user, course, permission, role |
| `dosen` | `MAKE_COURSE` | CRUD course miliknya, approve/reject enrolment |
| `student` | `ENROL_COURSE` | Lihat course aktif, enroll, lihat status pendaftaran |

Permission di-seed otomatis melalui `DataInitializer` dan langsung terikat ke role masing-masing.

---

## 📂 Struktur Project

```
src/main/java/org/example/jwtexample/
├── Config/
│   ├── SecurityConfig.java         # Filter chain, CORS, stateless session
│   ├── JwtFilter.java              # Extract token → load authorities → set context
│   └── RequestLoggingFilter.java   # Logging setiap request (method, URI, status, durasi)
│
├── Model/
│   ├── User.java                   # Entity utama dengan relasi ke UserRole
│   ├── Role.java                   # Role entity (admin, dosen, student)
│   ├── UserRole.java               # Join table User ↔ Role
│   ├── Permission.java             # Permission entity (MANAGE_ALL, MAKE_COURSE, dll)
│   ├── RolePermission.java         # Join table Role ↔ Permission
│   ├── CustomUserDetails.java      # Implementasi UserDetails Spring Security
│   ├── Course.java                 # Domain — kursus milik dosen
│   ├── Enrolment.java              # Domain — pendaftaran student ke course
│   └── Enum/Status.java            # PENDING | APPROVED | REJECTED
│
├── Repository/                     # JPA Repositories
├── Dto/                            # Data Transfer Objects (menghindari circular ref)
│
├── Service/
│   ├── CustomUserDetailService.java  # ⭐ Jantung RBAC — flatMap permission dari role chain
│   ├── AuthServiceImp.java           # Login & Register
│   ├── JwtService.java               # Generate & parse JWT (utilitas)
│   ├── UserService.java              # User management + admin DTO
│   ├── RoleService.java              # Assign role ke user
│   ├── PermissionService.java        # Assign/unassign permission ke role
│   ├── CourseService.java            # CRUD course
│   └── EnrolmentServiceImp.java      # Enroll, approve, reject
│
├── Controller/                     # Thin controllers — routing + @PreAuthorize only
└── Dummy/
    └── DataInitializer.java        # Seed data (roles, permissions, users, courses)
```

---

## 🔑 Prinsip Arsitektur

| Prinsip | Implementasi |
|---|---|
| **Thin Controller, Fat Service** | Controller hanya routing & security annotation. Logika bisnis di Service. |
| **Permission over Role** | `@PreAuthorize` mengecek Permission (`MAKE_COURSE`), bukan Role (`dosen`). |
| **DTO Pattern** | `EnrolmentDto`, `AdminUserDto` untuk response aman tanpa data sensitif. |
| **JWT as Transport** | JWT hanya membawa identitas. Otorisasi selalu dibaca dari DB. |
| **Idempotent Seeding** | `DataInitializer` aman dijalankan berulang kali tanpa duplikasi data. |

---

## 🚀 Cara Menjalankan

```bash
# Cukup jalankan — H2 in-memory, tidak perlu setup database
./mvnw spring-boot:run
```

> [!NOTE]
> Database H2 berjalan di memori. Setiap kali aplikasi di-restart, data kembali ke keadaan awal dari `DataInitializer`. Ini disengaja agar fokus ke eksperimen RBAC tanpa repot manage database.

DataInitializer akan otomatis membuat:
- **1 Admin** — `admin@lms.com` / `admin123`
- **3 Dosen** — `budi.dosen@lms.com`, `siti.dosen@lms.com`, `andi.dosen@lms.com` / `dosen123`
- **5 Student** — `raka@student.com`, `dewi@student.com`, dll / `student123`
- **6 Course** dengan **11 Enrolment** (campuran PENDING/APPROVED/REJECTED)

---

## 📡 API Endpoints

### Auth (Public)
| Method | Endpoint | Keterangan |
|---|---|---|
| POST | `/api/auth/login` | Login, return JWT |
| POST | `/api/auth/register` | Register sebagai student |

### Users (MANAGE_ALL)
| Method | Endpoint | Keterangan |
|---|---|---|
| GET | `/api/users` | Semua user + roles + permissions |
| GET | `/api/users/lecturers` | Filter dosen aktif |
| GET | `/api/users/students` | Filter student aktif |
| PUT | `/api/users/{id}/activate` | Aktifkan user |
| PUT | `/api/users/{id}/deactivate` | Nonaktifkan user |

### Roles (MANAGE_ALL)
| Method | Endpoint | Keterangan |
|---|---|---|
| GET | `/api/roles` | Semua role |
| POST | `/api/roles` | Buat role baru |
| POST | `/api/roles/set-dosen/{userId}` | Promote user jadi dosen |

### Permissions (MANAGE_ALL)
| Method | Endpoint | Keterangan |
|---|---|---|
| GET | `/api/permissions` | Semua permission |
| POST | `/api/permissions` | Buat permission baru |
| POST | `/api/permissions/{id}/assign-role/{roleId}` | Assign ke role |
| DELETE | `/api/permissions/{id}/unassign-role/{roleId}` | Cabut dari role |

### Courses (MAKE_COURSE / MANAGE_ALL)
| Method | Endpoint | Keterangan |
|---|---|---|
| POST | `/api/courses` | Buat course (owner = caller) |
| GET | `/api/courses` | Semua course (admin) |
| GET | `/api/courses/active` | Course aktif (semua role) |
| GET | `/api/courses/my-courses` | Course milik dosen caller |
| PUT | `/api/courses/{id}` | Update course |
| DELETE | `/api/courses/{id}` | Deactivate course |

### Enrolments (ENROL_COURSE / MAKE_COURSE / MANAGE_ALL)
| Method | Endpoint | Keterangan |
|---|---|---|
| POST | `/api/enrolments` | Enroll (body: userId, courseId) |
| DELETE | `/api/enrolments` | Unenroll |
| GET | `/api/enrolments/user/{userId}/courses` | Course yang diikuti user |
| GET | `/api/enrolments/course/{courseId}/users` | Peserta course |
| PUT | `/api/enrolments/{id}/approve` | Dosen approve |
| PUT | `/api/enrolments/{id}/reject` | Dosen reject |

---

## 🧠 Catatan Pembelajaran

1. **Kenapa Permission, bukan Role?** — Role bisa berubah kapan saja. Dengan mengecek Permission, admin bisa menambahkan kemampuan baru ke role tanpa mengubah kode.

2. **Kenapa Permission dibaca dari DB setiap request?** — Agar perubahan yang dilakukan admin (assign/unassign permission) langsung berlaku tanpa user perlu re-login.

3. **Kenapa JWT tanpa refresh token?** — Scope project ini adalah RBAC, bukan JWT management. JWT di sini hanya alat bantu untuk membawa identitas secara stateless.

4. **Logout?** — Cukup hapus token dari `localStorage` di frontend. Karena tidak ada refresh token atau server-side session, tidak perlu endpoint logout di backend.
