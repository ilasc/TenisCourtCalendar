# Apulum Tenis — Rezervări teren

Aplicație Android (Kotlin + Jetpack Compose) și API Ktor pentru **Clubul Sportiv Apulum Tenis** — 3 terenuri (Teren 1, Teren 2, Acoperit), rezervări 60 / 90 / 120 minute, fără suprapuneri.

## Structură

| Folder | Descriere |
|--------|-----------|
| `app/` | Aplicația Android (minSdk 26) |
| `backend/` | API REST Ktor + H2 |

## Pornire rapidă

### 1. Backend

```powershell
cd backend
.\gradlew.bat run
```

Sau dublu-click: `start-backend.bat` (în rădăcina proiectului).

Verificare: [http://localhost:8080/health](http://localhost:8080/health)

### 2. Android

Deschide proiectul în Android Studio, pornește un emulator și rulează `app`.

- Emulator → API: `http://10.0.2.2:8080/` (setat în `BuildConfig`)
- Dispozitiv fizic → schimbă `API_BASE_URL` în `app/build.gradle.kts` cu IP-ul PC-ului (ex. `http://192.168.1.10:8080/`)

### Conturi demo

| Rol | Email | Parolă |
|-----|-------|--------|
| Client | `andrei@apulum.ro` | `tenis123` |
| Administrator | `catalin@apulum.ro` | `tenis123` |

Administratorul vede toate rezervările pe terenuri (panou dedicat după login).

## Limbi

Română (implicit) și engleză (`values-en/strings.xml`) — urmează limba sistemului.

## Prețuri

| Durată | Preț |
|--------|------|
| 60 min | 80 RON |
| 90 min | 120 RON |
| 120 min | 160 RON |

Program teren: **07:00–23:00** (sloturi la 30 min; ultima rezervare 1h poate începe la 22:00).

## Ecrane

- Login
- Client: Acasă — rezervare, Rezervările mele, Profil / Favorite (placeholder)
- Administrator: Panou rezervări (toate terenurile), navigare Dashboard / Clienți / Statistici (placeholder)

Detalii API: [backend/README.md](backend/README.md)
