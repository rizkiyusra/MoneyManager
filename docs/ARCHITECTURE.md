# 🏗 Architecture & Database Schema

Dokumen ini menjelaskan struktur teknis aplikasi MoneyManager, termasuk pembagian layer (Clean Architecture) dan skema database lengkap.

## 📐 Layered Architecture (Clean Architecture)

Aplikasi ini dibagi menjadi 3 layer utama untuk memastikan pemisahan tanggung jawab (*Separation of Concerns*) dan kemudahan pengujian (*Testability*).

### 1. Presentation Layer (`presentation/`)
* **Focus:** UI & User Interaction.
* **Components:**
    * `Screen` (Composable): Menampilkan data ke layar.
    * `ViewModel`: Mengelola state UI dan menjembatani UI dengan Domain layer.
    * `State`: Data class yang merepresentasikan kondisi UI pada satu waktu.

### 2. Domain Layer (`domain/`)
* **Focus:** Business Logic (Pure Kotlin).
* **Components:**
    * `Model`: Data class murni yang digunakan di seluruh aplikasi.
    * `Repository Interface`: Kontrak (interface) untuk mendefinisikan cara mengakses data.
    * `UseCase`: Unit logika bisnis spesifik (contoh: `AddTransactionUseCase`, `CalculateTotalAssetUseCase`).

### 3. Data Layer (`data/`)
* **Focus:** Data Access & Storage.
* **Components:**
    * `Local`: Room Database (DAO, Entity) untuk penyimpanan offline.
    * `Remote`: Retrofit Service & DTO untuk akses API (CoinGecko, GoldAPI).
    * `RepositoryImpl`: Implementasi interface repository, mengatur logika *Offline-First* (ambil dari DB, sync dengan API).

---

## 🗄 Database Schema (Room)

Berikut adalah detail tabel database. Tipe data uang menggunakan `Double` (REAL) untuk MVP ini demi kecepatan development, namun dibungkus dengan arsitektur yang memungkinkan migrasi ke `BigDecimal` di masa depan jika diperlukan.

### 1. Assets Table
Menyimpan semua aset/akun keuangan (Bank, E-wallet, Crypto, dll).

| Column | Type | Description |
| :--- | :--- | :--- |
| `assetId` | INT (PK) | Auto Generate (Primary Key) |
| `assetName` | TEXT | Nama aset (e.g., Bank BCA, Bitcoin Wallet) |
| `assetType` | TEXT | Enum: BANK, E_WALLET, CRYPTO, GOLD, CASH |
| `currentBalance` | REAL | Saldo saat ini dalam unit asli |
| `balanceUnit` | TEXT | Unit: IDR, USD, BTC, gram |
| `currencySymbol` | TEXT | Symbol: Rp, $, ₿, gr |
| `accountNumber` | TEXT | Nomor rekening (Nullable) |
| `bankName` | TEXT | Nama bank (Nullable) |
| `lastPriceUpdate` | REAL | Harga terbaru per unit dalam IDR (Nullable) |
| `priceSource` | TEXT | Sumber harga: CoinGecko, Manual (Nullable) |
| `isActive` | BOOL | Status aktif (Default: True) |
| `sortOrder` | INT | Urutan tampil di UI (Default: 0) |
| `createdDate` | LONG | Timestamp dibuat |
| `lastModified` | LONG | Timestamp terakhir diubah |

### 2. Transactions Table
Mencatat semua transaksi: income, expense, transfer.
*(Revisi: Menggunakan `categoryId` sebagai Foreign Key sesuai best practice).*

| Column | Type | Description |
| :--- | :--- | :--- |
| `transactionId` | INT (PK) | Auto Generate (Primary Key) |
| `fromAssetId` | INT (FK) | Asset Sumber (Foreign Key ke Assets) |
| `toAssetId` | INT | Asset Tujuan (Nullable, Foreign Key ke Assets) |
| `categoryId` | INT (FK) | Kategori Transaksi (Foreign Key ke Categories) |
| `transactionType` | TEXT | Enum: INCOME, EXPENSE, TRANSFER_IN, TRANSFER_OUT |
| `transactionAmount` | REAL | Jumlah dalam unit aset sumber |
| `transactionCurrency` | TEXT | Currency: IDR, USD, BTC |
| `convertedAmountIDR` | REAL | Nilai dalam IDR untuk laporan |
| `exchangeRate` | REAL | Rate konversi saat transaksi (Default: 1.0) |
| `transactionTitle` | TEXT | Judul: Makan siang, Gaji bulanan |
| `transactionNote` | TEXT | Catatan tambahan (Nullable) |
| `transactionLocation` | TEXT | Lokasi transaksi (Nullable) |
| `receiptImagePath` | TEXT | Path foto struk (Nullable) |
| `transactionDate` | LONG | Timestamp tanggal transaksi |
| `createdDate` | LONG | Timestamp data dibuat |

### 3. Categories Table
Kategori transaksi (bawaan + custom user).

