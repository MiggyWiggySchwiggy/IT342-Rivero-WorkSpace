import axios from 'axios';

const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api/v1',
    headers: {
        'Content-Type': 'application/json',
    },
});

// Automatically attach the accessToken from your Login.tsx
api.interceptors.request.use((config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
}, (error) => {
    return Promise.reject(error);
});

export default api;

// Fetch the currently authenticated user's profile
export async function fetchCurrentUser() {
    const response = await api.get('/auth/me');
    return response.data.data; // Unwrap ApiResponse envelope
}

// ── Space CRUD (Admin) ──

export async function createSpace(spaceData: Record<string, unknown>) {
    const response = await api.post('/spaces', spaceData);
    return response.data.data;
}

export async function updateSpace(spaceId: string, spaceData: Record<string, unknown>) {
    const response = await api.put(`/spaces/${spaceId}`, spaceData);
    return response.data.data;
}

export async function deleteSpace(spaceId: string) {
    const response = await api.delete(`/spaces/${spaceId}`);
    return response.data;
}

// ── Reservation Management (Admin) ──

export async function fetchAllReservations() {
    const response = await api.get('/reservations/all');
    return response.data.data;
}

export async function adminCancelReservation(reservationId: number) {
    const response = await api.patch(`/reservations/${reservationId}/cancel`);
    return response.data;
}

// ── Availability Slots ──

export async function fetchAvailability(spaceId: string) {
    const response = await api.get(`/spaces/${spaceId}/availability`);
    return response.data.data;
}

export async function saveAvailability(spaceId: string, slots: Record<string, unknown>[]) {
    const response = await api.put(`/spaces/${spaceId}/availability/replace`, slots);
    return response.data.data;
}

// ── Space Bookings (public, no user data) ──

export async function fetchSpaceBookings(spaceId: string) {
    const response = await api.get(`/spaces/${spaceId}/bookings`);
    return response.data.data;
}

// ── Image Upload ──

export async function uploadSpaceImage(spaceId: string, files: FileList) {
    const formData = new FormData();
    for (let i = 0; i < files.length; i++) {
        formData.append('file', files[i]);
    }
    const response = await api.post(`/spaces/${spaceId}/image`, formData, {
        headers: {
            'Content-Type': 'multipart/form-data',
        },
    });
    return response.data.data;
}

// ── Weather API ──

export async function fetchSpaceWeather(spaceId: string) {
    const response = await api.get(`/spaces/${spaceId}/weather`);
    return response.data.data;
}

// ── Geocoding API ──

export async function fetchSpaceCoordinates(spaceId: string) {
    const response = await api.get(`/spaces/${spaceId}/coordinates`);
    return response.data.data;
}