# 🛡️ FileSafe — Spring Security Stateful (Session-Based Auth)

Project ini adalah implementasi nyata Spring Security menggunakan pendekatan **Stateful / Session-Based Authentication** — artinya server yang "mengingat" siapa yang sedang login, bukan client.

---

## 📁 Struktur Package Penting

```
com.example.faizul
├── Config/
│   └── DataSeeder.java           → Menyemai data dummy ke H2 saat startup
├── Controller/
│   ├── AuthController.java       → Endpoint /auth/login & /auth/register
│   └── FileController.java       → Endpoint /file (upload, download, list)
├── Model/
│   ├── User.java                 → Entitas user di database
│   ├── UserDetailsImp.java       → Implementasi UserDetails untuk Spring Security
│   ├── Role.java                 → Entitas role (ROLE_USER, ROLE_ADMIN)
│   └── StoredFile.java           → Entitas file yang diupload
├── Security/
│   ├── SecurityConfig.java       → Konfigurasi utama Spring Security
│   └── RequestLoggingFilter.java → Filter debug untuk log request
├── Service/
│   ├── AuthService.java          → Logika login & registrasi
│   ├── UserDetailsServiceImpl.java → Mengambil user dari DB untuk Spring Security
│   └── FileService.java          → Logika pengelolaan file
└── Repository/
    ├── UserRepository.java
    ├── RoleRepository.java
    └── FileRepository.java
```

---

## 🔐 Apa itu Stateful / Session-Based?

```
CLIENT                              SERVER
  │                                   │
  │── POST /auth/login ─────────────► │ 1. Terima email & password
  │   {email, password}               │ 2. Verifikasi ke database
  │                                   │ 3. Buat Session (disimpan di server)
  │◄─ Set-Cookie: JSESSIONID=ABC ──── │ 4. Kirim Session ID via Cookie
  │                                   │
  │── GET /file/all ────────────────► │ 5. Browser otomatis kirim cookie
  │   Cookie: JSESSIONID=ABC          │ 6. Server cari session ABC
  │                                   │ 7. Temukan → user sudah login!
  │◄─ 200 OK [daftar file] ─────────  │
```

**Poin utama:** Server menyimpan **Session di memori**. Client hanya menyimpan **ID Session** via Cookie `JSESSIONID`. Setiap request, browser mengirimkan cookie tersebut sehingga server tahu siapa yang mengirim request.

---

## 👤 UserDetails — Jembatan Antara DB dan Spring Security

Spring Security tidak mengenal entitas `User` Anda secara langsung. Ia membutuhkan objek yang mengimplementasikan interface `UserDetails`. Di sinilah `UserDetailsImp` berperan.

### 1. Interface `UserDetails` (dari Spring Security)

```java
// Kontrak yang harus dipenuhi agar Spring Security bisa bekerja
public interface UserDetails {
    Collection<? extends GrantedAuthority> getAuthorities(); // Daftar role/permission
    String getPassword();                                    // Password terenkripsi
    String getUsername();                                    // Username (atau email)
    boolean isAccountNonExpired();
    boolean isAccountNonLocked();
    boolean isCredentialsNonExpired();
    boolean isEnabled();                                     // Apakah akun aktif?
}
```

### 2. Implementasi: `UserDetailsImp.java`

Kelas ini "membungkus" data dari entitas `User` ke dalam format yang dimengerti Spring Security:

```java
@Getter
@AllArgsConstructor
public class UserDetailsImp implements UserDetails {

    private Long userId;       // ← Field tambahan (bukan dari UserDetails),
    private String username;   //   berguna untuk identifikasi di Controller
    private String password;
    private boolean isActive;
    private Set<Role> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Mengubah Set<Role> menjadi format GrantedAuthority yang dimengerti Spring
        return roles.stream()
            .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
            .toList();
    }

    @Override
    public boolean isEnabled() {
        return isActive; // Jika user dinonaktifkan di DB, Spring Security akan tolak login
    }
}
```

> **Kenapa ada `userId`?** Karena kita butuh ID user untuk mengambil file miliknya. Field ini tidak ada di interface standar `UserDetails`, kita tambahkan sendiri dan ambil via `@AuthenticationPrincipal`.

### 3. `UserDetailsServiceImpl.java` — Menghubungkan ke Database

Spring Security memanggil service ini secara otomatis saat proses autentikasi untuk mencari user berdasarkan username (email dalam kasus ini):

