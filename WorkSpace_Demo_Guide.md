# WorkSpace — Final Project Presentation Demo Guide
> IT342 Systems Integration and Architecture | Personal Checklist & Script

---

## Before You Start Recording

**Make sure all of these are running:**
- [ ] MySQL is running and `workspace_db` is up
- [ ] Backend started via `./mvnw.cmd spring-boot:run` → listening on `http://localhost:8080`
- [ ] Web frontend started via `npm run dev` → listening on `http://localhost:5173`
- [ ] Android emulator is running (backend accessible at `10.0.2.2:8080`)
- [ ] Postman is open with your collection ready (for architecture proof)
- [ ] Browser is on the WorkSpace landing page, logged out
- [ ] A test Stripe card number is ready: `4242 4242 4242 4242`, any future expiry, any CVC

**Keep a notepad with:**
- Admin credentials: `admin@workspace.com` / `admin123`
- A test user email/password you registered earlier
- Stripe test card: `4242 4242 4242 4242`

---

## Presentation Script (5–10 Minutes)

---

### PART 1 — Self-Introduction (~30 seconds)

Say something like:

> "Hi, I'm [Your Name], from [Course and Section]. My final project for IT342 Systems Integration and Architecture is called **WorkSpace** — a full-stack workspace booking platform with a Spring Boot backend, a React web frontend, and an Android mobile app."

---

### PART 2 — System Introduction (~1 minute)

Talking points to hit:

- **Purpose:** WorkSpace lets users browse co-working spaces, check availability, and reserve them with payment — all in real time.
- **Problem it solves:** Finding and booking a workspace is usually fragmented. WorkSpace puts discovery, scheduling, and payment in one place.
- **Intended users:** Two types — regular users who search and book workspaces, and admins who manage the listings, availability, and reservations.
- **Overall goal:** Demonstrate a fully integrated system using modern architecture concepts — REST APIs, JWT security, third-party service integration, and a multi-client setup (web + mobile).

*While saying this, you can stay on the Landing Page of the web app so there's something visual on screen.*

---

### PART 3 — Main Features (~1–1.5 minutes)

Briefly list these — you don't need to demo each one yet, just name and describe them:

1. **User Authentication & Authorization** — Email/password login, Google OAuth2 login, JWT-secured sessions, and role-based access (User vs Admin).
2. **Workspace Browsing** — Users can browse available workspaces with images, descriptions, amenities, and pricing.
3. **Availability & Booking** — Users can view per-day availability slots and make reservations with a defined start and end time.
4. **Stripe Payment Integration** — Payment is processed through Stripe at checkout. The web app uses Stripe Elements; the Android app sends card details for tokenization.
5. **Admin Dashboard** — Admins can create, update, and delete workspaces; manage availability slots; and cancel reservations with a refund status.
6. **Image Upload** — Admins can upload multiple images per workspace, served from the backend's uploads directory.
7. **Email Notifications** — Transactional emails are sent on registration, login, and booking confirmation via Gmail SMTP.
8. **External API Integration** — Real-time weather from Open-Meteo and geocoding from Nominatim (OpenStreetMap) are shown on the space detail page.
9. **Multi-client Support** — Both a web app (React) and an Android mobile app share the same backend API.

---

### PART 4 — Architecture, Component Interaction & Proof (~2 minutes)

**Describe the architecture first (show your folder structure on screen):**

> "WorkSpace follows a **Client-Server architecture** with a **Layered (Service Layer) architecture** on the backend, and a **REST API** as the contract between all clients."

**Walk through the data flow with a concrete example:**

> "Here's how a booking works end-to-end:
> 1. The React frontend collects the user's time selection and Stripe payment method ID.
> 2. It calls `POST /api/v1/reservations/checkout` via Axios, attaching the JWT from localStorage in the Authorization header.
> 3. The `JwtAuthenticationFilter` on the backend validates the token and sets the security context.
> 4. The `ReservationController` receives the request and delegates to `ReservationService`.
> 5. `ReservationService` checks for booking conflicts, calculates the total price using the formula `(hourlyRate × hours) + $49 service fee`, then calls `SandboxStripeStrategy.processPayment()` — which is the **Strategy Pattern** in action.
> 6. On payment success, a `Reservation` entity is saved to MySQL, and an async confirmation email is sent via `EmailService`.
> 7. The response is wrapped in `ApiResponse<T>` and returned to the frontend."

**Proof of implementation — show these on screen:**

- [ ] Open your **project folder structure** in VS Code or File Explorer: show `backend/`, `web/`, `mobile/` as separate components in one repo.
- [ ] Show `SecurityConfig.java` — point to the JWT filter chain and the public vs secured endpoints.
- [ ] Show `ReservationService.java` — point to the conflict check and the `PaymentStrategy` call.
- [ ] Show `SandboxStripeStrategy.java` — explain the Strategy Pattern: the service doesn't care which payment processor is used.
- [ ] Show `axiosConfig.ts` — point to the Bearer token interceptor that attaches JWT to every request.
- [ ] Open **Postman**: fire `POST /api/v1/auth/login` with your test credentials, copy the JWT, then call `GET /api/v1/reservations/my` with it as the Bearer token. Show a `401` if you remove the token.
- [ ] Show `application.properties` — mention that all secrets (DB, JWT, Stripe, Google, Gmail) are loaded from environment variables, not hardcoded.