| Column | Type | Description |
| :--- | :--- | :--- |
| `categoryId` | INT (PK) | Auto Generate (Primary Key) |
| `categoryName` | TEXT | Nama kategori (e.g., Food & Drinks) |
| `categoryDescription` | TEXT | Deskripsi kategori (Nullable) |
| `isIncomeCategory` | BOOL | True = Income, False = Expense (Default: False) |
| `categoryColor` | INT | Warna UI (Format ARGB) |
| `categoryIcon` | TEXT | Nama icon atau emoji |
| `isSystemCategory` | BOOL | True = Bawaan, False = Custom (Default: False) |
| `isActive` | BOOL | Status aktif (Default: True) |
| `usageCount` | INT | Counter untuk sorting (Default: 0) |
| `createdDate` | LONG | Timestamp dibuat |

### 4. Budgets Table
Budget planning per kategori per bulan.

| Column | Type | Description |
| :--- | :--- | :--- |
| `budgetId` | INT (PK) | Auto Generate (Primary Key) |
| `budgetCategoryId` | INT (FK) | Foreign Key ke Categories |
| `budgetName` | TEXT | Nama budget (e.g., Food Budget March 2024) |
| `budgetLimit` | REAL | Batas pengeluaran dalam IDR |
| `budgetPeriod` | TEXT | Enum: MONTHLY, WEEKLY, DAILY |
| `budgetMonth` | INT | Bulan (1-12) |
| `budgetYear` | INT | Tahun (e.g., 2024) |
| `currentSpent` | REAL | Total pengeluaran saat ini (Default: 0.0) |
| `isActive` | BOOL | Status aktif (Default: True) |
| `alertThreshold` | REAL | Persentase notifikasi (0.8 = 80%) |
| `createdDate` | LONG | Timestamp dibuat |
| `lastUpdated` | LONG | Timestamp terakhir diupdate |

### 5. Price History Table
Menyimpan riwayat harga untuk crypto/gold/forex.

| Column | Type | Description |
| :--- | :--- | :--- |
| `priceId` | INT (PK) | Auto Generate (Primary Key) |
| `currencyCode` | TEXT | Kode mata uang: BTC, ETH, XAU |
| `priceInIDR` | REAL | Harga dalam IDR |
| `priceInUSD` | REAL | Harga dalam USD |
| `priceSource` | TEXT | Sumber: CoinGecko, GoldAPI, Manual |
| `priceDate` | LONG | Timestamp harga diambil |
| `isLatest` | BOOL | Apakah harga terbaru (Default: False) |

### 6. Transfer Pairs Table
Menyimpan pasangan transfer untuk history dan quick transfer.

| Column | Type | Description |
| :--- | :--- | :--- |
| `pairId` | INT (PK) | Auto Generate (Primary Key) |
| `fromAssetId` | INT (FK) | Asset Sumber (Foreign Key) |
| `toAssetId` | INT (FK) | Asset Tujuan (Foreign Key) |
| `pairName` | TEXT | Nama pasangan (e.g., PayPal → BCA) |
| `lastUsed` | LONG | Timestamp terakhir digunakan |
| `usageCount` | INT | Counter dipakai (Default: 0) |
| `isQuickTransfer` | BOOL | Tampil di quick transfer (Default: False) |
| `createdDate` | LONG | Timestamp dibuat |

### 7. Backup Metadata Table
Metadata untuk backup/restore.

| Column | Type | Description |
| :--- | :--- | :--- |
| `backupId` | INT (PK) | Auto Generate (Primary Key) |
| `backupName` | TEXT | Nama backup |
| `backupPath` | TEXT | Path file backup |
| `backupType` | TEXT | Enum: JSON, CSV, GOOGLE_DRIVE |
| `backupSize` | LONG | Ukuran file (bytes) |
| `totalAssets` | INT | Jumlah aset dibackup |
| `totalTransactions` | INT | Jumlah transaksi dibackup |
| `backupDate` | LONG | Timestamp backup |
| `isAutoBackup` | BOOL | Apakah otomatis (Default: False) |

---

## 🔗 Database Relations (Relasi Antar Tabel)

* **Assets → Transactions**
    * *Relation:* One-to-Many
    * *Logic:* Satu aset bisa memiliki banyak transaksi.
    * *Implementation:* `transactions.fromAssetId` references `assets.assetId`.

* **Categories → Transactions**
    * *Relation:* One-to-Many
    * *Logic:* Satu kategori (misal: "Makan") bisa digunakan di banyak transaksi.
    * *Implementation:* `transactions.categoryId` references `categories.categoryId`.
    * *Improvement:* Menggunakan ID Integer lebih efisien & aman daripada String matching.

* **Categories → Budgets**
    * *Relation:* One-to-Many
    * *Logic:* Satu kategori bisa memiliki beberapa budget (misal budget bulan Maret, April, dst).
    * *Implementation:* `budgets.budgetCategoryId` references `categories.categoryId`.

* **Assets → Transfer Pairs**
    * *Relation:* Many-to-Many
    * *Logic:* Aset bisa menjadi sumber/tujuan di banyak pasangan transfer.
    * *Implementation:* `transfer_pairs.fromAssetId` & `transfer_pairs.toAssetId` reference `assets.assetId`.