```java
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Cari user di database berdasarkan email
        User user = userRepository.findByUserEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        // Bungkus entitas User menjadi UserDetailsImp
        return new UserDetailsImp(
            user.getUserId(),
            user.getUserName(),
            user.getUserPassword(), // Password sudah terenkripsi di DB
            user.isActive(),
            user.getRoles()
        );
    }
}
```

**Alur Pemanggilan:** `AuthenticationManager` → `UserDetailsServiceImpl.loadUserByUsername()` → cari di DB → kembalikan `UserDetailsImp`.

---

## 🏗️ Konfigurasi Security: `SecurityConfig.java`

```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final RequestLoggingFilter requestLoggingFilter;

    // 1. PasswordEncoder: Menggunakan DelegatingPasswordEncoder
    //    Format hash: {bcrypt}$2a$10$...
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // 2. AuthenticationManager: Menggunakan konfigurasi default Spring
    //    Secara otomatis mendeteksi UserDetailsServiceImpl karena @Service
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // 3. CSRF Repository: Menggunakan Cookie agar bisa dibaca JS
    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        return CookieCsrfTokenRepository.withHttpOnlyFalse();
        //                               ^^^^^^^^^^^^^^^^^
        //                               HttpOnly=false → JS bisa baca cookie
    }

    // 4. Filter Chain: Aturan keamanan per URL
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Filter logging SETELAH SecurityContextHolderFilter
            // agar user sudah teridentifikasi saat di-log
            .addFilterAfter(requestLoggingFilter, SecurityContextHolderFilter.class)

            // CSRF aktif untuk semua, kecuali endpoint auth
            .csrf(csrf -> csrf
                .csrfTokenRepository(csrfTokenRepository())
                .ignoringRequestMatchers("/auth/login", "/auth/register"))

            // Aturan akses per URL
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index.html", "/style.css", "/app.js").permitAll()
                .requestMatchers("/auth/login", "/auth/register").permitAll()
                .anyRequest().authenticated() // Semua endpoint lain butuh login
            )

            // Matikan form login bawaan dan HTTP Basic
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable());

        return http.build();
    }
}
```

---

## 🔑 Alur Login: `AuthService.java`

Ini adalah bagian **terpenting** untuk memahami Stateful Auth di Spring Security 6:

```java
public LoginResponse login(LoginRequest request,
                           HttpServletRequest httpRequest,
                           HttpServletResponse httpResponse) {

    // LANGKAH 1: Verifikasi kredensial
    // AuthenticationManager memanggil UserDetailsServiceImpl di balik layar
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.email().toLowerCase(), // username/email
            request.password()             // password plain text
        )
    );

    // LANGKAH 2: Simpan ke SecurityContextHolder (memori thread saat ini)
    var context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(authentication);
    SecurityContextHolder.setContext(context);

    // LANGKAH 3: ⚠️ WAJIB di Spring Security 6 — Simpan ke Session!
    // Ini yang menghasilkan cookie JSESSIONID di response
    securityContextRepository.saveContext(context, httpRequest, httpResponse);

    // LANGKAH 4: Ambil data user dari Principal
    UserDetailsImp user = (UserDetailsImp) authentication.getPrincipal();

    return new LoginResponse(user.getUserId(), user.getUsername(), ...);
}
```

> **⚠️ Perbedaan Spring Security 5 vs 6:**
> Di Spring Security 5, session tersimpan **otomatis**. Di **Spring Security 6+**, Anda **harus** memanggil `securityContextRepository.saveContext()` secara eksplisit ketika login dilakukan manual (bukan via form login bawaan). Tanpa ini, user dianggap Anonymous di request berikutnya.

---

## 🍪 Cookie yang Digunakan

Aplikasi ini menggunakan **dua cookie** dengan tujuan berbeda:

| Cookie | Dibuat oleh | Isi | Kegunaan |
|---|---|---|---|
| `JSESSIONID` | Spring Session | ID sesi unik (acak) | Mengenali user yang login di setiap request |
| `XSRF-TOKEN` | Spring Security CSRF | Token acak | Melindungi dari serangan Cross-Site Request Forgery |

### Cara kerja `XSRF-TOKEN` di Frontend (`app.js`):

