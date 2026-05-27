# WorkSpace — IT342 Systems Integration and Architecture

A full-stack workspace booking platform with a Spring Boot backend, React web frontend, and Android mobile app.

---

## Prerequisites

| Tool | Version | Download |
|---|---|---|
| Java JDK | 17 or 23 | https://adoptium.net |
| Maven | (included via `mvnw`) | — |
| MySQL | 8.0 | https://dev.mysql.com/downloads/mysql/ |
| Node.js | 18+ | https://nodejs.org |
| Android Studio | Latest | https://developer.android.com/studio |

---

## Quick Start (No Configuration Needed)

The project includes safe defaults for local development. If your MySQL runs with **username `root` and no password**, you can start immediately with no extra setup.

### 1. Clone the repository

```bash
git clone https://github.com/MiggyWiggySchwiggy/IT342-Rivero-WorkSpace.git
cd IT342-Rivero-WorkSpace
```

### 2. Start the Backend

```bash
cd backend
./mvnw spring-boot:run        # Mac/Linux
.\mvnw.cmd spring-boot:run    # Windows
```

- Starts on **http://localhost:8080**
- MySQL database `workspace_db` is created automatically on first run
- Default admin account is seeded: `admin@workspace.com` / `admin123`

### 3. Start the Web Frontend

```bash
cd web
npm install
npm run dev
```

- Opens at **http://localhost:5173**

### 4. Run the Android App

- Open the `mobile/` folder in Android Studio
- Run on an emulator (the app connects to the backend at `10.0.2.2:8080` automatically)

---

## Configuration

### If your MySQL uses a different username/password

Create `backend/src/main/resources/application-local.properties` (this file is gitignored and won't be committed):

```properties
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
```

### Full configuration reference

All settings below override the defaults in `application.properties`:

```properties
# Database (defaults to root with no password)
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password

# JWT signing key — generate any Base64 string of at least 32 bytes
application.security.jwt.secret-key=YOUR_OWN_BASE64_JWT_SECRET

# Google OAuth2 (required for Google Sign-In feature)
spring.security.oauth2.client.registration.google.client-id=YOUR_GOOGLE_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_GOOGLE_CLIENT_SECRET

# Gmail SMTP (required for email notifications)
spring.mail.username=your_email@gmail.com
spring.mail.password=your_gmail_app_password

# Stripe (required for payments — use a Stripe test key)
stripe.api.key=sk_test_your_stripe_test_key
```

> **Note:** Google OAuth2, email notifications, and Stripe payments require real API credentials.  
> Email/password login and workspace browsing work without any credentials.

---

## Default Credentials (Development)

| Account | Email | Password |
|---|---|---|
| Admin | `admin@workspace.com` | `admin123` |

**Stripe test card:** `4242 4242 4242 4242` · Any future expiry · Any 3-digit CVC

---

## Project Structure

```
IT342-Rivero-WorkSpace/
├── backend/          # Spring Boot 3 + MySQL REST API (port 8080)
├── web/              # React 18 + TypeScript web app (port 5173)
├── mobile/           # Android (Kotlin) mobile app
├── WorkSpace_Demo_Guide.md     # Presentation script
└── WorkSpace_Code_Tour.md      # Code walkthrough guide
```

---

## Architecture Overview

- **Backend:** Layered architecture (Controller → Service → Repository). JWT-based stateless authentication. Google OAuth2 for web browser login. Strategy Pattern for payment processing (Stripe).
- **Web:** React Router with protected routes and admin route guards. Axios interceptor for automatic JWT attachment.
- **Mobile:** Retrofit2 HTTP client. SharedPreferences-based session management (Singleton pattern).
- **Integration:** Open-Meteo API (weather), Nominatim/OpenStreetMap (geocoding), Stripe (payments), Gmail SMTP (email notifications).

---

## API Endpoints (Key)

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/v1/auth/register` | Public | Register user |
| POST | `/api/v1/auth/login` | Public | Login, returns JWT |
| GET | `/api/v1/spaces` | Public | List all spaces |
| POST | `/api/v1/reservations/checkout` | User | Book a space |
| GET | `/api/v1/reservations/all` | Admin | All reservations |
| POST | `/api/v1/spaces` | Admin | Create space |

---

## Built With

- Spring Boot 3.5 · Spring Security · Spring Data JPA · MySQL
- React 18 · TypeScript · Vite · Axios · Stripe Elements
- Android (Kotlin) · Retrofit2 · OkHttp
- Google OAuth2 · Stripe API · Open-Meteo · Nominatim · Gmail SMTP
