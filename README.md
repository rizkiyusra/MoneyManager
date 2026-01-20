# 💰 MoneyManager - Personal Finance & Asset Tracker

MoneyManager is a modern native Android application designed to track multi-asset finances (Fiat, Crypto, Gold) with an **Offline-First** approach. Built using **Clean Architecture**, **Jetpack Compose**, and **MVVM** principles.

![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-purple?logo=kotlin)
![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-blue?logo=android)
![Architecture](https://img.shields.io/badge/Arch-Clean%20Architecture-green)

## 📱 Key Features
* **Multi-Asset Management:** Manage Bank Accounts (IDR), PayPal (USD), Bitcoin (BTC), and Gold (Gram) in a single dashboard.
* **Realtime Conversion:** Estimate total wealth in base currency (IDR) using Live APIs (CoinGecko & GoldAPI).
* **Offline-First:** Data is securely stored locally (Room Database) with synchronization capabilities when online.
* **Financial Analytics:** Visualize Cashflow and Asset Allocation using Interactive Charts.
* **Secure Backup:** Support for JSON/CSV export and Google Drive integration.
* **Biometric Lock:** Secure your financial data with native biometric authentication (Fingerprint/FaceID)
* **Privacy Mode:** Instantly mask sensitive balances and amounts with a single tap for privacy in public spaces

## 🛠 Tech Stack & Libraries
* **Language:** Kotlin
* **UI Toolkit:** Jetpack Compose (Material Design 3)
* **Architecture:** Clean Architecture (Data, Domain, Presentation) + MVVM
* **Dependency Injection:** Dagger Hilt
* **Local Storage:** Room Database (SQLite)
* **Network:** Retrofit + OkHttp + Moshi
* **Concurrency:** Coroutines + Flow
* **Background Tasks:** WorkManager (Periodic Price Sync)
* **Charts:** Vico (Compose Native Charting)

## 📸 Screenshots
*(To be added once the UI is complete)*

## 👤 Author
Developed by **Rizki Maulana Yusra**