```javascript
// 1. Saat halaman pertama dibuka, Spring mengirimkan cookie XSRF-TOKEN
// 2. JavaScript membaca nilai cookie tersebut:
function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
}

// 3. Setiap request POST harus menyertakan token di header:
fetch('/file/upload', {
    method: 'POST',
    headers: {
        'X-XSRF-TOKEN': getCookie('XSRF-TOKEN') // ← Wajib ada!
    },
    body: formData
});
```

> **Mengapa pakai Header, bukan Cookie?**
> Penyerang (situs jahat) tidak bisa mengirim header kustom ke domain lain, meskipun browser mengirim cookie secara otomatis. Inilah yang membuat mekanisme ini aman.

---

## 🎭 Menggunakan `@AuthenticationPrincipal` di Controller

Setelah user login dan session tersimpan, kita bisa mengambil data user di Controller tanpa query database ulang:

```java
@GetMapping("/all")
ResponseEntity<List<FileDto>> getAllFileByUserId(
        @AuthenticationPrincipal UserDetailsImp userDetails) {
    //   ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    //   Spring Security inject objek UserDetailsImp secara otomatis
    //   dari SecurityContextHolder

    List<FileDto> files = fileService.getAllFileByUserId(userDetails.getUserId());
    return ResponseEntity.ok(files);
}
```

**Alur di balik layar:**
1. Request masuk dengan cookie `JSESSIONID=ABC`.
2. `SecurityContextHolderFilter` membaca session server → muat `Authentication` ke `SecurityContextHolder`.
3. `Authentication.getPrincipal()` mengembalikan objek `UserDetailsImp` yang dibuat saat login.
4. Spring meng-inject objek tersebut ke parameter `@AuthenticationPrincipal`.

---

## 🔄 Rangkuman Alur Lengkap

```
REGISTRASI
──────────
POST /auth/register {userName, userEmail, userPassword, roles:[]}
  └─► AuthService.register()
        ├─ Cari Role dari DB (ROLE_USER / ROLE_ADMIN)
        ├─ passwordEncoder.encode(password) → {bcrypt}$2a$...
        └─ userRepository.save(newUser)


LOGIN
─────
POST /auth/login {email, password}
  └─► AuthService.login()
        └─► AuthenticationManager.authenticate()
              └─► UserDetailsServiceImpl.loadUserByUsername(email)
                    ├─ userRepository.findByUserEmail(email)
                    └─ return new UserDetailsImp(...)
              ├─ Cocokkan password → PasswordEncoder.matches()
              └─ return Authentication (jika cocok)
        ├─ SecurityContextHolder.setContext(authentication)
        ├─ securityContextRepository.saveContext() ← simpan ke Session
        └─ Response: {userId, userName, roles}
           + Set-Cookie: JSESSIONID=ABC123
              + Set-Cookie: XSRF-TOKEN=XYZ789


REQUEST BERIKUTNYA (misal: GET /file/all)
──────────────────────────────────────────
Cookie: JSESSIONID=ABC123
  └─► SecurityContextHolderFilter
        ├─ Baca JSESSIONID dari cookie
        ├─ Cari session ABC123 di server
        └─ Muat Authentication ke SecurityContextHolder
  └─► AuthorizationFilter
        └─ Cek authenticated? → Ya, lanjut
  └─► FileController.getAllFileByUserId(@AuthenticationPrincipal userDetails)
        ├─ userDetails.getUserId() → sudah tersedia, tanpa query DB
        └─ fileService.getAllFileByUserId(userId)
```

---

## 🧪 Data Dummy untuk Testing

Saat startup, `DataSeeder.java` otomatis membuat dua user:

| Email | Password | Role |
|---|---|---|
| `admin@gmail.com` | `admin123` | `ROLE_ADMIN`, `ROLE_USER` |
| `user@gmail.com` | `user123` | `ROLE_USER` |

---

## ⚠️ Hal yang Wajib Diperhatikan

1. **`saveContext()` wajib dipanggil** — Tanpa ini, sesi tidak tersimpan dan user langsung logout setelah request selesai.
2. **`HttpOnly=false` pada CSRF Cookie** — Wajib agar JavaScript bisa membaca `XSRF-TOKEN`.
3. **`DelegatingPasswordEncoder`** — Password disimpan dengan format `{bcrypt}$2a$...`. Pastikan `PasswordEncoder` yang sama digunakan saat login dan registrasi.
4. **H2 In-Memory Database** — Data hilang setiap server di-restart. `DataSeeder` memastikan data dummy selalu ada kembali.
