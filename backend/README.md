# Apulum Tenis API

Backend Ktor pentru rezervări terenuri tenis (Clubul Sportiv Apulum Tenis).

## Pornire (Windows)

Din folderul `backend` (nu ai nevoie de Gradle instalat global):

```powershell
cd backend
.\gradlew.bat run
```

Sau dublu-click pe `start-backend.bat` din rădăcina proiectului.

Prima rulare descarcă Gradle automat (1–3 minute).

Server: `http://localhost:8080`

## Utilizatori demo

| Rol | Email | Parolă |
|-----|-------|--------|
| Client | `andrei@apulum.ro` | `tenis123` |
| Administrator | `catalin@apulum.ro` | `tenis123` |

## Endpoints

| Method | Path | Auth |
|--------|------|------|
| POST | `/api/v1/auth/login` | Nu |
| POST | `/api/v1/auth/register` | Nu |
| GET | `/api/v1/courts` | JWT |
| GET | `/api/v1/availability?courtId=teren1&date=2026-05-23&durationMinutes=60` | JWT |
| GET | `/api/v1/reservations` | JWT |
| POST | `/api/v1/reservations` | JWT |
| GET | `/api/v1/admin/reservations?from=2026-05-25&to=2026-06-07` | JWT (admin) |
| GET | `/api/v1/admin/reservations?date=2026-05-23` | JWT (admin) |

## Prețuri (RON)

- 60 min → 80
- 90 min → 120
- 120 min → 160

Program: 07:00–23:00 (închidere), sloturi la 30 min, fără suprapuneri pe același teren.
