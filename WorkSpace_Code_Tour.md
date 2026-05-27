# WorkSpace — Code Tour Guide
> Use this alongside `WorkSpace_Demo_Guide.md` during **Part 4 — Architecture & Proof**
> Open each file in VS Code beforehand. Hit `Ctrl+G` to jump to a line number during the demo.

---

## 🗂️ STEP 0 — Show the Folder Structure (15 seconds)

**Action:** In VS Code, click the Explorer sidebar. Collapse everything, then expand just the top-level folders.

**What to show:**
```
IT342-Rivero-WorkSpace/
├── backend/     ← Spring Boot (Java)
├── web/         ← React + TypeScript
└── mobile/      ← Android (Kotlin)
```

**Say:**
> "The entire project lives in one repository with three separate components — a Spring Boot backend, a React web frontend, and an Android mobile app. They all share the same REST API."

---

## 📄 FILE 1 — `application.properties`
**Path:** `backend/src/main/resources/application.properties`
**Open in VS Code → `Ctrl+G` → type line number**

| Line | Content | What it shows |
|---|---|---|
| 11–12 | `${DB_USERNAME}` / `${DB_PASSWORD}` | Database creds from env |
| 25 | `${JWT_SECRET}` | JWT signing key from env |
| 30 | `${GOOGLE_CLIENT_ID}` | Google OAuth from env |
| 38 | `smtp.gmail.com` | Gmail SMTP config |
| 46 | `${STRIPE_API_KEY}` | Stripe key from env |

**Say:**
> "All secrets — the database credentials, JWT signing key, Google OAuth keys, Gmail SMTP credentials, and the Stripe API key — are loaded from environment variables at runtime. Nothing sensitive is hardcoded in the source code."

---

## 📄 FILE 2 — `SecurityConfig.java`
**Path:** `backend/src/main/java/edu/cit/rivero/workspace/security/SecurityConfig.java`

### Highlight A — Public vs Protected endpoints (Lines 60–65)
```java
.requestMatchers("/api/v1/auth/register", "/api/v1/auth/login", "/api/v1/auth/google").permitAll()
.requestMatchers("/uploads/**").permitAll()
.requestMatchers(HttpMethod.GET, "/api/v1/spaces/**").permitAll()
.requestMatchers("/api/v1/**").authenticated()
```
**Say:** *"Auth endpoints, uploaded images, and browsing spaces are public. Everything else under `/api/v1/` requires a valid JWT."*

### Highlight B — JWT Filter Chain (Line 71)
```java
.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
```
**Say:** *"Before every request hits any controller, `JwtAuthenticationFilter` validates the Bearer token and sets the security context. Stateless — no sessions."*

### Highlight C — Google OAuth2 Success Handler (Lines 73–103)
```java
.oauth2Login(oauth2 -> oauth2
    .successHandler((request, response, authentication) -> {
        // find or create user in DB
        String token = jwtService.generateToken(user);
        response.sendRedirect(frontendOAuthRedirectUri + "?token=" + token);
    })
)
```
**Say:** *"On Google login success, we find or create the user, generate a JWT, and redirect the browser to the React frontend with the token in the URL."*

---

## 📄 FILE 3 — `ReservationService.java`
**Path:** `backend/src/main/java/edu/cit/rivero/workspace/features/reservation/ReservationService.java`

### Highlight A — Conflict Detection (Lines 62–70)
```java
boolean hasConflict = reservationRepository
    .existsBySpaceIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
        request.getSpaceId(), "CONFIRMED", end, start);

if (hasConflict) {
    throw new BusinessException("BOOK-001", "Schedule conflict: already booked.");
}
```
**Say:** *"A single repository query checks for overlapping confirmed bookings. A conflict throws a `BusinessException` — which `GlobalExceptionHandler` converts into a structured API error."*

### Highlight B — Pricing Formula (Lines 74–79)
```java
long billableHours = Math.max(1, (long) Math.ceil(durationMinutes / 60.0));
BigDecimal subtotal = hourlyRate.multiply(BigDecimal.valueOf(billableHours));
BigDecimal total = subtotal.add(SERVICE_FEE); // SERVICE_FEE = $49.00
```
**Say:** *"Pricing: hourly rate × hours (ceiling, min 1) + a flat $49 service fee."*

### Highlight C — Strategy Pattern Call (Lines 82–85)
```java
boolean paymentSuccess = paymentStrategy.processPayment(
    request.getPaymentMethod(), total.doubleValue());
if (!paymentSuccess) {
    throw new BusinessException("PAY-001", "Payment Declined: " + paymentStrategy.getDeclineReason());
}
```
**Say:** *"This is the **Strategy Pattern**. The service calls `paymentStrategy.processPayment()` without knowing which payment processor is behind it."*

---

## 📄 FILE 4 — `SandboxStripeStrategy.java`
**Path:** `backend/src/main/java/edu/cit/rivero/workspace/features/reservation/strategy/SandboxStripeStrategy.java`

### Highlight A — Interface Implementation (Line 16)
```java
public class SandboxStripeStrategy implements PaymentStrategy {
```
**Say:** *"Concrete Strategy implementation. `ReservationService` depends only on the interface — swapping Stripe for another provider requires zero changes to business logic."*

