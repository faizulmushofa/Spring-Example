# FileVault — ORM with JPA & Core Backend Features 🚀

Repositori ini adalah catatan belajar mendalam (Deep-Dive) mengenai implementasi fitur krusial dalam Backend Engineering: **Autentikasi Custom (Session-based)**, **File Storage (Upload/Download)**, dan **Soft Delete**.

Dokumentasi ini ditulis sebagai referensi agar Anda bisa memahami *alur kerja (flow)* dari setiap fitur yang ada di bawah kap (under the hood).

---

## 🔐 1. Bagaimana Sistem Session (Autentikasi) Bekerja?

Karena kita tidak menggunakan `Spring Security` secara penuh, kita membangun sistem *Custom Session* kita sendiri.

### Alur Login (Membuat Session)
1. **Validasi User**: Frontend mengirim `email` dan `password`. Backend mencari user di Database. Jika password cocok, user dinyatakan *valid*.
2. **Membuat Tiket (Session ID)**: Di `AuthServiceImp`, kita membuat sebuah "tiket" menggunakan `UUID.randomUUID()`. Ini adalah string acak yang sulit ditebak.
3. **Penyimpanan Lokal (Memory)**: Tiket ini disimpan di dalam *RAM Backend* (menggunakan `HashMap` di class `SessionStore`), dengan format: `{ "UUID-Tiket-123": 1 }` (di mana `1` adalah ID dari User tersebut).
4. **Dikirim via Cookie**: Backend tidak hanya mengembalikan Response JSON biasa, tapi juga menyelipkan `Cookie` bernama `SESSION_ID` ke dalam Header HTTP. Cookie ini diatur sebagai `HttpOnly=true`, artinya hacker tidak bisa mencuri cookie ini menggunakan script JavaScript XSS.

### Alur Request Terproteksi (Validasi Session)
Setiap kali Frontend melakukan *Request* ke endpoint yang butuh login (seperti `/file/user/{id}`), browser **secara otomatis** akan menyertakan Cookie `SESSION_ID` tadi.
- Sebelum request sampai ke Controller, **`AuthFilter`** (class yang meng-implements `Filter`) akan mencegat request tersebut.
- Filter akan mengambil nilai Cookie `SESSION_ID`.
- Filter mengecek ke `SessionStore`: *"Apakah tiket ini ada di dalam Map memori kita?"*
  - Jika **Tidak**: Filter menolak request dengan status `401 Unauthorized`. Frontend lalu menendang user ke halaman login.
  - Jika **Ya**: Filter mempersilakan request lanjut ke Controller.

---

## 📁 2. Bagaimana Sistem Upload Bekerja?

Sistem file kita menggunakan pendekatan hibrida: **Data Fisik** disimpan di Harddisk Server, dan **Metadata** disimpan di Database SQL.

### Alur Upload File
1. **Frontend**: User memilih file, lalu `dashboard.js` membungkusnya dalam bentuk `FormData` (sebuah format web standar untuk mengirim file biner) lalu memanggil API `POST /file/upload`.
2. **Controller (`@ModelAttribute UploadRequest`)**: Spring Boot menerima file ini dalam wujud objek Java bernama `MultipartFile`.
3. **Pembuatan Nama Unik (Hash Name)**: Jika dua user mengupload file dengan nama `tugas.pdf`, mereka akan tumpang tindih. Untuk mencegahnya, di `FileServiceImp`, kita membuat nama alias yang unik (contoh: `4988aaa8-bd1b-4772.pdf`) menggunakan `UUID`.
4. **Penyimpanan Fisik**: Class `StorageServicesImp` mengambil data biner dari `MultipartFile` lalu menyimpannya (meng-copy) secara permanen ke sebuah direktori lokal di laptop/server Anda (biasanya di folder `Data/DummyData/{userId}/...`).
5. **Penyimpanan Database**: Setelah sukses tersimpan di harddisk, *path* (lokasi folder fisik), `originalName` (nama asli), `hashName` (nama unik UUID), dan ukuran file disimpan ke dalam tabel `stored_file` menggunakan JPA.

---

## 📥 3. Bagaimana Sistem Download Bekerja?

Download bukan sekadar mengirim teks, melainkan memompa aliran data (Stream) kembali ke browser.

### Alur Download File
1. **Frontend Request**: Browser memanggil `GET /file/download/{fileId}?userId={userId}`.
2. **Pencarian Metadata**: Backend mencari `fileId` di Database. Dari database, Backend tahu di mana persisnya file fisik itu berada (menggunakan `path` atau `hashName`).
3. **Validasi Kepemilikan**: Backend memastikan bahwa `userId` yang merequest benar-benar pemilik dari file tersebut (mencegah user lain mendownload file rahasia kita).
4. **Membaca Fisik File (`Resource`)**: `StorageServicesImp` membuka file fisik dari Harddisk dan mengubahnya menjadi bentuk objek `UrlResource` atau `InputStream`.
5. **Memaksa Browser Mendownload**: 
   - Di `FileController`, kita men-setting `HttpHeaders.CONTENT_DISPOSITION` menjadi `attachment; filename="nama_asli.pdf"`.
   - Kata `attachment` memberi tahu browser: *"Tolong jangan buka file ini di layar (seperti menampilkan foto/PDF di tab browser), tapi pancing jendela download (Save As) ke komputer user."*
   - Kita mereturn `Resource` tadi ke dalam *Body* Response, dan Spring Boot secara otomatis mengalirkan datanya ke user.

---

## ♻️ 4. Bagaimana Soft Delete (Tempat Sampah) Bekerja?

**Kenapa Soft Delete?** Jika kita memakai operasi `DELETE FROM table`, data akan hilang selamanya. Jika user salah klik, tidak ada fitur *Undo*.

### Alur Hapus dan Restore
1. **Database Flagging**: Di entitas `StoredFile`, terdapat kolom `is_removed` bertipe boolean.
2. **Aksi Hapus**: Saat dipanggil API `/file/delete/{id}`, Backend hanya mengubah nilai `is_removed` menjadi `true`. **File fisik di harddisk TIDAK DIHAPUS.**
3. **Aksi Restore**: Saat API `/file/restore/{id}` dipanggil, Backend mengubah `is_removed` kembali menjadi `false`.
4. **Logika Frontend**: `dashboard.js` memanggil fungsi `loadUserFiles()`. Ketika menerima respon JSON (yang berisi flag `isRemoved`), Javascript akan memfilter mana file yang harus masuk tab "Aktif" (`isRemoved == false`) dan mana yang masuk tab "Tempat Sampah" (`isRemoved == true`).

---

## 🧹 5. Pola DTO (Data Transfer Object)

Kita memisahkan objek database (`Model`) dengan objek yang dikirim ke browser (`DTO`).

- **Mencegah Over-posting**: User tidak bisa memanipulasi field yang tidak kita sediakan (seperti memaksa mengirim `isRemoved: true` saat registrasi).
- **Mencegah Kebocoran Data**: Model `User` memiliki password (meskipun dienkripsi). Jika Controller langsung mereturn `User`, password tersebut bisa terekspos di tab Network browser. Dengan `UserResponse` atau `LoginResponse`, kita memilih hanya mengembalikan ID, Username, dan Email saja.

---
> **Catatan Penutup:** Repositori ini adalah blueprint fundamental. Pola arsitektur yang digunakan di sini (DTO, Layering Service-Controller, Storage Interface, Custom Auth) akan terus Anda pakai dan menjadi dasar yang kuat saat berpindah ke _framework_ lain (seperti NestJS) maupun ketika Anda menggunakan modul canggih seperti _Spring Security_ dan _AWS S3_ nantinya.