**Design Patterns to mention:**
- **Strategy Pattern** — `PaymentStrategy` / `SandboxStripeStrategy`
- **Repository Pattern** — Spring Data JPA repos
- **Service Layer** — `AuthService`, `SpaceService`, `ReservationService`
- **Filter Chain** — `JwtAuthenticationFilter`
- **Route Guard** — `ProtectedRoute` and `AdminProtectedRoute` in React
- **Singleton** — `SessionManager` and `ApiClient` in Android

---

### PART 5 — System Demonstration (~3–4 minutes)

Follow this exact flow for the demo. Narrate every action clearly.

---

#### 5A — Web App Demo

**Step 1: Landing Page**
- Show the landing page. Briefly mention the hero section, "How It Works" steps.
- Say: *"The landing page is publicly accessible and serves as the entry point."*

**Step 2: Register a new user**
- Click Register. Fill in a name, email, and password. Submit.
- Say: *"On registration, the backend creates the user with `ROLE_USER`, hashes the password with BCrypt, and sends a welcome email via Gmail SMTP."*
- You should be redirected to the dashboard automatically.

**Step 3: Browse workspaces**
- On the dashboard, show the list of workspace cards.
- Say: *"These are fetched from `GET /api/v1/spaces`, which is a public endpoint — no authentication required to list spaces."*

**Step 4: View a space detail**
- Click on a workspace card.
- Show: images, description, amenities, the weather widget, the map/geocoding display, availability calendar, and upcoming bookings.
- Say: *"The weather comes from Open-Meteo API and the map coordinates from Nominatim (OpenStreetMap). Both are called from the backend when this page loads."*

**Step 5: Make a booking**
- Click "Book Now" or the checkout button.
- Select a start and end time.
- Enter Stripe test card: `4242 4242 4242 4242`, any future expiry, any CVC.
- Submit.
- Say: *"The frontend uses Stripe Elements, which tokenizes the card in the browser and sends only a `pm_*` PaymentMethod ID to our backend — we never touch raw card data on our server. The backend's `SandboxStripeStrategy` processes the charge."*
- After success, show the booking in the Reservations page.
- Say: *"A confirmation email is also sent asynchronously — the response doesn't wait for the email to finish sending."*

**Step 6: Admin login**
- Log out. Log in as `admin@workspace.com` / `admin123`.
- Say: *"The `AdminProtectedRoute` calls `GET /auth/me` on every navigation to verify role — it doesn't rely solely on the stored role in localStorage."*

**Step 7: Admin Dashboard**
- Show the admin dashboard. Navigate to Workspace Manager.
- Create or edit a workspace. Upload an image.
- Say: *"Image files are stored in the backend's `uploads/` directory and served publicly via `/uploads/**`. The image URLs are saved as a comma-separated string in the database."*

**Step 8: Manage Availability**
- Go to Availability Manager.
- Show adding or replacing availability slots for a space (day of week, start time, end time).
- Say: *"Availability is stored per space with day-of-week and time ranges. The `PUT /availability/replace` endpoint atomically replaces all slots for a space."*

**Step 9: Cancel a Reservation**
- Go to Reservation Manager.
- Cancel the booking you made in Step 5.
- Say: *"Cancellation sets status to `CANCELLED` and payment status to `REFUNDED`. Only admins can cancel reservations."*

---

#### 5B — Mobile App Demo

- Switch to the Android emulator.

**Step 1: Welcome Screen**
- Show the Welcome screen with Login, Register, and Google Sign-In options.

**Step 2: Login**
- Log in with the test user credentials.
- Say: *"The mobile app uses Retrofit2 to call the same `POST /api/v1/auth/login` endpoint as the web app. The JWT is stored in SharedPreferences via `SessionManager`."*

**Step 3: Browse and View a Space**
- Show the Dashboard with space cards.
- Tap a space to open SpaceDetailActivity.
- Say: *"The detail screen calls multiple endpoints: space details, availability, upcoming bookings, weather, and coordinates — all from the same backend."*

**Step 4: Make a Booking on Mobile**
- Tap Book / Go to BookingActivity.
- Enter card details and book.
- Say: *"On Android, the app uses the legacy Stripe token flow — card details are sent to Stripe's Token API to get a token, which is then sent to our backend. This is handled differently from the web Stripe Elements flow, but the backend's Strategy Pattern lets it handle both."*

**Step 5: Admin on Mobile (optional, if time allows)**
- Log in as admin on mobile.
- Show AdminDashboardActivity — tabs for Spaces, Reservations, Availability.

---

## Closing (~15 seconds)

> "WorkSpace demonstrates a fully integrated system — a secured REST API backend, a React web client, and an Android mobile app, all connected through JWT authentication, third-party payment and email services, and real-time external API data. Thank you."

---

## Things to Avoid / Watch Out For

- **Don't rush the demo.** Narrate every click — the instructor needs to hear what you're doing and why.
- **Weather is always Cebu City** — that's intentional for the demo. You can briefly mention it's a known simplification.
- **Service fee is fixed at $49** — just state it as part of the pricing model, it won't look wrong.
- **Don't show or say your real Stripe key, Gmail password, or JWT secret** — use env var names only (`STRIPE_API_KEY`, etc.).
- **If the Google OAuth login fails during the demo**, fall back to email/password login and mention that Google OAuth is also implemented.
- **If Postman calls fail**, double-check the Bearer token is pasted correctly and the backend is running.

---

## Time Budget

| Section | Target Time |
|---|---|
| Self-Introduction | ~30 sec |
| System Introduction | ~1 min |
| Main Features | ~1 min |
| Architecture & Proof | ~2 min |
| Demo (Web) | ~3 min |
| Demo (Mobile) | ~1.5 min |
| Closing | ~15 sec |
| **Total** | **~9 min** |