### Highlight B — Dual Payment Flows (Lines 32–49 vs 51–86)
```java
if (cardNumber.startsWith("pm_")) {
    // Web: Stripe Elements → PaymentMethod ID → PaymentIntent
    PaymentIntent intent = PaymentIntent.create(intentParams);
} else {
    // Android: raw card → Stripe Token → Charge
    Token token = Token.create(tokenParams);
    Charge charge = Charge.create(chargeParams);
}
```
**Say:** *"The strategy handles both payment flows. `pm_*` is a Stripe PaymentMethod ID from the web's Stripe Elements. Raw card data from Android goes through the legacy Token API. The backend absorbs both transparently."*

---

## 📄 FILE 5 — `axiosConfig.ts`
**Path:** `web/src/features/shared/axiosConfig.ts`

### Highlight A — JWT Interceptor (Lines 11–19)
```typescript
api.interceptors.request.use((config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});
```
**Say:** *"This interceptor runs before every API call. It reads the JWT from `localStorage` and attaches it as a Bearer token automatically."*

### Highlight B — Helper Function (Lines 24–27)
```typescript
export async function fetchCurrentUser() {
    const response = await api.get('/auth/me');
    return response.data.data; // Unwrap ApiResponse<T> envelope
}
```
**Say:** *"All API calls are wrapped in typed helper functions. The `ApiResponse<T>` envelope from the backend is unwrapped here so the rest of the app works with clean data objects."*

---

## 📄 FILE 6 — `App.tsx` (Route Guards)
**Path:** `web/src/App.tsx`

### Highlight A — `ProtectedRoute` (Lines 19–23)
```tsx
const ProtectedRoute = ({ children }) => {
    const token = localStorage.getItem('accessToken');
    if (!token) return <Navigate to="/login" replace />;
    return children;
};
```
**Say:** *"No token → redirect to login immediately."*

### Highlight B — `AdminProtectedRoute` (Lines 26–54)
```tsx
fetchCurrentUser()
    .then((user) => {
        if (user.role === 'ROLE_ADMIN') setStatus('authorized');
        else setStatus('unauthorized');
    })
```
**Say:** *"Admin routes call `GET /auth/me` server-side on every navigation. A user can't just edit `localStorage` to gain admin access."*

---

## 📄 FILE 7 — `SessionManager.kt`
**Path:** `mobile/app/src/main/java/edu/cit/rivero/workspace/SessionManager.kt`

### Highlight A — Singleton (Line 10)
```kotlin
object SessionManager {
```
**Say:** *"`object` in Kotlin is the built-in **Singleton** pattern. One instance, app-wide."*

### Highlight B — SharedPreferences (Lines 20–38)
```kotlin
fun saveSession(context, token, role, userId, firstName, lastName, email) {
    context.getSharedPreferences("WorkSpacePrefs", MODE_PRIVATE).edit()
        .putString("JWT_TOKEN", token)
        .putString("USER_ROLE", role)
        .apply()
}
```
**Say:** *"JWT and user info are persisted in `SharedPreferences`. User stays logged in between app restarts."*

### Highlight C — Role Check (Lines 66–69)
```kotlin
fun isAdmin(context: Context): Boolean {
    val r = getRole(context)?.uppercase() ?: ""
    return r == "ADMIN" || r == "ROLE_ADMIN"
}
```
**Say:** *"Handles both `ROLE_ADMIN` and `ADMIN` — resilient to formatting differences."*

---

## 🔌 POSTMAN PROOF — Live API Demo

### Request 1 — Login
- **POST** `http://localhost:8080/api/v1/auth/login`
- Body: `{ "email": "admin@workspace.com", "password": "admin123" }`
- Copy the `accessToken` from the response.

### Request 2 — Authenticated Call (200 OK)
- **GET** `http://localhost:8080/api/v1/reservations/all`
- Header: `Authorization: Bearer <paste token>`

### Request 3 — No Token (401)
- Same request, remove the `Authorization` header.
- Show the `401 Unauthorized` response.

---

## 🃏 Quick Reference Card

| File | Lines | Key Concept |
|---|---|---|
| `application.properties` | 11–12, 25, 30, 38, 46 | No hardcoded secrets |
| `SecurityConfig.java` | 60–65, 71, 73–103 | JWT filter, public routes, OAuth2 |
| `ReservationService.java` | 62–70, 74–79, 82–85 | Conflict check, pricing, Strategy call |
| `SandboxStripeStrategy.java` | 16, 32–49, 51–86 | Strategy Pattern, dual payment flows |
| `axiosConfig.ts` | 11–19, 24–27 | JWT interceptor, API helpers |
| `App.tsx` | 19–23, 26–54 | Route guards, server-side role check |
| `SessionManager.kt` | 10, 20–38, 66–69 | Singleton, SharedPreferences |

---

**Stripe test card:** `4242 4242 4242 4242` · Any future expiry · Any 3-digit CVC

**Admin:** `admin@workspace.com` / `admin123`
