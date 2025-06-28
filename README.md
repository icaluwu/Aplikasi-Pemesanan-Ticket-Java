# 🌟 Aplikasi Pemesanan Tiket (JavaFX)

**Repository:** `icaluwu/Aplikasi-Pemesanan-Ticket-Java`  
Aplikasi desktop berbasis **JavaFX** untuk memesan tiket film, menampilkan jadwal, serta menyimpan riwayat booking.

---

## 📌 Fitur Utama

1. Tampilan **login** untuk user atau admin.
2. **Dashboard Admin**:
   - Manage film (CRUD)
   - Manage jadwal
3. **Dashboard User**:
   - View daftar jadwal tersedia
   - Pesan tiket
   - Lihat riwayat pemesanan
4. Listener otomatis melakukan perbaruan data saat terjadi pemesanan.

---

## 🧭 Alur Aplikasi

1. **Login**:
   - Admin login: dialihkan ke `AdminDash` (tab management).
   - User login: dialihkan ke `UserDashboard` (tab Booking dan My Bookings).
2. **Admin**:
   - Tambah/Update/Delete film & jadwal tayang. 
   - Validasi untuk mencegah hapus jadwal yang memiliki booking (konflik foreign key).
3. **User**:
   - Pilih jadwal, tentukan jumlah tiket.
   - Tekan **Book Now** → update seat otomatis, dan My Bookings diperbarui via listener.
4. **Riwayat User**:
   - Tabel menampilkan film, showtime, jumlah tiket, total harga, dan waktu booking.
   - Tombol "Delete Booking" tersedia (opsional, jika diimplementasikan).

---

## ⚙️ Setup & Run di NetBeans

### 1. Persiapan lingkungan
- **Java SDK** minimal versi 11.
- **NetBeans IDE** (disarankan versi ≥ 12).
- Pastikan plugin **JavaFX**/Gluon sudah terinstal:
  - Tools → Plugins → Available Plugins → cari `JavaFX` atau `Gluon` → install :contentReference[oaicite:1]{index=1}

### 2. Import dan Build Project
1. Clone atau download proyek:
   ```bash
   git clone https://github.com/icaluwu/Aplikasi-Pemesanan-Ticket-Java.git
